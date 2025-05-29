package com.blog.board.dto;

import com.blog.board.domain.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryDto {
    private final Long id;
    private final String name;

    public static CategoryDto fromEntity(Category category) {
        return category == null ? null : CategoryDto.builder()
                .id(category.getCategoryId())
                .name(category.getCategoryName())
                .build();
    }
}
