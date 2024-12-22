package com.hifive.yeodam.categoryTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifive.yeodam.category.controller.CategoryApiController;
import com.hifive.yeodam.category.dto.CategoryReqDto;
import com.hifive.yeodam.category.dto.CategoryResDto;

import com.hifive.yeodam.category.service.CategoryService;
import com.hifive.yeodam.global.exception.CustomExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CategoryApiControllerTest {

    @InjectMocks
    private CategoryApiController categoryApiController;

    @Mock
    private CategoryService categoryService;

    private MockMvc mockMvc;

    private  final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryApiController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("카테고리 생성 테스트")
    public void saveCategoryTest() throws Exception {

        //given
        String categoryName = "액티비티";
        CategoryReqDto categoryReqDto = mock(CategoryReqDto.class);
        when(categoryReqDto.getCategoryName()).thenReturn(categoryName);

        CategoryResDto categoryResDto = mock(CategoryResDto.class);
        when(categoryResDto.getName()).thenReturn(categoryName);

        String reqUrl = "/api/categories";

        String json = objectMapper.writeValueAsString(categoryReqDto);
       doReturn(categoryResDto).when(categoryService).saveCategory(any(CategoryReqDto.class));

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(reqUrl)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(categoryName));
    }
    @Test
    @DisplayName("하위 카테고리 생성 테스트")
    public void saveSubCategoryTest() throws Exception {
        //given
        Long parentId = 1L;
        String categoryName = "레저";

        CategoryReqDto categoryReqDto = mock(CategoryReqDto.class);
        when(categoryReqDto.getCategoryName()).thenReturn(categoryName);
        when(categoryReqDto.getParentCategoryId()).thenReturn(parentId);

        CategoryResDto categoryResDto = mock(CategoryResDto.class);
        when(categoryResDto.getName()).thenReturn(categoryName);
        when(categoryResDto.getParentId()).thenReturn(parentId);


        String url = "/api/categories";
        String json = objectMapper.writeValueAsString(categoryReqDto);

        doReturn(categoryResDto).when(categoryService).saveCategory(any(CategoryReqDto.class));

        //when
        ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));


        //then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(categoryName));

    }

    @Test
    @DisplayName("카테고리 전체 목록 조회 테스트")
    public void findAllCategoryTest() throws Exception {
        //given
        String url = "/api/categories";
        int testCount = 2;

        List<CategoryResDto> categoryResDtoList = new ArrayList<>();
        for (int i = 0; i < testCount; i++) {
            CategoryResDto categoryResDto = mock(CategoryResDto.class);
            categoryResDtoList.add(categoryResDto);
        }

        doReturn(categoryResDtoList).when(categoryService).findAllCategory();

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        assertEquals(testCount, categoryResDtoList.size());
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("카테고리 단일 조회 테스트")
    public void findCategoryTest() throws Exception {
        //given
        String url = "/api/categories/{id}";
        Long categoryId = 1L;
        String categoryName = "액티비티";

        CategoryResDto categoryResDto = mock(CategoryResDto.class);
        when(categoryResDto.getId()).thenReturn(categoryId);
        when(categoryResDto.getName()).thenReturn(categoryName);

        doReturn(categoryResDto).when(categoryService).findCategoryById(categoryId);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url, categoryId)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value(categoryName));
    }

    @Test
    @DisplayName("카테고리 수정 테스트")
    public void updateCategoryTest() throws Exception {
        //given
        Long categoryId = 1L;
        String updateName = "공연/전시/체험";
        CategoryReqDto categoryReqDto = mock(CategoryReqDto.class);
        when(categoryReqDto.getCategoryName()).thenReturn(updateName);

        CategoryResDto categoryResDto = mock(CategoryResDto.class);
        when(categoryResDto.getId()).thenReturn(categoryId);
        when(categoryResDto.getName()).thenReturn(updateName);

        String url = "/api/categories/{id}";
        String json = objectMapper.writeValueAsString(categoryReqDto);

        doReturn(categoryResDto).when(categoryService).updateCategory(any(Long.class), any(CategoryReqDto.class));

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.patch(url, categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );


        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updateName));
    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    public void deleteCategoryTest() throws Exception {
        //given
        Long categoryId = 1L;
        String url = "/api/categories/{id}";

        doNothing().when(categoryService).deleteCategory(categoryId);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, categoryId)
        );

        //then
        result.andExpect(status().isNoContent());
        //하위 카테고리가 존재하는 상위 카테고리 삭제 요청 시 발생하는 오류 추후 예외 처리
        // -> 하위 카테고리가 존재할 경우 상위 카테고리는 삭제되서는 안됨

    }

}