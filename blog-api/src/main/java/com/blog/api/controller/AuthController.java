package com.blog.api.controller;

import com.blog.api.response.LoginResponse;
import com.blog.api.response.LogoutResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "https://nohyeongo.github.io", allowCredentials = "true")
public class AuthController {

    @GetMapping("/user")
    public ResponseEntity<Void> redirectWithUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        String githubId = principal.getAttribute("login");
        String name = principal.getAttribute("name");
        String role = "ADMIN";

        URI redirectUri = UriComponentsBuilder.fromUriString("http://localhost:8000")
                .queryParam("githubId", githubId)
                .queryParam("name", name)
                .queryParam("role", role)
                .build()
                .encode()
                .toUri();

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectUri)
                .build();
    }

    @GetMapping("/login-error")
    public ResponseEntity<LoginResponse> loginError(@RequestParam(required = false) String reason) {
        String errorMessage;
        if ("not_admin".equals(reason)) {
            errorMessage = "관리자 권한이 없습니다. 관리자만 로그인할 수 있습니다.";
        } else {
            errorMessage = "로그인에 실패했습니다. GitHub OAuth 인증 중 오류가 발생했습니다.";
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(LoginResponse.builder()
                .authenticated(false)
                .githubId(null)
                .name(null)
                .role(null)
                .message(errorMessage)
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout() {
        return ResponseEntity.ok(LogoutResponse.builder()
                .success(true)
                .message("로그아웃되었습니다.")
                .redirectUrl("/")
                .build());
    }

    @GetMapping("/logout-success")
    public ResponseEntity<LogoutResponse> logoutSuccess() {
        return ResponseEntity.ok(LogoutResponse.builder()
                .success(true)
                .message("성공적으로 로그아웃되었습니다.")
                .redirectUrl("/")
                .build());
    }
}
