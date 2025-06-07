package com.blog.api.mapper;

import com.blog.api.response.CategoryResponse;
import com.blog.board.dto.CategoryDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }
        return CategoryResponse.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public List<CategoryResponse> toResponseList(List<CategoryDto> categoryDtos) {
        return categoryDtos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
