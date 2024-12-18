package com.hifive.yeodam.itemTourTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.tour.controller.TourItemAPIController;
import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.dto.TourItemResDto;
import com.hifive.yeodam.tour.dto.TourItemUpdateReqDto;
import com.hifive.yeodam.tour.repository.TourRepositoryCustom;
import com.hifive.yeodam.tour.service.TourItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.hifive.yeodam.tour.entity.Tour;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TourItemTest {
    private final static Long sellerId = 1L;
    private final static String tourName = "test";
    private final static String tourDesc = "test";
    private final static String tourPeriod = "1일";
    private final static String tourRegion = "제주";
    private final static int tourPrice = 100;
    private final static int tourMaximum = 2;
    private Tour expectedTour;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TourItemService tourItemService;

    @Mock
    private TourRepositoryCustom tourRepositoryCustom;

    @InjectMocks
    private TourItemAPIController tourItemAPIController;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tourItemAPIController).build();
        Seller seller = new Seller();
        expectedTour = Tour.builder()
                .seller(seller)
                .itemName(tourName)
                .description(tourDesc)
                .region(tourRegion)
                .period(tourPeriod)
                .price(tourPrice)
                .maximum(tourMaximum)
                .build();

    }


    @Test
    @DisplayName("상품_여행 등록 테스트 성공")
    public void itemTourSaveSuccessTest() throws Exception {

        //given
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(1L);
        categoryIds.add(2L);

        List<Long> guideIds = new ArrayList<>();
        guideIds.add(1L);


        TourItemReqDto tourItemReqDto = new TourItemReqDto();
        tourItemReqDto.setSellerId(sellerId);
        tourItemReqDto.setTourName(tourName);
        tourItemReqDto.setTourDesc(tourDesc);
        tourItemReqDto.setTourPeriod(tourPeriod);
        tourItemReqDto.setTourRegion(tourRegion);
        tourItemReqDto.setTourPrice(tourPrice);
        tourItemReqDto.setMaximum(tourMaximum);
        tourItemReqDto.setCategoryIdList(categoryIds);
        tourItemReqDto.setGuideIdList(guideIds);

        String url = "/api/tours";
        String json = objectMapper.writeValueAsString(tourItemReqDto);


        TourItemResDto expectedTourItemResDto = new TourItemResDto(expectedTour);

        when(tourItemService.saveTourItem(tourItemReqDto)).thenReturn(expectedTourItemResDto);


        //when
        ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));


        //then
        result.andExpect(status().isCreated());
        verify(tourItemService, times(1)).saveTourItem(any(TourItemReqDto.class));
    }
    @Test
    @DisplayName("상품_여행 등록 테스트 실패")
    public void tourItemSaveFailTest() throws Exception {

        //given
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(1L);
        categoryIds.add(2L);

        List<Long> guideIds = new ArrayList<>();
        guideIds.add(1L);


        TourItemReqDto tourItemReqDto = new TourItemReqDto();
        tourItemReqDto.setSellerId(sellerId);
        tourItemReqDto.setTourName(tourName);
        //tourItemReqDto.setTourDesc(tourDesc);
        tourItemReqDto.setTourPeriod(tourPeriod);
        tourItemReqDto.setTourRegion(tourRegion);
        tourItemReqDto.setTourPrice(tourPrice);
        tourItemReqDto.setMaximum(tourMaximum);
        tourItemReqDto.setCategoryIdList(categoryIds);
        tourItemReqDto.setGuideIdList(guideIds);

        String url = "/api/tours";
        String json = objectMapper.writeValueAsString(tourItemReqDto);


        TourItemResDto expectedTourItemResDto = new TourItemResDto(expectedTour);

        when(tourItemService.saveTourItem(tourItemReqDto)).thenReturn(expectedTourItemResDto);


        //when
        ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));


        //then
        result.andExpect(status().isBadRequest());
        //verify(tourItemService, times(1)).saveTourItem(any(TourItemReqDto.class));
    }

    @Test
    @DisplayName("상품_여행 목록 조회 테스트")
    public void itemFindAllTest() throws Exception {
        //given
        String url ="/api/tours";
        int testCount = 4;
        List<TourItemResDto> mockTourList = new ArrayList<>();
        for (int i = 0; i < testCount; i++) {
            Tour tour = Tour.builder().build();
            TourItemResDto tourItemResDto = new TourItemResDto(tour);
            mockTourList.add(tourItemResDto);
        }
        when(tourItemService.findAll()).thenReturn(mockTourList);

        //when
        ResultActions resultActions = mockMvc.perform(get(url));

        MvcResult mvcResult = resultActions.andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<?> responseList = objectMapper.readValue(jsonResponse, List.class);

        //then
        assertEquals(testCount, responseList.size());
        resultActions.andExpect(status().isOk());
    }
    @Test
    @DisplayName("카테고리 필터링 테스트")
    public void itemTourSearchFilterTest() throws Exception {
        // Given
        SearchFilterDto filter = new SearchFilterDto();
        filter.setCategory("레저");

        // When
        List<Tour> results = tourRepositoryCustom.searchByFilter(filter);

        // Then
        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("상품_여행 단일 조회 테스트")
    public void itemFindByIdSuccessTest() throws Exception {
        //given
        String url = "/api/tours/{id}";
        Long tourItemId = 1L;

        TourItemResDto expectedTourItemResDto = new TourItemResDto(expectedTour);

        when(tourItemService.findById(tourItemId)).thenReturn(expectedTourItemResDto);

        //when
        ResultActions resultActions = mockMvc.perform(get(url,tourItemId));

        //then
        resultActions.andExpect(status().isOk());
        //assertEquals(seller, resultTour.getSeller());
    }
    @Test
    @DisplayName("상품_여행 단일 조회 실패 테스트")
    public void itemFindByIdFailTest() throws Exception {
        //given
        //String url = "/api/tours/{id}";
        Long tourItemId = 2L;

        when(tourItemService.findById(tourItemId)).thenThrow(new RuntimeException("해당 여행을 찾을 수 없습니다"));

        //when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tourItemService.findById(tourItemId);
        });

        //then
        assertEquals("해당 여행을 찾을 수 없습니다", exception.getMessage());

        //RuntimeException이 아닌 CustomExceoption으로 처리하여 404 상태 코드를 반환하게 할 것
