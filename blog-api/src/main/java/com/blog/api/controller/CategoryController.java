package com.blog.api.controller;

import com.blog.api.exception.AccessDeniedException;
import com.blog.api.mapper.CategoryMapper;
import com.blog.api.request.CategoryNameRequest;
import com.blog.api.response.CategoryResponse;
import com.blog.board.service.CategoryService;
import com.blog.login.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://nohyeongo.github.io", allowCredentials = "true")
public class CategoryController {

    private final CategoryService categoryService;
    private final LoginService loginService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryMapper.toResponseList(categoryService.findAllCategories()));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryNameRequest request,
                                                           @AuthenticationPrincipal OAuth2User principal) {
        validateAdminAccess(principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryMapper.toResponse(categoryService.createCategory(request.getName())));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long categoryId,
                                                           @RequestBody @Valid CategoryNameRequest request,
                                                           @AuthenticationPrincipal OAuth2User principal) {
        validateAdminAccess(principal);
        return ResponseEntity.ok(categoryMapper.toResponse(categoryService.updateCategory(categoryId, request.getName())));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId,
                                               @AuthenticationPrincipal OAuth2User principal) {
        validateAdminAccess(principal);
        categoryService.deleteCategory(categoryId);
        log.info("카테고리 삭제 API 호출 성공 - ID: {}", categoryId);
        return ResponseEntity.noContent().build();
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
