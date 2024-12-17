package com.hifive.yeodam.itemTourTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifive.yeodam.category.entity.Category;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.tour.controller.TourItemAPIController;
import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.dto.TourItemUpdateReqDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TourItemService tourItemService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private TourItemAPIController tourItemAPIController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tourItemAPIController).build();
    }


    @Test
    @DisplayName("상품_여행 등록 테스트")
    public void itemTourSaveTest() throws Exception {

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
        tourItemReqDto.setCategoryIdList(categoryIds);
        tourItemReqDto.setGuideIdList(guideIds);

        String url = "/api/tours";
        String json = objectMapper.writeValueAsString(tourItemReqDto);

        Seller seller = new Seller();



        Tour expectedTour = Tour.builder()
                .seller(seller)
                .itemName(tourName)
                .description(tourDesc)
                .region(tourRegion)
                .period(tourPeriod)
                .price(tourPrice)
                .build();

        when(tourItemService.saveTourItem(tourItemReqDto)).thenReturn(expectedTour);


        //when
        ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));


        //then
        result.andExpect(status().isCreated());
        verify(tourItemService, times(1)).saveTourItem(any(TourItemReqDto.class));
    }

    @Test
    @DisplayName("상품_여행 목록 조회 테스트")
    public void itemFindAllTest() throws Exception {
        //given
        String url ="/api/tours";
        int testCount = 4;
        List<Tour> mockTourList = new ArrayList<>();
        for (int i = 0; i < testCount; i++) {
            Tour tour = Tour.builder().build();
            mockTourList.add(tour);
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
        //given
        String url ="/api/tours";
        int testCount = 4;
        String filterCategory = "액티비티";
        Category category  = Category.builder()
                .name(filterCategory)
                .build();
        List<Tour> mockTourList = new ArrayList<>();
        for (int i = 0; i < testCount; i++) {
            Tour tour = Tour.builder().build();
            mockTourList.add(tour);
        }
    }

    @Test
    @DisplayName("상품_여행 단일 조회 테스트")
    public void itemFindByIdTest() throws Exception {
        //given
        String url = "/api/tours/{id}";
        Long tourItemId = 1L;

        Seller seller = new Seller();
        Tour expectedTour = Tour.builder()
                .seller(seller)
                .itemName(tourName)
                .description(tourDesc)
                .region(tourRegion)
                .period(tourPeriod)
                .price(tourPrice)
                .build();

        when(tourItemService.findById(tourItemId)).thenReturn(expectedTour);

        //when
        ResultActions resultActions = mockMvc.perform(get(url,tourItemId));

        MvcResult mvcResult = resultActions.andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        Tour resultTour = objectMapper.readValue(jsonResponse, Tour.class);

        //then
        resultActions.andExpect(status().isOk());
        assertEquals(seller, resultTour.getSeller());
        assertEquals(tourName, resultTour.getItemName());
        assertEquals(tourDesc, resultTour.getDescription());
        assertEquals(tourPeriod, resultTour.getPeriod());
        assertEquals(tourPrice, resultTour.getPrice());
    }

    @Test
    @DisplayName("상품_여행 수정 테스트")
    public void itemTourUpdateTest() throws Exception {
        //given
        String url = "/api/tours/{id}";
        Long tourItemId = 1L;

        String updateTourName = "update name";
        String updateTourDesc = "update desc";
        String updateTourPeriod = "반나절";
        String updateTourRegion = "강원도";
        int updateTourPrice = 100;

        TourItemUpdateReqDto tourItemUpdateReqDto = new TourItemUpdateReqDto();

        tourItemUpdateReqDto.setTourName(updateTourName);
        tourItemUpdateReqDto.setPrice(updateTourPrice);
        tourItemUpdateReqDto.setDescription(updateTourDesc);
        tourItemUpdateReqDto.setPeriod(updateTourPeriod);
        tourItemUpdateReqDto.setRegion(updateTourRegion);

        String json = objectMapper.writeValueAsString(tourItemUpdateReqDto);

        Tour expectedTour = Tour.builder()
                .itemName(updateTourName)
                .description(updateTourDesc)
                .region(updateTourRegion)
                .period(updateTourPeriod)
                .price(updateTourPrice)
                .build();

        when(tourItemService.update(tourItemId,tourItemUpdateReqDto)).thenReturn(expectedTour);

        //when
        ResultActions resultActions = mockMvc.perform(patch(url,tourItemId).contentType(MediaType.APPLICATION_JSON).content(json));

        //then
        resultActions.andExpect(status().isOk());
        verify(tourItemService, times(1)).update(any(Long.class),any(TourItemUpdateReqDto.class));
    }

    @Test
    @DisplayName("상품_여행 삭제 테스트")
    public void itemTourDeleteTest() throws Exception {
        //given
        String url = "/api/tours/{id}";
        Long tourItemId = 1L;


        //when
        ResultActions resultActions = mockMvc.perform(delete(url,tourItemId));

        //then
        resultActions.andExpect(status().isNoContent());
        verify(tourItemService,times(1)).delete(tourItemId);
    }


}
