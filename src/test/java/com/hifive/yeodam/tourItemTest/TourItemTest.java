package com.hifive.yeodam.tourItemTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.category.dto.CategoryResDto;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.service.SellerService;
import com.hifive.yeodam.tour.controller.TourItemApiController;
import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.dto.TourItemResDto;
import com.hifive.yeodam.tour.dto.TourItemUpdateReqDto;
import com.hifive.yeodam.tour.repository.TourRepository;
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
    private SellerService sellerService;

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private TourItemApiController tourItemAPIController;



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
    public void tourItemSaveSuccessTest() {

        //given
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(1L);
        categoryIds.add(2L);

        List<Long> guideIds = new ArrayList<>();
        guideIds.add(1L);


        TourItemReqDto tourItemReqDto = mock(TourItemReqDto.class);
        when(tourItemReqDto.getTourName()).thenReturn(tourName);
        when(tourItemReqDto.getTourDesc()).thenReturn(tourDesc);
        when(tourItemReqDto.getTourPeriod()).thenReturn(tourPeriod);
        when(tourItemReqDto.getTourRegion()).thenReturn(tourRegion);
        when(tourItemReqDto.getTourPrice()).thenReturn(tourPrice);
        when(tourItemReqDto.getMaximum()).thenReturn(tourMaximum);
        when(tourItemReqDto.getCategoryIdList()).thenReturn(categoryIds);
        when(tourItemReqDto.getGuideIdList()).thenReturn(guideIds);


        TourItemResDto expectedTourItemResDto = new TourItemResDto(expectedTour);

        when(tourItemService.saveTourItem(tourItemReqDto)).thenReturn(expectedTourItemResDto);


        //when
        TourItemResDto result = tourItemService.saveTourItem(tourItemReqDto);


        //then
        assertEquals(result.getTourName(), expectedTourItemResDto.getTourName());
        assertEquals(result.getTourDesc(), expectedTourItemResDto.getTourDesc());
        assertEquals(result.getTourPeriod(), expectedTourItemResDto.getTourPeriod());
        assertEquals(result.getTourRegion(), expectedTourItemResDto.getTourRegion());
        assertEquals(result.getTourPrice(), expectedTourItemResDto.getTourPrice());
        assertEquals(result.getMaximum(), expectedTourItemResDto.getMaximum());
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


        TourItemReqDto tourItemReqDto = mock(TourItemReqDto.class);
        when(tourItemReqDto.getSellerId()).thenReturn(sellerId);
        when(tourItemReqDto.getTourName()).thenReturn(tourName);
        //when(tourItemReqDto.getTourDesc()).thenReturn(tourDesc);
        when(tourItemReqDto.getTourPeriod()).thenReturn(tourPeriod);
        when(tourItemReqDto.getTourRegion()).thenReturn(tourRegion);
        when(tourItemReqDto.getTourPrice()).thenReturn(tourPrice);
        when(tourItemReqDto.getMaximum()).thenReturn(tourMaximum);
        when(tourItemReqDto.getCategoryIdList()).thenReturn(categoryIds);
        when(tourItemReqDto.getGuideIdList()).thenReturn(guideIds);

        String url = "/api/tours";
        String json = objectMapper.writeValueAsString(tourItemReqDto);


        TourItemResDto expectedTourItemResDto = new TourItemResDto(expectedTour);
        when(tourItemService.saveTourItem(tourItemReqDto)).thenReturn(expectedTourItemResDto);


        //when
        ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));


        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품_여행 목록 조회 테스트")
    public void tourItemFindAllTest() {
        //given
        int testCount = 4;
        List<TourItemResDto> mockTourList = new ArrayList<>();
        for (int i = 0; i < testCount; i++) {
            Tour tour = Tour.builder().build();
            TourItemResDto tourItemResDto = new TourItemResDto(tour);
            mockTourList.add(tourItemResDto);
        }
        when(tourItemService.findAll()).thenReturn(mockTourList);

        //when
        List<TourItemResDto> result = tourItemService.findAll();

        //then
        assertEquals(testCount, result.size());
        verify(tourItemService, times(1)).findAll();
    }
    @Test
    @DisplayName("카테고리 필터링 테스트")
    public void tourItemSearchFilterTest()  {
        // given
        List<TourItemResDto> mockTourList = new ArrayList<>();

        // 카테고리 응답 dto 모킹
        CategoryResDto categoryResDtoMock = mock(CategoryResDto.class);
        when(categoryResDtoMock.getName()).thenReturn("액티비티");

        List<CategoryResDto> categoryResDtoListMock = new ArrayList<>();
        categoryResDtoListMock.add(categoryResDtoMock);

        // 여행상품 응답 dto 모킹
        TourItemResDto resMock = mock(TourItemResDto.class);
        when(resMock.getCategoryResDtoList()).thenReturn(categoryResDtoListMock);

        mockTourList.add(resMock);

        // filter 모킹
        SearchFilterDto filterMock = mock(SearchFilterDto.class);
        List<String> categories = new ArrayList<>();
        categories.add("액티비티");

        when(filterMock.getCategories()).thenReturn(categories);

        when(tourItemService.getSearchFilterTour(filterMock)).thenReturn(mockTourList);

        // when
        List<TourItemResDto> results = tourItemService.getSearchFilterTour(filterMock);

        // then
        assertThat(results).hasSize(1);
        assertEquals("액티비티", results.get(0).getCategoryResDtoList().get(0).getName());
        verify(tourItemService, times(1)).getSearchFilterTour(any(SearchFilterDto.class));
    }

    @Test
    @DisplayName("키워드 필터링 테스트")
    public void tourItemSearchFilterKeywordTest()  {
        //given
        List<TourItemResDto> mockTourList = new ArrayList<>();

        TourItemResDto resMock = mock(TourItemResDto.class);
        when(resMock.getTourName()).thenReturn("제주도");

        mockTourList.add(resMock);

        SearchFilterDto filterMock = mock(SearchFilterDto.class);
        when(filterMock.getKeyword()).thenReturn("제주");

        when(tourItemService.getSearchFilterTour(filterMock)).thenReturn(mockTourList);

        // when
        List<TourItemResDto> results = tourItemService.getSearchFilterTour(filterMock);

        //then
        assertThat(results).hasSize(1);
        assertEquals("제주도",results.get(0).getTourName());
        verify(tourItemService, times(1)).getSearchFilterTour(any(SearchFilterDto.class));
    }

    @Test
    @DisplayName("지역 필터링 테스트")
    public void tourItemSearchFilterRegionTest() {
        //given
        List<TourItemResDto> mockTourList = new ArrayList<>();

        TourItemResDto resMock = mock(TourItemResDto.class);
        when(resMock.getTourRegion()).thenReturn("제주");
        mockTourList.add(resMock);

        SearchFilterDto filterMock = mock(SearchFilterDto.class);
        when(filterMock.getRegion()).thenReturn("제주");

        when(tourItemService.getSearchFilterTour(filterMock)).thenReturn(mockTourList);

        //when
        List<TourItemResDto> results = tourItemService.getSearchFilterTour(filterMock);

        //then
        assertThat(results).hasSize(1);
        assertEquals("제주",results.get(0).getTourRegion());
        verify(tourItemService, times(1)).getSearchFilterTour(any(SearchFilterDto.class));

    }

    @Test
    @DisplayName("기간 필터링 테스트")
    public void tourItemSearchFilterPeriodTest() {
        //given
        List<TourItemResDto> mockTourList = new ArrayList<>();
        TourItemResDto resMock = mock(TourItemResDto.class);
        when(resMock.getTourPeriod()).thenReturn("1일");
        mockTourList.add(resMock);

        SearchFilterDto filterMock = mock(SearchFilterDto.class);
        when(filterMock.getPeriod()).thenReturn("1일");

        when(tourItemService.getSearchFilterTour(filterMock)).thenReturn(mockTourList);

        //when
        List<TourItemResDto> results = tourItemService.getSearchFilterTour(filterMock);

        //then
        assertThat(results).hasSize(1);
        assertEquals("1일",results.get(0).getTourPeriod());
        verify(tourItemService, times(1)).getSearchFilterTour(any(SearchFilterDto.class));

    }

    @Test
    @DisplayName("상품_여행 단일 조회 테스트")
    public void tourItemFindByIdSuccessTest() throws Exception {
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
    public void tourItemFindByIdFailTest() {
        //given
        Long tourItemId = 2L;

        when(tourItemService.findById(tourItemId)).thenThrow(new RuntimeException("해당 여행을 찾을 수 없습니다"));

        //when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tourItemService.findById(tourItemId);
        });

        //then
        assertEquals("해당 여행을 찾을 수 없습니다", exception.getMessage());
    }


    @Test
    @DisplayName("상품_여행 수정 성공 테스트")
    public void tourItemUpdateSuccessTest() {
        //given
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

        TourItemUpdateReqDto tourItemUpdateReqDto = mock(TourItemUpdateReqDto.class);
        when(tourItemUpdateReqDto.getTourName()).thenReturn(updateTourName);
        when(tourItemUpdateReqDto.getDescription()).thenReturn(updateTourDesc);
        when(tourItemUpdateReqDto.getPeriod()).thenReturn(updateTourPeriod);
        when(tourItemUpdateReqDto.getRegion()).thenReturn(updateTourRegion);
        when(tourItemUpdateReqDto.getPrice()).thenReturn(updateTourPrice);
        when(tourItemUpdateReqDto.getAddCategoryIds()).thenReturn(addCategories);
        when(tourItemUpdateReqDto.getAddGuideIds()).thenReturn(addGuides);
        when(tourItemUpdateReqDto.getRemoveCategoryIds()).thenReturn(removeCategories);
        when(tourItemUpdateReqDto.getRemoveGuideIds()).thenReturn(removeGuides);

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
        TourItemResDto result = tourItemService.update(tourItemId,tourItemUpdateReqDto);

        //then
        assertEquals(updateTourName, result.getTourName());
        assertEquals(updateTourDesc,result.getTourDesc());
        assertEquals(updateTourPeriod,result.getTourPeriod());
        assertEquals(updateTourPrice,result.getTourPrice());
        verify(tourItemService, times(1)).update(any(Long.class),any(TourItemUpdateReqDto.class));
    }
    @Test
    @DisplayName("상품_여행 수정 실패 테스트")
    public void tourItemUpdateFailTest() {
        //given
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

        TourItemUpdateReqDto tourItemUpdateReqDto = mock(TourItemUpdateReqDto.class);
        when(tourItemUpdateReqDto.getTourName()).thenReturn(updateTourName);
        when(tourItemUpdateReqDto.getDescription()).thenReturn(updateTourDesc);
        when(tourItemUpdateReqDto.getPeriod()).thenReturn(updateTourPeriod);
        when(tourItemUpdateReqDto.getRegion()).thenReturn(updateTourRegion);
        when(tourItemUpdateReqDto.getPrice()).thenReturn(updateTourPrice);
        when(tourItemUpdateReqDto.getAddCategoryIds()).thenReturn(addCategories);
        when(tourItemUpdateReqDto.getAddGuideIds()).thenReturn(addGuides);
        when(tourItemUpdateReqDto.getRemoveCategoryIds()).thenReturn(removeCategories);
        when(tourItemUpdateReqDto.getRemoveGuideIds()).thenReturn(removeGuides);

        when(tourItemService.update(tourItemId,tourItemUpdateReqDto)).thenThrow(new RuntimeException("해당 여행을 찾을 수 없습니다"));

        //when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tourItemService.update(tourItemId,tourItemUpdateReqDto);
        });

        //then
        assertEquals("해당 여행을 찾을 수 없습니다", exception.getMessage());
        verify(tourItemService, times(1)).update(any(Long.class),any(TourItemUpdateReqDto.class));
    }

    @Test
    @DisplayName("상품_여행 삭제 성공 테스트")
    public void TourItemDeleteSuccessTest() {
        //given
        Long tourItemId = 1L;

        //when
       tourItemService.delete(tourItemId);

        //then
        verify(tourItemService,times(1)).delete(tourItemId);
    }
    @Test
    @DisplayName("상품_여행 삭제 실패 테스트")
    public void tourItemDeleteFailTest() {
        //given
        Long tourItemId = 2L;
        doThrow(new RuntimeException("해당 여행을 찾을 수 없습니다"))
                .when(tourItemService).delete(tourItemId);


        //when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tourItemService.delete(tourItemId);
        });

        //then
        assertEquals("해당 여행을 찾을 수 없습니다", exception.getMessage());
        verify(tourItemService,times(1)).delete(tourItemId);
    }
    @Test
    @DisplayName("업체별 여행 상품 목록 조회")
    public void tourItemFindBySellerTest(){
        //given
        Auth mockAuth = mock(Auth.class);
        Seller mockSeller = mock(Seller.class);

        Tour mockTour1 = mock(Tour.class);
        when(mockTour1.getId()).thenReturn(1L);
        when(mockTour1.getItemName()).thenReturn("test1");
        when(mockTour1.getSeller()).thenReturn(mockSeller);

        Tour mockTour2 = mock(Tour.class);
        when(mockTour2.getId()).thenReturn(2L);
        when(mockTour2.getItemName()).thenReturn("test2");
        when(mockTour2.getSeller()).thenReturn(mockSeller);

        List <TourItemResDto> mockTourItemResDtoList =  List.of(new TourItemResDto(mockTour1),new TourItemResDto(mockTour2));

        when(sellerService.getSellerByAuth(mockAuth)).thenReturn(mockSeller);
        when(tourItemService.findBySeller(mockAuth)).thenReturn(mockTourItemResDtoList);

        //when
        List<TourItemResDto> result = tourItemService.findBySeller(mockAuth);

        //then
        assertEquals(2, result.size());
        assertEquals("test1", result.get(0).getTourName());
        assertEquals("test2", result.get(1).getTourName());

        verify(tourItemService, times(1)).findBySeller(mockAuth);
    }


}
