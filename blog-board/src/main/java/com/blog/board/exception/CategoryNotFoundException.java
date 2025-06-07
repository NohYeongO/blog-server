package com.blog.board.exception;

public class CategoryNotFoundException extends BusinessException {
    public CategoryNotFoundException() {
        super(ErrorCode.CATEGORY_NOT_FOUND);
    }
} 