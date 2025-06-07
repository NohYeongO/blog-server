package com.blog.api.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String imageUrl;
    private boolean published;
    private CategoryResponse category;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
