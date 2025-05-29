package com.blog.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;       // 현재 페이지의 데이터 목록
    private int pageNumber;        // 현재 페이지 번호 (0부터 시작)
    private int pageSize;          // 페이지 당 데이터 수
    private int totalPages;        // 전체 페이지 수
    private long totalElements;    // 전체 데이터 수
    private boolean first;         // 첫 페이지 여부
    private boolean last;          // 마지막 페이지 여부
    private boolean empty;         // 현재 페이지가 비어있는지 여부

    public PageResponseDto(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }
}
