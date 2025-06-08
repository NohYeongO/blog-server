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
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String author;
    private boolean published;
    private CategoryDto category;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static PostResponseDto fromEntity(Post post) {
        return PostResponseDto.builder()
                .id(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .published(post.isPublished())
                .category(CategoryDto.fromEntity(post.getCategory()))
                .createdDate(post.getCreatedDate())
                .modifiedDate(post.getModifiedDate())
                .build();
    }
}
