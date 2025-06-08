package com.blog.board.dto;

import com.blog.board.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSimpleResponseDto {
    private Long id;
    private String title;
    private String author;
    private String content;
    private boolean published;
    private CategoryDto category;
    private LocalDateTime createdDate;

    public static PostSimpleResponseDto fromEntity(Post post) {
        return PostSimpleResponseDto.builder()
                .id(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .published(post.isPublished())
                .category(CategoryDto.fromEntity(post.getCategory()))
                .createdDate(post.getCreatedDate())
                .build();
    }
}
