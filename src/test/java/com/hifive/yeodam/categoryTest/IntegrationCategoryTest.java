package com.hifive.yeodam.categoryTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifive.yeodam.category.dto.CategoryReqDto;
import com.hifive.yeodam.category.dto.CategoryResDto;
import com.hifive.yeodam.category.repository.CategoryRepository;
import com.hifive.yeodam.category.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationCategoryTest {
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setMockMvc(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("카테고리 생성 테스트")
    public void saveCategoryTest() throws Exception {

        //given
        String categoryName = "액티비티";
        CategoryReqDto categoryReqDto = new CategoryReqDto();
        categoryReqDto.setCategoryName(categoryName);
        String url = "/api/categorys";

        String json = objectMapper.writeValueAsString(categoryReqDto);

        //when
        ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
        List<CategoryResDto> categories = categoryService.findAllCategory();
        CategoryResDto category = categories.getLast();

        //then
        result.andExpect(status().isCreated());
        assertEquals(categoryName, category.getName());
    }
    @Test
    @DisplayName("하위 카테고리 생성 테스트")
    public void saveSubCategoryTest() throws Exception {
        //given
        Long parentId = 1L;
        String categoryName = "레저";
        CategoryReqDto categoryReqDto = new CategoryReqDto();
        categoryReqDto.setParentCategoryId(parentId);
        categoryReqDto.setCategoryName(categoryName);

        String url = "/api/categorys";
        String json = objectMapper.writeValueAsString(categoryReqDto);

        //when
        ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
        List<CategoryResDto> categories = categoryService.findAllCategory();
        CategoryResDto category = categories.getLast();

        //then
        result.andExpect(status().isCreated());
        assertEquals(categoryName, category.getName());
        //assertEquals(parentCategory.getName(),category.getParent().getName());

    }

    @Test
    @DisplayName("카테고리 전체 목록 조회 테스트")
    public void findAllCategoryTest() throws Exception {
        //given
        String url = "/api/categorys";
        int testCount = 2;

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
        String url = "/api/categorys/{id}";
        Long categoryId = 1L;
        String categoryName = "액티비티";

        //when
        ResultActions result = mockMvc.perform(get(url, categoryId));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(categoryName));
    }

    @Test
    @DisplayName("카테고리 수정 테스트")
    public void updateCategoryTest() throws Exception {
        //given
        Long categoryId = 4L;
        String updateName = "공연/전시/체험";
        CategoryReqDto categoryReqDto = new CategoryReqDto();
        categoryReqDto.setCategoryName(updateName);
        String url = "/api/categorys/{id}";
        String json = objectMapper.writeValueAsString(categoryReqDto);

        //when
        ResultActions result = mockMvc.perform(patch(url, categoryId).contentType(MediaType.APPLICATION_JSON).content(json));
        List<CategoryResDto> categories = categoryService.findAllCategory();
        CategoryResDto category = categories.getLast();

        //then
        result.andExpect(status().isOk());
        assertEquals(updateName, category.getName());
    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    public void deleteCategoryTest() throws Exception {
        //given
        Long categoryId = 4L;

        //when
        ResultActions result = mockMvc.perform(delete("/api/categorys/{id}", categoryId));

        //then
        result.andExpect(status().isNoContent());
        //하위 카테고리가 존재하는 상위 카테고리 삭제 요청 시 발생하는 오류 추후 예외 처리
        // -> 하위 카테고리가 존재할 경우 상위 카테고리는 삭제되서는 안됨

    }

}