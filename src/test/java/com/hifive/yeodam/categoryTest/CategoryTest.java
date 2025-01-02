package com.hifive.yeodam.categoryTest;

import com.hifive.yeodam.category.dto.CategoryReqDto;
import com.hifive.yeodam.category.dto.CategoryResDto;
import com.hifive.yeodam.category.entity.Category;
import com.hifive.yeodam.category.repository.CategoryRepository;
import com.hifive.yeodam.category.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryTest {
    private final static  String categoryName = "액티비티";
    private final static String subCategoryName = "레저";


    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 생성 테스트")
    public void saveCategoryTest(){
        // given
        CategoryReqDto categoryReqDto = new CategoryReqDto(null,categoryName);

        Category savedCategory = Category.builder()
                .id(1L)
                .name(categoryName)
                .parent(null)
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // when
        CategoryResDto result = categoryService.saveCategory(categoryReqDto);

        // then
        assertEquals(categoryName, result.getName());
        assertNull(result.getParentId());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
    @Test
    @DisplayName("하위 카테고리 생성 테스트")
    public void saveSubCategoryTest() {
        // given

        Long parentCategoryId = 1L;

        Category parentCategory = Category.builder()
                .id(parentCategoryId)
                .name(categoryName)
                .build();

        CategoryReqDto categoryReqDto = new CategoryReqDto(parentCategoryId,subCategoryName);

        Category savedCategory = Category.builder()
                .id(2L)
                .name(subCategoryName)
                .parent(parentCategory)
                .build();

        when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // when
        CategoryResDto result = categoryService.saveCategory(categoryReqDto);

        // then
        assertEquals(subCategoryName, result.getName());
        assertNotNull(result.getParentId());
        assertEquals(parentCategoryId, result.getParentId());
        verify(categoryRepository, times(1)).findById(parentCategoryId);
        verify(categoryRepository, times(1)).save(any(Category.class));

    }

    @Test
    @DisplayName("카테고리 전체 목록 조회 테스트")
    public void findAllCategoryTest() {
        //given
        int testCount = 3;

        List<Category> categoryList = new ArrayList<>();

        for (int i = 0; i < testCount; i++) {
            Category category = mock(Category.class);
            categoryList.add(category);
        }
        when(categoryRepository.findAll()).thenReturn(categoryList);

        //when
        List<CategoryResDto> result = categoryService.findAllCategory();

        //then
        assertEquals(testCount, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("카테고리 단일 조회 테스트")
    public void findCategoryTest(){
        //given
        Long categoryId = 1L;
        Category category = mock(Category.class);
        when(category.getId()).thenReturn(categoryId);
        when(category.getName()).thenReturn(categoryName);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        //when
        CategoryResDto result = categoryService.findCategoryById(categoryId);

        //then
        assertEquals(categoryId, result.getId());
        assertEquals(categoryName, result.getName());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("카테고리 수정 테스트")
    public void updateCategoryTest() {
        //given
        Long categoryId = 1L;
        String updateCategoryName = "공연/전시/체험";
        Category category = mock(Category.class);

        Category updatedCategory = mock(Category.class);
        when(updatedCategory.getId()).thenReturn(categoryId);
        when(updatedCategory.getName()).thenReturn(updateCategoryName);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        CategoryReqDto updatedCategoryReqDto = mock(CategoryReqDto.class);
        when(updatedCategoryReqDto.getCategoryName()).thenReturn("공연/전시/체험");

        //when
        CategoryResDto result = categoryService.updateCategory(categoryId, updatedCategoryReqDto);

        //then
        assertEquals(categoryId, result.getId());
        assertEquals(updateCategoryName, result.getName());
        verify(categoryRepository, times(1)).save(category);

    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    public void deleteCategoryTest(){
        //given
        Long categoryId = 1L;

        Category category = mock(Category.class);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        //when
        categoryService.deleteCategory(categoryId);

        //then
        verify(categoryRepository,times(1)).delete(category);

    }

}
