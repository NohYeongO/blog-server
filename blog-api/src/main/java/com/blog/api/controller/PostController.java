package com.blog.api.controller;

import com.blog.api.exception.AccessDeniedException;
import com.blog.api.mapper.PageMapper;
import com.blog.api.mapper.PostMapper;
import com.blog.api.request.PostCreateRequest;
import com.blog.api.request.PostUpdateRequest;
import com.blog.api.response.PageResponse;
import com.blog.api.response.PostResponse;
import com.blog.api.response.PostSummaryResponse;
import com.blog.board.dto.PageResponseDto;
import com.blog.board.dto.PostSimpleResponseDto;
import com.blog.board.service.PostService;
import com.blog.login.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://nohyeongo.github.io", allowCredentials = "true")
public class PostController {

    private final PostService postService;
    private final LoginService loginService;
    private final PostMapper postMapper;
    private final PageMapper pageMapper;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody @Valid PostCreateRequest request,
                                                   @AuthenticationPrincipal OAuth2User principal) {
        validateAdminAccess(principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(postMapper.toResponse(postService.createPost(postMapper.toRequestDto(request), principal)));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId,
                                                   @RequestBody @Valid PostUpdateRequest request,
                                                   @AuthenticationPrincipal OAuth2User principal) {
        validateAdminAccess(principal);
        return ResponseEntity.ok(postMapper.toResponse(postService.updatePost(postId, postMapper.toRequestDto(request))));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal OAuth2User principal) {
        validateAdminAccess(principal);
        postService.deletePost(postId);
        log.info("게시글 삭제 API 호출 성공 - ID: {}", postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(postMapper.toResponse(postService.getPostById(postId)));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostSummaryResponse>> getPosts(@RequestParam(required = false) String categoryName,
                                                                      @PageableDefault(size = 10) Pageable pageable,
                                                                      @AuthenticationPrincipal OAuth2User principal) {
        boolean isAdmin = isAdminUser(principal);
        PageResponseDto<PostSimpleResponseDto> pageResponseDto = postService.getPosts(categoryName, pageable, isAdmin);
        List<PostSummaryResponse> content = postMapper.toSummaryResponseList(pageResponseDto.getContent());
        return ResponseEntity.ok(pageMapper.toResponse(pageResponseDto, content));
    }

    private void validateAdminAccess(OAuth2User principal) {
        if (!isAdminUser(principal)) {
            throw new AccessDeniedException();
        }
    }

    private boolean isAdminUser(OAuth2User principal) {
        return principal != null && loginService.isAdmin(principal);
    }
}
