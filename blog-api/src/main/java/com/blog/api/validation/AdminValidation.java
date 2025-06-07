package com.blog.api.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class AdminValidation {
    private final String adminGithubId;
    public AdminValidation(@Value("${blog.admin-github-id}") String adminGithubId) {
        this.adminGithubId = adminGithubId;
    }

    public boolean isAdminUser(OAuth2User principal) {
        return principal != null && principal.getAttribute("login").equals(adminGithubId);
    }
}
