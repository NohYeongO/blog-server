package com.blog.api.mapper;

import com.blog.api.request.PostCreateRequest;
import com.blog.api.request.PostUpdateRequest;
import com.blog.api.response.PostResponse;
import com.blog.api.response.PostSummaryResponse;
import com.blog.board.dto.PostRequestDto;
import com.blog.board.dto.PostResponseDto;
import com.blog.board.dto.PostSimpleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final CategoryMapper categoryMapper;

    public PostRequestDto toRequestDto(PostCreateRequest request) {
        return PostRequestDto.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .published(request.getPublished())
                .categoryName(request.getCategoryName())
                .build();
    }

    public PostRequestDto toRequestDto(PostUpdateRequest request) {
        return PostRequestDto.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .published(request.getPublished())
                .categoryName(request.getCategoryName())
                .build();
    }

    public PostResponse toResponse(PostResponseDto postResponseDto) {
        if (postResponseDto == null) {
            return null;
        }
        return PostResponse.builder()
                .id(postResponseDto.getId())
                .title(postResponseDto.getTitle())
                .content(postResponseDto.getContent())
                .author(postResponseDto.getAuthor())
                .published(postResponseDto.isPublished())
                .category(categoryMapper.toResponse(postResponseDto.getCategory()))
                .createdDate(postResponseDto.getCreatedDate())
                .modifiedDate(postResponseDto.getModifiedDate())
                .build();
    }

    public PostSummaryResponse toSummaryResponse(PostSimpleResponseDto postSimpleResponseDto) {
        if (postSimpleResponseDto == null) {
            return null;
        }
        return PostSummaryResponse.builder()
                .id(postSimpleResponseDto.getId())
                .title(postSimpleResponseDto.getTitle())
                .author(postSimpleResponseDto.getAuthor())
                .content(postSimpleResponseDto.getContent())
                .published(postSimpleResponseDto.isPublished())
                .category(categoryMapper.toResponse(postSimpleResponseDto.getCategory()))
                .createdDate(postSimpleResponseDto.getCreatedDate())
                .build();
    }

    public List<PostSummaryResponse> toSummaryResponseList(List<PostSimpleResponseDto> postSimpleResponseDtos) {
        return postSimpleResponseDtos.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }
}
