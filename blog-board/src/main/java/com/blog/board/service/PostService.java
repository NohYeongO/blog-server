package com.blog.board.service;

import com.blog.board.exception.PostNotFoundException;
import com.blog.board.domain.Category;
import com.blog.board.domain.Post;
import com.blog.board.dto.*;
import com.blog.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;

    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto) {
        boolean isPublished = requestDto.getPublished() != null ? requestDto.getPublished() : true;
        return PostResponseDto.fromEntity(
                    postRepository.save(Post.builder()
                        .title(requestDto.getTitle())
                        .content(requestDto.getContent())
                        .published(isPublished)
                        .category(categoryService.findOrCreateCategory(requestDto.getCategoryName()))
                        .build()
                    ));
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto requestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("게시글을 찾을 수 없습니다 - ID: {}", postId);
                    return new PostNotFoundException();
                });
        post.update(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getPublished() != null ? requestDto.getPublished() : post.isPublished(),
                categoryService.findOrCreateCategory(requestDto.getCategoryName())
        );
        return PostResponseDto.fromEntity(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("게시글을 찾을 수 없습니다 - ID: {}", postId);
                    return new PostNotFoundException();
                });

        postRepository.delete(post);
        log.info("게시글 삭제 완료 - ID: {}, 제목: {}", postId, post.getTitle());
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("게시글을 찾을 수 없습니다 - ID: {}", postId);
                    return new PostNotFoundException();
                });
        return PostResponseDto.fromEntity(post);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<PostSimpleResponseDto> getPosts(String categoryName, Pageable pageable, boolean isAdmin) {
        Pageable sortedPageable = pageable;
        if (pageable.getSort().isUnsorted()) {
            sortedPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdDate")
            );
        }
        Category category = null;
        if (StringUtils.hasText(categoryName) && !"all".equalsIgnoreCase(categoryName)) {
            category = categoryService.findOrCreateCategory(categoryName);
        }
        Page<Post> posts;
        if (isAdmin) {
            if (category != null) {
                posts = postRepository.findByCategoryOrderByCreatedDateDesc(category, sortedPageable);
            } else {
                posts = postRepository.findAllByOrderByCreatedDateDesc(sortedPageable);
            }
        } else {
            if (category != null) {
                posts = postRepository.findByCategoryAndPublishedTrueOrderByCreatedDateDesc(category, sortedPageable);
            } else {
                posts = postRepository.findByPublishedTrueOrderByCreatedDateDesc(sortedPageable);
            }
        }
        return new PageResponseDto<>(posts.map(PostSimpleResponseDto::fromEntity));
    }
}
