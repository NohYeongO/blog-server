package com.blog.board.service;

import com.blog.board.domain.Category;
import com.blog.board.domain.Post;
import com.blog.board.dto.PageResponseDto;
import com.blog.board.dto.PostRequestDto;
import com.blog.board.dto.PostResponseDto;
import com.blog.board.dto.PostSimpleResponseDto;
import com.blog.board.exception.PostNotFoundException;
import com.blog.board.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService 단위 테스트")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private OAuth2User principal;

    @InjectMocks
    private PostService postService;

    private Post post;
    private Category category;
    private PostRequestDto postRequestDto;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .categoryId(1L)
                .categoryName("개발 일지")
                .build();

        post = Post.builder()
                .postId(1L)
                .title("테스트 게시글")
                .content("테스트 내용")
                .published(true)
                .category(category)
                .build();

        postRequestDto = PostRequestDto.builder()
                .title("새 게시글")
                .content("새 내용")
                .categoryName("개발 일지")
                .published(true)
                .build();
    }

    @Test
    @DisplayName("게시글 수정 - 성공")
    void updatePost_Success() {
        // given
        Long postId = 1L;
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(categoryService.findOrCreateCategory("개발 일지")).willReturn(category);

        // when
        PostResponseDto result = postService.updatePost(postId, postRequestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("새 게시글");
        verify(postRepository).findById(postId);
        verify(categoryService).findOrCreateCategory("개발 일지");
    }

    @Test
    @DisplayName("게시글 수정 - published null일 때 기존 값 유지")
    void updatePost_PublishedNullKeepOriginal() {
        // given
        Long postId = 1L;
        PostRequestDto requestWithNullPublished = PostRequestDto.builder()
                .title("수정된 게시글")
                .content("수정된 내용")
                .categoryName("개발 일지")
                .published(null)
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(categoryService.findOrCreateCategory("개발 일지")).willReturn(category);

        // when
        PostResponseDto result = postService.updatePost(postId, requestWithNullPublished);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("수정된 게시글");
        verify(postRepository).findById(postId);
        verify(categoryService).findOrCreateCategory("개발 일지");
    }

    @Test
    @DisplayName("게시글 수정 - 존재하지 않는 게시글로 예외 발생")
    void updatePost_PostNotFound_ThrowsException() {
        // given
        Long postId = 999L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.updatePost(postId, postRequestDto))
                .isInstanceOf(PostNotFoundException.class);

        verify(postRepository).findById(postId);
        verify(categoryService, never()).findOrCreateCategory(anyString());
    }

    @Test
    @DisplayName("게시글 삭제 - 성공")
    void deletePost_Success() {
        // given
        Long postId = 1L;
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        postService.deletePost(postId);

        // then
        verify(postRepository).findById(postId);
        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("게시글 삭제 - 존재하지 않는 게시글로 예외 발생")
    void deletePost_PostNotFound_ThrowsException() {
        // given
        Long postId = 999L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.deletePost(postId))
                .isInstanceOf(PostNotFoundException.class);

        verify(postRepository).findById(postId);
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    @DisplayName("게시글 단건 조회 - 성공")
    void getPostById_Success() {
        // given
        Long postId = 1L;
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        PostResponseDto result = postService.getPostById(postId);

        // then
        assertThat(result.getTitle()).isEqualTo("테스트 게시글");
        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("게시글 단건 조회 - 존재하지 않는 게시글로 예외 발생")
    void getPostById_PostNotFound_ThrowsException() {
        // given
        Long postId = 999L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.getPostById(postId))
                .isInstanceOf(PostNotFoundException.class);

        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("게시글 목록 조회 - 관리자, 카테고리 없음")
    void getPosts_AdminNoCategory() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(post));
        given(postRepository.findAllByOrderByCreatedDateDesc(any(Pageable.class))).willReturn(postPage);

        // when
        PageResponseDto<PostSimpleResponseDto> result = postService.getPosts(null, pageable, true);

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(postRepository).findAllByOrderByCreatedDateDesc(any(Pageable.class));
        verify(categoryService, never()).findOrCreateCategory(anyString());
    }

    @Test
    @DisplayName("게시글 목록 조회 - 관리자, 특정 카테고리")
    void getPosts_AdminWithCategory() {
        // given
        String categoryName = "개발 일지";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(post));
        
        given(categoryService.findOrCreateCategory(categoryName)).willReturn(category);
        given(postRepository.findByCategoryOrderByCreatedDateDesc(eq(category), any(Pageable.class))).willReturn(postPage);

        // when
        PageResponseDto<PostSimpleResponseDto> result = postService.getPosts(categoryName, pageable, true);

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(categoryService).findOrCreateCategory(categoryName);
        verify(postRepository).findByCategoryOrderByCreatedDateDesc(eq(category), any(Pageable.class));
    }

    @Test
    @DisplayName("게시글 목록 조회 - 일반 사용자, 카테고리 없음 (발행된 글만)")
    void getPosts_PublicNoCategory() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(post));
        given(postRepository.findByPublishedTrueOrderByCreatedDateDesc(any(Pageable.class))).willReturn(postPage);

        // when
        PageResponseDto<PostSimpleResponseDto> result = postService.getPosts(null, pageable, false);

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(postRepository).findByPublishedTrueOrderByCreatedDateDesc(any(Pageable.class));
        verify(categoryService, never()).findOrCreateCategory(anyString());
    }

    @Test
    @DisplayName("게시글 목록 조회 - 일반 사용자, 특정 카테고리 (발행된 글만)")
    void getPosts_PublicWithCategory() {
        // given
        String categoryName = "개발 일지";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(post));
        
        given(categoryService.findOrCreateCategory(categoryName)).willReturn(category);
        given(postRepository.findByCategoryAndPublishedTrueOrderByCreatedDateDesc(eq(category), any(Pageable.class))).willReturn(postPage);

        // when
        PageResponseDto<PostSimpleResponseDto> result = postService.getPosts(categoryName, pageable, false);

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(categoryService).findOrCreateCategory(categoryName);
        verify(postRepository).findByCategoryAndPublishedTrueOrderByCreatedDateDesc(eq(category), any(Pageable.class));
    }

    @Test
    @DisplayName("게시글 목록 조회 - 'all' 카테고리는 전체 조회")
    void getPosts_AllCategory() {
        // given
        String categoryName = "all";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(post));
        given(postRepository.findAllByOrderByCreatedDateDesc(any(Pageable.class))).willReturn(postPage);

        // when
        PageResponseDto<PostSimpleResponseDto> result = postService.getPosts(categoryName, pageable, true);

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(postRepository).findAllByOrderByCreatedDateDesc(any(Pageable.class));
        verify(categoryService, never()).findOrCreateCategory(anyString());
    }

    @Test
    @DisplayName("게시글 목록 조회 - 빈 카테고리명은 전체 조회")
    void getPosts_EmptyCategory() {
        // given
        String categoryName = "";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(post));
        given(postRepository.findAllByOrderByCreatedDateDesc(any(Pageable.class))).willReturn(postPage);

        // when
        PageResponseDto<PostSimpleResponseDto> result = postService.getPosts(categoryName, pageable, true);

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(postRepository).findAllByOrderByCreatedDateDesc(any(Pageable.class));
        verify(categoryService, never()).findOrCreateCategory(anyString());
    }

    @Test
    @DisplayName("게시글 목록 조회 - 정렬되지 않은 Pageable시 기본 정렬 적용")
    void getPosts_UnsortedPageable() {
        // given
        Pageable unsortedPageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(post));
        given(postRepository.findAllByOrderByCreatedDateDesc(any(Pageable.class))).willReturn(postPage);

        // when
        PageResponseDto<PostSimpleResponseDto> result = postService.getPosts(null, unsortedPageable, true);

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(postRepository).findAllByOrderByCreatedDateDesc(any(Pageable.class));
    }
}
