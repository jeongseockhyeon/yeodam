package com.hifive.yeodam.categoryTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifive.yeodam.category.controller.CategoryApiController;
import com.hifive.yeodam.category.dto.CategoryReqDto;
import com.hifive.yeodam.category.dto.CategoryResDto;
import com.hifive.yeodam.category.entity.Category;
import com.hifive.yeodam.category.repository.CategoryRepository;
import com.hifive.yeodam.category.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class CategoryTest {
    private final static  String categoryName = "액티비티";
    private final static String subCategoryName = "레저";

    MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryApiController categoryApiController;

    @BeforeEach
    public void setMockMvc(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryApiController).build();
    }

    @Test
    @DisplayName("카테고리 생성 테스트")
    public void saveCategoryTest() throws Exception {
        //given
        CategoryReqDto categoryReqDto = new CategoryReqDto();
        categoryReqDto.setCategoryName(categoryName);
        String url = "/api/categorys";

        String json = objectMapper.writeValueAsString(categoryReqDto);

        Category category = Category.builder()
                .name(categoryName)
                .build();



        when(categoryService.saveCategory(categoryReqDto)).thenReturn(category);

        //when
        ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
        result.andExpect(status().isCreated());
        verify(categoryService, times(1)).saveCategory(any(CategoryReqDto.class));

        //then
        result.andExpect(status().isCreated());
        assertEquals(categoryName, category.getName());
    }
    @Test
    @DisplayName("하위 카테고리 생성 테스트")
    public void saveSubCategoryTest() throws Exception {
        //given
        Long parentId = 1L;
        CategoryReqDto categoryReqDto = new CategoryReqDto();
        categoryReqDto.setParentCategoryId(parentId);
        categoryReqDto.setCategoryName(subCategoryName);

        String url = "/api/categorys";
        String json = objectMapper.writeValueAsString(categoryReqDto);

        Category parentCategory = Category.builder()
                .name(categoryName)
                .build();

        categoryRepository.save(parentCategory);

        Category childCategory = Category.builder()
                .parent(parentCategory)
                .name(subCategoryName)
                .build();

        when(categoryService.saveCategory(any(CategoryReqDto.class))).thenReturn(childCategory);

        //when
        ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));

        //then
        result.andExpect(status().isCreated());
        verify(categoryService, times(1)).saveCategory(any(CategoryReqDto.class));

    }

    @Test
    @DisplayName("카테고리 전체 목록 조회 테스트")
    public void findAllCategoryTest() throws Exception {
        //given
        String url = "/api/categorys";
        int testCount = 3;

        List<CategoryResDto> mockCategoryList = new ArrayList<>();
        for (int i = 0; i < testCount; i++) {
            Category category = Category.builder().build();
            CategoryResDto categoryResDto = new CategoryResDto(category);
            mockCategoryList.add(categoryResDto);
        }
        when(categoryService.findAllCategory()).thenReturn(mockCategoryList);

        //when
        ResultActions result = mockMvc.perform(get(url));
        MvcResult mvcResult = result.andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<?> responseList = objectMapper.readValue(jsonResponse, List.class);

        //then
        assertEquals(testCount, responseList.size());
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("카테고리 단일 조회 테스트")
    public void findCategoryTest() throws Exception {
        //given
        Long categoryId = 1L;

        CategoryResDto mockCategoryResDto = mock(CategoryResDto.class);
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
    public void updateCategoryTest() throws Exception {
        //given
        Long categoryId = 1L;
        String updateName = "공연/전시/체험";
        CategoryReqDto categoryReqDto = new CategoryReqDto();
        categoryReqDto.setCategoryName(updateName);
        String url = "/api/categorys/{id}";
        String json = objectMapper.writeValueAsString(categoryReqDto);

        Category updateCategory = Category.builder()
                .id(categoryId)
                .name(updateName)
                .build();

        when(categoryService.updateCategory(categoryId, categoryReqDto)).thenReturn(updateCategory);

        //when
        ResultActions result = mockMvc.perform(patch(url, categoryId).contentType(MediaType.APPLICATION_JSON).content(json));

        //then
        result.andExpect(status().isOk());
        verify(categoryService, times(1)).updateCategory(any(Long.class), any(CategoryReqDto.class));

    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    public void deleteCategoryTest() throws Exception {
        //given
        Long categoryId = 1L;

        //when
        ResultActions result = mockMvc.perform(delete("/api/categorys/{id}", categoryId));

        //then
        result.andExpect(status().isNoContent());
        verify(categoryService,times(1)).deleteCategory(categoryId);
        //하위 카테고리가 존재하는 상위 카테고리 삭제 요청 시 발생하는 오류 추후 예외 처리
        // -> 하위 카테고리가 존재할 경우 상위 카테고리는 삭제되서는 안됨
        //해당 카테고리를 사용하고 있는 여행 상품이 있을 경우 삭제 x

    }

}
