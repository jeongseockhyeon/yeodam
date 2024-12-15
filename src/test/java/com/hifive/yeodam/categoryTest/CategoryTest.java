package com.hifive.yeodam.categoryTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifive.yeodam.category.dto.CategoryReqDto;
import com.hifive.yeodam.category.entity.Category;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryTest {
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private CategoryService categoryService;

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
        String url = "/api/category";

        String json = objectMapper.writeValueAsString(categoryReqDto);

        //when
        ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
        List<Category> categories = categoryService.findAllCategory();
        Category category = categories.get(0);

        //then
        result.andExpect(status().isCreated());
        assertEquals(categoryName, category.getName());
    }

    @Test
    @DisplayName("카테고리 전체 목록 조회")
    public void findAllCategoryTest() throws Exception {
        //given
        String url = "/api/category";
        int testCount = 1;

        //when
        ResultActions result = mockMvc.perform(get(url));
        MvcResult mvcResult = result.andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<?> responseList = objectMapper.readValue(jsonResponse, List.class);

        //then
        assertEquals(testCount, responseList.size());
        result.andExpect(status().isOk());
    }




}
