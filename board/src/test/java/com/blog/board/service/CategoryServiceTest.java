package com.blog.board.service;

import com.blog.board.domain.Category;
import com.blog.board.dto.CategoryDto;
import com.blog.board.exception.CategoryAlreadyExistsException;
import com.blog.board.exception.CategoryNotFoundException;
import com.blog.board.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService 단위 테스트")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .categoryId(1L)
                .categoryName("개발 일지")
                .build();

        categoryDto = CategoryDto.builder()
                .id(1L)
                .name("개발 일지")
                .build();
    }

    @Test
    @DisplayName("모든 카테고리 조회 - 성공")
    void findAllCategories_Success() {
        // given
        List<Category> categories = List.of(category);
        given(categoryRepository.findAll()).willReturn(categories);

        // when
        List<CategoryDto> result = categoryService.findAllCategories();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("개발 일지");
        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("모든 카테고리 조회 - 빈 목록")
    void findAllCategories_EmptyList() {
        // given
        given(categoryRepository.findAll()).willReturn(List.of());

        // when
        List<CategoryDto> result = categoryService.findAllCategories();

        // then
        assertThat(result).isEmpty();
        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("카테고리 찾기 또는 생성 - 기존 카테고리 반환")
    void findOrCreateCategory_ExistingCategory() {
        // given
        String categoryName = "개발 일지";
        given(categoryRepository.findByCategoryName(categoryName)).willReturn(Optional.of(category));

        // when
        Category result = categoryService.findOrCreateCategory(categoryName);

        // then
        assertThat(result).isEqualTo(category);
        verify(categoryRepository).findByCategoryName(categoryName);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 찾기 또는 생성 - 새 카테고리 생성")
    void findOrCreateCategory_NewCategory() {
        // given
        String categoryName = "새 카테고리";
        given(categoryRepository.findByCategoryName(categoryName)).willReturn(Optional.empty());
        given(categoryRepository.save(any(Category.class))).willReturn(category);

        // when
        Category result = categoryService.findOrCreateCategory(categoryName);

        // then
        assertThat(result).isEqualTo(category);
        verify(categoryRepository).findByCategoryName(categoryName);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 찾기 또는 생성 - 공백 제거")
    void findOrCreateCategory_TrimWhitespace() {
        // given
        String categoryName = "  개발 일지  ";
        given(categoryRepository.findByCategoryName("개발 일지")).willReturn(Optional.of(category));

        // when
        Category result = categoryService.findOrCreateCategory(categoryName);

        // then
        assertThat(result).isEqualTo(category);
        verify(categoryRepository).findByCategoryName("개발 일지");
    }

    @Test
    @DisplayName("카테고리 생성 - 성공")
    void createCategory_Success() {
        // given
        String categoryName = "새 카테고리";
        given(categoryRepository.findByCategoryName(categoryName)).willReturn(Optional.empty());
        given(categoryRepository.save(any(Category.class))).willReturn(category);

        // when
        CategoryDto result = categoryService.createCategory(categoryName);

        // then
        assertThat(result.getName()).isEqualTo("개발 일지");
        verify(categoryRepository).findByCategoryName(categoryName);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 생성 - 중복된 이름으로 예외 발생")
    void createCategory_DuplicateName_ThrowsException() {
        // given
        String categoryName = "개발 일지";
        given(categoryRepository.findByCategoryName(categoryName)).willReturn(Optional.of(category));

        // when & then
        assertThatThrownBy(() -> categoryService.createCategory(categoryName))
                .isInstanceOf(CategoryAlreadyExistsException.class);

        verify(categoryRepository).findByCategoryName(categoryName);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 생성 - 공백 제거 후 생성")
    void createCategory_TrimWhitespace() {
        // given
        String categoryName = "  새 카테고리  ";
        given(categoryRepository.findByCategoryName("새 카테고리")).willReturn(Optional.empty());
        given(categoryRepository.save(any(Category.class))).willReturn(category);

        // when
        CategoryDto result = categoryService.createCategory(categoryName);

        // then
        assertThat(result).isNotNull();
        verify(categoryRepository).findByCategoryName("새 카테고리");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 수정 - 성공")
    void updateCategory_Success() {
        // given
        Long categoryId = 1L;
        String newName = "수정된 카테고리";
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
        given(categoryRepository.findByCategoryName(newName)).willReturn(Optional.empty());

        // when
        CategoryDto result = categoryService.updateCategory(categoryId, newName);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(newName);
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findByCategoryName(newName);
    }

    @Test
    @DisplayName("카테고리 수정 - 존재하지 않는 카테고리로 예외 발생")
    void updateCategory_CategoryNotFound_ThrowsException() {
        // given
        Long categoryId = 999L;
        String newName = "수정된 카테고리";
        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryId, newName))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).findByCategoryName(anyString());
    }

    @Test
    @DisplayName("카테고리 수정 - 중복된 이름으로 예외 발생")
    void updateCategory_DuplicateName_ThrowsException() {
        // given
        Long categoryId = 1L;
        String newName = "기존 카테고리";
        Category anotherCategory = Category.builder()
                .categoryId(2L)
                .categoryName("기존 카테고리")
                .build();

        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
        given(categoryRepository.findByCategoryName(newName)).willReturn(Optional.of(anotherCategory));

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryId, newName))
                .isInstanceOf(CategoryAlreadyExistsException.class);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findByCategoryName(newName);
    }

    @Test
    @DisplayName("카테고리 수정 - 같은 카테고리의 이름 수정 (중복 허용)")
    void updateCategory_SameCategory_Success() {
        // given
        Long categoryId = 1L;
        String newName = "개발 일지";
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
        given(categoryRepository.findByCategoryName(newName)).willReturn(Optional.of(category));

        // when
        CategoryDto result = categoryService.updateCategory(categoryId, newName);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(newName);
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findByCategoryName(newName);
    }

    @Test
    @DisplayName("카테고리 삭제 - 성공")
    void deleteCategory_Success() {
        // given
        Long categoryId = 1L;
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        // when
        categoryService.deleteCategory(categoryId);

        // then
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).delete(category);
    }

    @Test
    @DisplayName("카테고리 삭제 - 존재하지 않는 카테고리로 예외 발생")
    void deleteCategory_CategoryNotFound_ThrowsException() {
        // given
        Long categoryId = 999L;
        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.deleteCategory(categoryId))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
