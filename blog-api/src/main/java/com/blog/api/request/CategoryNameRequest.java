package com.blog.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryNameRequest {
    @NotBlank(message = "카테고리 이름을 입력해주세요.")
    private String name;
}
