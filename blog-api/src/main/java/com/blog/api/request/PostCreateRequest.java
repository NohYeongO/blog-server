package com.blog.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostCreateRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 255, message = "제목은 255자를 넘을 수 없습니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    private Boolean published;

    @NotBlank(message = "카테고리 이름을 입력해주세요.")
    private String categoryName;
}
