package com.blog.api.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostSummaryResponse {
    private Long id;
    private String title;
    private String author;
    private String content;
    private boolean published;
    private CategoryResponse category;
    private LocalDateTime createdDate;
}
