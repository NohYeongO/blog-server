package com.blog.api.controller;

import com.blog.api.validation.AdminValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AdminValidation adminValidation;

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            log.debug("🔓 인증되지 않은 사용자 - /api/auth/user 요청");
            return ResponseEntity.ok(Map.of(
                "authenticated", false
            ));
        }
        
        String githubId = principal.getAttribute("login");
        String name = principal.getAttribute("name");
        String avatarUrl = principal.getAttribute("avatar_url");
        boolean isAdmin = adminValidation.isAdminUser(principal);
        
        log.info("✅ 사용자 정보 조회 - GitHub ID: {}, Admin: {}", githubId, isAdmin);
        
        return ResponseEntity.ok(Map.of(
            "authenticated", true,
            "githubId", githubId != null ? githubId : "",
            "name", name != null ? name : "",
            "avatarUrl", avatarUrl != null ? avatarUrl : "",
            "role", isAdmin ? "ADMIN" : "USER"
        ));
    }
} 