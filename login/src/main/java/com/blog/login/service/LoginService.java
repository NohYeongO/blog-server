package com.blog.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final String adminGithubId;

    public LoginService(@Value("${blog.admin-github-id}") String adminGithubId) {
        this.adminGithubId = adminGithubId;
    }

    public boolean isAdmin(OAuth2User user) {
        String githubId = user.getAttribute("login");
        return adminGithubId.equals(githubId);
    }
}
