package com.blog.api.mapper;

import com.blog.api.response.PageResponse;
import com.blog.board.dto.PageResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PageMapper {

    public <T> PageResponse<T> toResponse(PageResponseDto<?> pageResponseDto, List<T> content) {
        if (pageResponseDto == null) {
            return null;
        }
        return PageResponse.<T>builder()
                .content(content)
                .pageNumber(pageResponseDto.getPageNumber())
                .pageSize(pageResponseDto.getPageSize())
                .totalPages(pageResponseDto.getTotalPages())
                .totalElements(pageResponseDto.getTotalElements())
                .first(pageResponseDto.isFirst())
                .last(pageResponseDto.isLast())
                .empty(pageResponseDto.isEmpty())
                .build();
    }
}
