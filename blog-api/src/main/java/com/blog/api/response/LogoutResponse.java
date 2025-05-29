package com.blog.api.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogoutResponse {
    private boolean success;
    private String message;
    private String redirectUrl;
} 