/*        mockMvc.perform(get(url, tourItemId))
                .andExpect(status().isNotFound()) // 예외에 따른 HTTP 상태 코드
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RuntimeException))
                .andExpect(result -> assertEquals("해당 여행을 찾을 수 없습니다", result.getResolvedException().getMessage()));*/
    }


    @Test
    @DisplayName("상품_여행 수정 실패 성공 테스트")
    public void itemTourUpdateSuccessTest() throws Exception {
        //given
        String url = "/api/tours/{id}";
        Long tourItemId = 1L;

        String updateTourName = "update name";
        String updateTourDesc = "update desc";
        String updateTourPeriod = "반나절";
        String updateTourRegion = "강원도";
        int updateTourPrice = 100;

        List<Long> addCategories = new ArrayList<>();
        addCategories.add(1L);
        addCategories.add(2L);

        List<Long> removeCategories = new ArrayList<>();
        removeCategories.add(3L);

        List<Long> addGuides = new ArrayList<>();
        addGuides.add(4L);
        List<Long> removeGuides = new ArrayList<>();
        removeGuides.add(5L);

        TourItemUpdateReqDto tourItemUpdateReqDto = new TourItemUpdateReqDto();

        tourItemUpdateReqDto.setTourName(updateTourName);
        tourItemUpdateReqDto.setPrice(updateTourPrice);
        tourItemUpdateReqDto.setDescription(updateTourDesc);
        tourItemUpdateReqDto.setPeriod(updateTourPeriod);
        tourItemUpdateReqDto.setRegion(updateTourRegion);
        tourItemUpdateReqDto.setAddCategoryIds(addCategories);
        tourItemUpdateReqDto.setRemoveCategoryIds(removeCategories);
        tourItemUpdateReqDto.setAddGuideIds(addGuides);
        tourItemUpdateReqDto.setRemoveGuideIds(removeGuides);

        String json = objectMapper.writeValueAsString(tourItemUpdateReqDto);

        Tour updatedTour = Tour.builder()
                .itemName(updateTourName)
                .description(updateTourDesc)
                .region(updateTourRegion)
                .period(updateTourPeriod)
                .price(updateTourPrice)
                .build();
        TourItemResDto expectedTourItemResDto = new TourItemResDto(updatedTour);

        when(tourItemService.update(tourItemId,tourItemUpdateReqDto)).thenReturn(expectedTourItemResDto);

        //when
        ResultActions resultActions = mockMvc.perform(patch(url,tourItemId).contentType(MediaType.APPLICATION_JSON).content(json));

        //then
        resultActions.andExpect(status().isOk());
        verify(tourItemService, times(1)).update(any(Long.class),any(TourItemUpdateReqDto.class));
    }
    @Test
    @DisplayName("상품_여행 수정 실패 테스트")
    public void itemTourUpdateFailTest() throws Exception {
        //given
        //String url = "/api/tours/{id}";
        Long tourItemId = 2L;

        String updateTourName = "update name";
        String updateTourDesc = "update desc";
        String updateTourPeriod = "반나절";
        String updateTourRegion = "강원도";
        int updateTourPrice = 100;

        List<Long> addCategories = new ArrayList<>();
        addCategories.add(1L);
        addCategories.add(2L);

        List<Long> removeCategories = new ArrayList<>();
        removeCategories.add(3L);

        List<Long> addGuides = new ArrayList<>();
        addGuides.add(4L);
        List<Long> removeGuides = new ArrayList<>();
        removeGuides.add(5L);

        TourItemUpdateReqDto tourItemUpdateReqDto = new TourItemUpdateReqDto();

        tourItemUpdateReqDto.setTourName(updateTourName);
        tourItemUpdateReqDto.setPrice(updateTourPrice);
        tourItemUpdateReqDto.setDescription(updateTourDesc);
        tourItemUpdateReqDto.setPeriod(updateTourPeriod);
        tourItemUpdateReqDto.setRegion(updateTourRegion);
        tourItemUpdateReqDto.setAddCategoryIds(addCategories);
        tourItemUpdateReqDto.setRemoveCategoryIds(removeCategories);
        tourItemUpdateReqDto.setAddGuideIds(addGuides);
        tourItemUpdateReqDto.setRemoveGuideIds(removeGuides);

        when(tourItemService.update(tourItemId,tourItemUpdateReqDto)).thenThrow(new RuntimeException("해당 여행을 찾을 수 없습니다"));

        //when
        //ResultActions resultActions = mockMvc.perform(patch(url,tourItemId).contentType(MediaType.APPLICATION_JSON).content(json));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tourItemService.update(tourItemId,tourItemUpdateReqDto);
        });

        //then
        assertEquals("해당 여행을 찾을 수 없습니다", exception.getMessage());
        verify(tourItemService, times(1)).update(any(Long.class),any(TourItemUpdateReqDto.class));
    }

    @Test
    @DisplayName("상품_여행 삭제 성공 테스트")
    public void itemTourDeleteSuccessTest() throws Exception {
        //given
        String url = "/api/tours/{id}";
        Long tourItemId = 1L;


        //when
        ResultActions resultActions = mockMvc.perform(delete(url,tourItemId));

        //then
        resultActions.andExpect(status().isNoContent());
        verify(tourItemService,times(1)).delete(tourItemId);
    }
    @Test
    @DisplayName("상품_여행 삭제 실패 테스트")
    public void itemTourDeleteFailTest() throws Exception {
        //given
        //String url = "/api/tours/{id}";
        Long tourItemId = 2L;
        doThrow(new RuntimeException("해당 여행을 찾을 수 없습니다"))
                .when(tourItemService).delete(tourItemId);


        //when
        //ResultActions resultActions = mockMvc.perform(delete(url,tourItemId));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tourItemService.delete(tourItemId);
        });

        //then
        assertEquals("해당 여행을 찾을 수 없습니다", exception.getMessage());
        verify(tourItemService,times(1)).delete(tourItemId);
    }


}
