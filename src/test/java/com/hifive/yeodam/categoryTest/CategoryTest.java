package com.hifive.yeodam.categoryTest;

import com.hifive.yeodam.category.controller.CategoryApiController;
import com.hifive.yeodam.category.dto.CategoryReqDto;
import com.hifive.yeodam.category.dto.CategoryResDto;
import com.hifive.yeodam.category.entity.Category;
import com.hifive.yeodam.category.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class CategoryTest {
    private final static  String categoryName = "액티비티";
    private final static String subCategoryName = "레저";

    MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;


    @InjectMocks
    private CategoryApiController categoryApiController;

    @BeforeEach
    public void setMockMvc(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryApiController).build();
    }

    @Test
    @DisplayName("카테고리 생성 테스트")
    public void saveCategoryTest(){
        //given
        CategoryReqDto categoryReqDto = mock(CategoryReqDto.class);
        when(categoryReqDto.getCategoryName()).thenReturn(categoryName);

        Category category = mock(Category.class);
        when(category.getName()).thenReturn(categoryName);

        when(categoryService.saveCategory(categoryReqDto)).thenReturn(category);

        //when
        String result = categoryService.saveCategory(categoryReqDto).getName();

        //then
        assertEquals(categoryName, result);
        verify(categoryService, times(1)).saveCategory(categoryReqDto);
    }
    @Test
    @DisplayName("하위 카테고리 생성 테스트")
    public void saveSubCategoryTest() {
        //given
        Long parentId = 1L;
        Category category = mock(Category.class);
        when(category.getId()).thenReturn(parentId);

        CategoryReqDto categoryReqDto = mock(CategoryReqDto.class);
        when(categoryReqDto.getCategoryName()).thenReturn(subCategoryName);
        when(categoryReqDto.getParentCategoryId()).thenReturn(parentId);

        Category subCategory = mock(Category.class);
        when(subCategory.getName()).thenReturn(subCategoryName);
        when(subCategory.getParent()).thenReturn(category);

        when(categoryService.saveCategory(categoryReqDto)).thenReturn(subCategory);

        //when
        Category result = categoryService.saveCategory(categoryReqDto);

        //then
        assertEquals(subCategoryName, result.getName());
        assertEquals(1L, result.getParent().getId());
        verify(categoryService, times(1)).saveCategory(any(CategoryReqDto.class));

    }

    @Test
    @DisplayName("카테고리 전체 목록 조회 테스트")
    public void findAllCategoryTest() {
        //given
        int testCount = 3;

        List<CategoryResDto> mockCategoryList = new ArrayList<>();
        for (int i = 0; i < testCount; i++) {
            Category category = Category.builder().build();
            CategoryResDto categoryResDto = new CategoryResDto(category);
            mockCategoryList.add(categoryResDto);
        }
        when(categoryService.findAllCategory()).thenReturn(mockCategoryList);

        //when
        List<CategoryResDto> result = categoryService.findAllCategory();

        //then
        assertEquals(testCount, result.size());
        verify(categoryService, times(1)).findAllCategory();
    }

    @Test
    @DisplayName("카테고리 단일 조회 테스트")
    public void findCategoryTest(){
        //given
        Long categoryId = 1L;

        CategoryResDto mockCategoryResDto = mock(CategoryResDto.class);
        when(mockCategoryResDto.getId()).thenReturn(categoryId);
        when(mockCategoryResDto.getName()).thenReturn(categoryName);

        when(categoryService.findCategoryById(categoryId)).thenReturn(mockCategoryResDto);

        //when
        CategoryResDto result = categoryService.findCategoryById(categoryId);

        //then
        assertEquals(categoryId, result.getId());
        assertEquals(categoryName, result.getName());
        verify(categoryService, times(1)).findCategoryById(categoryId);
    }

    @Test
    @DisplayName("카테고리 수정 테스트")
    public void updateCategoryTest() {
        //given
        Long categoryId = 1L;
        CategoryReqDto updateCategoryReqDto = mock(CategoryReqDto.class);
        when(updateCategoryReqDto.getCategoryName()).thenReturn("공연/전시/체험");

        Category updatedCategory = mock(Category.class);
        when(updatedCategory.getId()).thenReturn(categoryId);
        when(updatedCategory.getName()).thenReturn("공연/전시/체험");

        when(categoryService.updateCategory(categoryId, updateCategoryReqDto)).thenReturn(updatedCategory);

        //when
        Category result = categoryService.updateCategory(categoryId, updateCategoryReqDto);

        //then
        assertEquals(categoryId, result.getId());
        assertEquals("공연/전시/체험", result.getName());
        verify(categoryService, times(1)).updateCategory(categoryId, updateCategoryReqDto);

    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    public void deleteCategoryTest(){
        //given
        Long categoryId = 1L;

        //when
        categoryService.deleteCategory(categoryId);

        //then
        verify(categoryService,times(1)).deleteCategory(categoryId);

    }

}
