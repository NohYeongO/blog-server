package com.blog.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "인증 코드가 필요합니다.")
    private String code;

    @NotBlank(message = "리다이렉트 URI가 필요합니다.")
    private String redirectUri;
}
