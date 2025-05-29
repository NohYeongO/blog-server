package com.blog.api.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private boolean authenticated;
    private String githubId;
    private String name;
    private String role;
    private String message;
}
