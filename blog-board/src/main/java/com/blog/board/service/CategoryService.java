package com.blog.board.service;

import com.blog.board.exception.CategoryAlreadyExistsException;
import com.blog.board.exception.CategoryNotFoundException;
import com.blog.board.domain.Category;
import com.blog.board.dto.CategoryDto;
import com.blog.board.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryDto::fromEntity)
                .toList();
    }

    @Transactional
    public Category findOrCreateCategory(String categoryName) {
        String trimmedName = categoryName.trim();
        Optional<Category> existingCategory = categoryRepository.findByCategoryName(trimmedName);

        if (existingCategory.isPresent()) {
            return existingCategory.get();
        }
        
        Category newCategory = Category.builder()
                .categoryName(trimmedName)
                .build();

        return categoryRepository.save(newCategory);
    }

    @Transactional
    public CategoryDto createCategory(String categoryName) {
        String trimmedName = categoryName.trim();

        if (categoryRepository.findByCategoryName(trimmedName).isPresent()) {
            throw new CategoryAlreadyExistsException();
        }

        Category newCategory = Category.builder()
                .categoryName(trimmedName)
                .build();

        Category savedCategory = categoryRepository.save(newCategory);
        return CategoryDto.fromEntity(savedCategory);
    }

    @Transactional
    public CategoryDto updateCategory(Long categoryId, String newName) {
        String trimmedName = newName.trim();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("카테고리를 찾을 수 없습니다 - ID: {}", categoryId);
                    return new CategoryNotFoundException();
                });

        Optional<Category> duplicateCategory = categoryRepository.findByCategoryName(trimmedName);
        if (duplicateCategory.isPresent() && !duplicateCategory.get().getCategoryId().equals(categoryId)) {
            throw new CategoryAlreadyExistsException();
        }

        category.updateName(trimmedName);
        return CategoryDto.fromEntity(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("카테고리를 찾을 수 없습니다 - ID: {}", categoryId);
                    return new CategoryNotFoundException();
                });
        String categoryName = category.getCategoryName();
        categoryRepository.delete(category);
        log.info("카테고리 삭제 완료 - ID: {}, 이름: {}", categoryId, categoryName);
    }
}
