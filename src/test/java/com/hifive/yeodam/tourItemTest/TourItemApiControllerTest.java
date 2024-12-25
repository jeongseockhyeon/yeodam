package com.hifive.yeodam.tourItemTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.category.dto.CategoryResDto;
import com.hifive.yeodam.category.entity.Category;
import com.hifive.yeodam.global.exception.CustomExceptionHandler;
import com.hifive.yeodam.tour.dto.GuideInTourResDto;
import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.dto.TourItemResDto;
import com.hifive.yeodam.tour.dto.TourItemUpdateReqDto;
import com.hifive.yeodam.tour.service.TourItemService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TourItemApiControllerTest {

    MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private com.hifive.yeodam.tour.controller.TourItemApiController tourItemApiController;

    @Mock
    private TourItemService tourItemService;

    @BeforeEach
    public void setMockMvc(){
        mockMvc = MockMvcBuilders.standaloneSetup(tourItemApiController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("상품_여행 등록 테스트")
    public void tourItemSaveTest() throws Exception {

        //given
        String tourName = "test";
        String tourDesc = "test";
        String tourPeriod = "1일";
        String tourRegion = "제주";
        String maximum = "2";

        String categoryIds = "[1,2]";

        String tourPrice = "100";

        String formData = "tourName=" + tourName +
                "&tourDesc=" + tourDesc +
                "&tourPeriod=" + tourPeriod +
                "&tourRegion=" + tourRegion +
                "&maximum=" + maximum +
                "&categoryIdList=" + categoryIds +
                "&tourPrice=" + tourPrice;

        String url = "/api/tours";

        List<CategoryResDto> categoryResDtoList = new ArrayList<>();

        Category category1 = new Category(1L, "Category1",null , null);
        Category category2 = new Category(2L, "Category2",null , null);

        CategoryResDto categoryResDto1 = new CategoryResDto(category1);
        CategoryResDto categoryResDto2 = new CategoryResDto(category2);

        categoryResDtoList.add(categoryResDto1);
        categoryResDtoList.add(categoryResDto2);

        TourItemResDto tourItemResDto = mock(TourItemResDto.class);
        when(tourItemResDto.getTourName()).thenReturn(tourName);
        when(tourItemResDto.getTourDesc()).thenReturn(tourDesc);
        when(tourItemResDto.getTourPeriod()).thenReturn(tourPeriod);
        when(tourItemResDto.getTourRegion()).thenReturn(tourRegion);
        when(tourItemResDto.getTourPrice()).thenReturn(100);
        when(tourItemResDto.getMaximum()).thenReturn(2);
        when(tourItemResDto.getCategoryResDtoList()).thenReturn(categoryResDtoList);

        doReturn(tourItemResDto).when(tourItemService).saveTourItem(any(TourItemReqDto.class),any(Auth.class));

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formData)
        );

        //then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.tourName").value(tourName))
                .andExpect(jsonPath("$.tourDesc").value(tourDesc))
                .andExpect(jsonPath("$.tourPeriod").value(tourPeriod))
                .andExpect(jsonPath("$.tourRegion").value(tourRegion))
                .andExpect(jsonPath("$.tourPrice").value(100))
                .andExpect(jsonPath("$.maximum").value(2))
                .andExpect(jsonPath("$.categoryResDtoList[0].id").value(1L))
                .andExpect(jsonPath("$.categoryResDtoList[0].name").value("Category1"))
                .andExpect(jsonPath("$.categoryResDtoList[1].id").value(2L))
                .andExpect(jsonPath("$.categoryResDtoList[1].name").value("Category2"));
    }

    @Test
    @DisplayName("상품_여행 목록 조회 테스트")
    public void tourItemFindAllTest() throws Exception {
        //given
        String url ="/api/tours";

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isOk());

    }

    @Test
    @DisplayName("상품_여행 단일 조회 테스트")
    public void tourItemFindByIdTest() throws Exception {
        //given
        Long tourItemId = 1L;
        String tourName = "test";
        String tourDesc = "test";
        String tourPeriod = "1일";
        String tourRegion = "제주";
        int tourPrice = 100;
        String url = "/api/tours/{id}";


        TourItemResDto tourItemResDto = mock(TourItemResDto.class);
        when(tourItemResDto.getId()).thenReturn(tourItemId);
        when(tourItemResDto.getTourName()).thenReturn(tourName);
        when(tourItemResDto.getTourDesc()).thenReturn(tourDesc);
        when(tourItemResDto.getTourPeriod()).thenReturn(tourPeriod);
        when(tourItemResDto.getTourRegion()).thenReturn(tourRegion);
        when(tourItemResDto.getTourPrice()).thenReturn(tourPrice);
        when(tourItemResDto.getMaximum()).thenReturn(2);

        doReturn(tourItemResDto).when(tourItemService).findById(tourItemId);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url,tourItemId)
                .contentType(MediaType.APPLICATION_JSON)
        );


        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.tourName").value(tourName))
                .andExpect(jsonPath("$.tourDesc").value(tourDesc))
                .andExpect(jsonPath("$.tourPeriod").value(tourPeriod))
                .andExpect(jsonPath("$.tourRegion").value(tourRegion))
                .andExpect(jsonPath("$.tourPrice").value(tourPrice))
                .andExpect(jsonPath("$.maximum").value(2));

    }

    @Test
    @DisplayName("상품_여행 수정 테스트")
    public void tourItemUpdateTest() throws Exception {
        //given
        String url = "/api/tours/{id}";
        Long tourItemId = 1L;

        String tourName = "update name";
        String tourDesc = "update desc";

        Category category2 = new Category(2L,"test",null,null);

        CategoryResDto categoryResDto2 = new CategoryResDto(category2);

        List<CategoryResDto> categoryResDtoList = new ArrayList<>();
        categoryResDtoList.add(categoryResDto2);

        GuideInTourResDto guideInTourResDto = mock(GuideInTourResDto.class);
        when(guideInTourResDto.getId()).thenReturn(2L);

        List<GuideInTourResDto> guideList = new ArrayList<>();
        guideList.add(guideInTourResDto);

        List<Long> addCategories = new ArrayList<>();
        addCategories.add(2L);

        List<Long> removeCategories = new ArrayList<>();
        removeCategories.add(1L);

        List<Long> addGuides = new ArrayList<>();
        addGuides.add(2L);
        List<Long> removeGuides = new ArrayList<>();
        removeGuides.add(1L);

        TourItemUpdateReqDto tourItemUpdateReqDto = mock(TourItemUpdateReqDto.class);

        TourItemResDto tourItemResDto = mock(TourItemResDto.class);
        when(tourItemResDto.getId()).thenReturn(tourItemId);
        when(tourItemResDto.getTourName()).thenReturn(tourName);
        when(tourItemResDto.getTourDesc()).thenReturn(tourDesc);
        when(tourItemResDto.getCategoryResDtoList()).thenReturn(categoryResDtoList);
        when(tourItemResDto.getGuideInTourResDtos()).thenReturn(guideList);

        String json = objectMapper.writeValueAsString(tourItemUpdateReqDto);

        doReturn(tourItemResDto).when(tourItemService).update(any(Long.class), any(TourItemUpdateReqDto.class));


        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch(url,tourItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.tourName").value(tourName))
                .andExpect(jsonPath("$.tourDesc").value(tourDesc))
                .andExpect(jsonPath("$.categoryResDtoList[0].id").value(2L))
                .andExpect(jsonPath("$.categoryResDtoList[0].name").value("test"))
                .andExpect(jsonPath("$.guideInTourResDtos[0].id").value(2L));



    }

    @Test
    @DisplayName("상품_여행 삭제 테스트")
    public void tourItemDeleteTest() throws Exception {
        //given
        String url = "/api/tours/{id}";
        Long tourItemId = 1L;

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url,tourItemId)
        );

        //then
        resultActions.andExpect(status().isNoContent());
    }
}