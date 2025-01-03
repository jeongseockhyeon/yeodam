package com.hifive.yeodam.tourItemTest;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.category.entity.Category;
import com.hifive.yeodam.category.repository.CategoryRepository;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.image.service.ImageService;
import com.hifive.yeodam.item.entity.ItemImage;
import com.hifive.yeodam.item.repository.ItemImageRepository;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.GuideRepository;
import com.hifive.yeodam.seller.service.GuideService;
import com.hifive.yeodam.seller.service.SellerService;
import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.dto.TourItemResDto;
import com.hifive.yeodam.tour.dto.TourItemUpdateReqDto;
import com.hifive.yeodam.tour.entity.TourCategory;
import com.hifive.yeodam.tour.entity.TourGuide;
import com.hifive.yeodam.tour.repository.TourCategoryRepository;
import com.hifive.yeodam.tour.repository.TourGuideRepository;
import com.hifive.yeodam.tour.repository.TourRepository;
import com.hifive.yeodam.tour.service.TourItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.hifive.yeodam.tour.entity.Tour;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TourItemTest {
    private final static String tourName = "제주도";
    private final static String tourDesc = "test";
    private final static String tourPeriod = "1일";
    private final static String tourRegion = "제주";
    private final static String tourPrice = "100";
    private final static String tourMaximum = "2";

    @InjectMocks
    private TourItemService tourItemService;

    @Mock
    private SellerService sellerService;

    @Mock
    private TourRepository tourRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TourCategoryRepository tourCategoryRepository;

    @Mock
    private GuideService guideService;

    @Mock
    private GuideRepository guideRepository;

    @Mock
    private TourGuideRepository tourGuideRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private ItemImageRepository itemImageRepository;

    @Test
    void testSaveTourItem() {
        // given
        TourItemReqDto mockRequest = mock(TourItemReqDto.class);
        when(mockRequest.getTourName()).thenReturn(tourName);
        when(mockRequest.getTourDesc()).thenReturn(tourDesc);
        when(mockRequest.getTourPeriod()).thenReturn(tourPeriod);
        when(mockRequest.getTourRegion()).thenReturn(tourRegion);
        when(mockRequest.getTourPrice()).thenReturn(tourPrice);
        when(mockRequest.getMaximum()).thenReturn(tourMaximum);
        when(mockRequest.getCategoryIdList()).thenReturn("[1, 2]");
        when(mockRequest.getGuideIdList()).thenReturn("[1, 2]");

        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockRequest.getTourImages()).thenReturn(List.of(mockImage));

        Auth mockAuth = mock(Auth.class);
        Seller mockSeller = new Seller();
        when(sellerService.getSellerByAuth(mockAuth)).thenReturn(mockSeller);

        Tour mockTour = Tour.builder().build();
        when(tourRepository.save(any(Tour.class))).thenReturn(mockTour);

        Category mockCategory1 = Category.builder().build();
        Category mockCategory2 = Category.builder().build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory1));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(mockCategory2));

        Guide mockGuide1 = new Guide();
        Guide mockGuide2 = new Guide();
        when(guideService.getGuideById(1L)).thenReturn(mockGuide1);
        when(guideService.getGuideById(2L)).thenReturn(mockGuide2);

        when(mockImage.getOriginalFilename()).thenReturn("image.jpg");
        when(imageService.upload(mockImage)).thenReturn("/images/image.jpg");

        // when
        TourItemResDto response = tourItemService.saveTourItem(mockRequest, mockAuth);

        // then
        assertNotNull(response);
        verify(sellerService).getSellerByAuth(mockAuth);
        verify(tourRepository).save(any(Tour.class));
        verify(categoryRepository, times(2)).findById(anyLong());
        verify(tourCategoryRepository, times(2)).save(any(TourCategory.class));
        verify(guideService, times(2)).getGuideById(anyLong());
        verify(tourGuideRepository, times(2)).save(any(TourGuide.class));
        verify(imageService).upload(mockImage);
        verify(itemImageRepository).save(any(ItemImage.class));
    }

    @Test
    @DisplayName("상품_여행 목록 조회 테스트")
    public void getSearchFilterTourTest() {

        //given
        Long cursorId = 0L;
        int testCount = 4;
        int pageSize = 2;
        Pageable pageable = PageRequest.of(0, pageSize);
        List<Tour> mockTours = new ArrayList<>();
        for (int i = 0; i < testCount; i++) {

            Tour mockTour = Tour.builder().build();
            mockTours.add(mockTour);
        }
        Slice<Tour> mockSlice = new SliceImpl<>(mockTours, pageable, true);
        SearchFilterDto searchFilterDto = mock(SearchFilterDto.class);
        when(searchFilterDto.getCursorId()).thenReturn(cursorId);
        when(searchFilterDto.getPageSize()).thenReturn(pageSize);

        when(tourRepository.searchByFilterAndActive(searchFilterDto)).thenReturn(mockSlice);

        //when
        Slice<TourItemResDto> result = tourItemService.getSearchFilterTour(searchFilterDto);

        //then
        assertEquals(pageSize, result.getSize());
        verify(tourRepository, times(1)).searchByFilterAndActive(searchFilterDto);
    }
    @Test
    @DisplayName("카테고리 필터링 테스트")
    public void tourItemSearchFilterTest()  {
        // given
        List<Tour> mockTours = new ArrayList<>();

        int pageSize = 2;
        Category categoryMock = mock(Category.class);
        when(categoryMock.getName()).thenReturn("액티비티");

        TourCategory tourCategoryMock = mock(TourCategory.class);
        when(tourCategoryMock.getCategory()).thenReturn(categoryMock);

        Tour tourMock = mock(Tour.class);
        when(tourMock.getTourCategories()).thenReturn(List.of(tourCategoryMock));

        mockTours.add(tourMock);

        // filter 모킹
        SearchFilterDto filterMock = mock(SearchFilterDto.class);
        when(filterMock.getCategories()).thenReturn(List.of("액티비티"));

        Pageable pageable = PageRequest.of(0, pageSize);
        Slice<Tour> mockSlice = new SliceImpl<>(mockTours, pageable, true);

        when(tourRepository.searchByFilterAndActive( filterMock)).thenReturn(mockSlice);

        // when
        Slice<TourItemResDto> results = tourItemService.getSearchFilterTour(filterMock);

        // then
        assertThat(results).hasSize(1);
        assertEquals("액티비티", results.getContent().getFirst().getCategoryResDtoList().getFirst().getName());
        assertEquals("액티비티",filterMock.getCategories().getFirst());
        verify(tourRepository, times(1)).searchByFilterAndActive(filterMock);
    }

    @Test
    @DisplayName("키워드 필터링 테스트")
    public void tourItemSearchFilterKeywordTest()  {
        //given
        List<Tour> mockTours = new ArrayList<>();

        int pageSize = 2;

        Tour tourMock = mock(Tour.class);
        when(tourMock.getItemName()).thenReturn(tourName);

        mockTours.add(tourMock);

        String keyword = "제주";

        SearchFilterDto filterMock = mock(SearchFilterDto.class);
        when(filterMock.getKeyword()).thenReturn(keyword);

        Pageable pageable = PageRequest.of(0, pageSize);
        Slice<Tour> mockSlice = new SliceImpl<>(mockTours, pageable, true);

        when(tourRepository.searchByFilterAndActive(filterMock)).thenReturn(mockSlice);

        // when
        Slice<TourItemResDto> results = tourItemService.getSearchFilterTour(filterMock);

        //then
        assertThat(results).hasSize(1);
        assertEquals("제주도",results.getContent().getFirst().getTourName());
        assertEquals("제주",filterMock.getKeyword());

        verify(tourRepository, times(1)).searchByFilterAndActive(filterMock);
    }

    @Test
    @DisplayName("지역 필터링 테스트")
    public void tourItemSearchFilterRegionTest() {
        //given
        List<Tour> mockTours = new ArrayList<>();

        int pageSize = 2;

        Tour tourMock = mock(Tour.class);
        when(tourMock.getRegion()).thenReturn(tourRegion);

        mockTours.add(tourMock);

        SearchFilterDto filterMock = mock(SearchFilterDto.class);
        when(filterMock.getRegion()).thenReturn(tourRegion);

        Pageable pageable = PageRequest.of(0, pageSize);
        Slice<Tour> mockSlice = new SliceImpl<>(mockTours, pageable, true);

        when(tourRepository.searchByFilterAndActive(filterMock)).thenReturn(mockSlice);

        //when
        Slice<TourItemResDto> results = tourItemService.getSearchFilterTour(filterMock);

        //then
        assertThat(results).hasSize(1);
        assertEquals(tourRegion,results.getContent().getFirst().getTourRegion());
        assertEquals(tourRegion,filterMock.getRegion());
        verify(tourRepository, times(1)).searchByFilterAndActive(filterMock);

    }

    @Test
    @DisplayName("기간 필터링 테스트")
    public void tourItemSearchFilterPeriodTest() {
        //given
        List<Tour> mockTours = new ArrayList<>();

        int pageSize = 2;

        Tour tourMock = mock(Tour.class);
        when(tourMock.getPeriod()).thenReturn(tourPeriod);

        mockTours.add(tourMock);

        SearchFilterDto filterMock = mock(SearchFilterDto.class);
        when(filterMock.getPeriod()).thenReturn(tourPeriod);

        Pageable pageable = PageRequest.of(0, pageSize);
        Slice<Tour> mockSlice = new SliceImpl<>(mockTours, pageable, true);

        when(tourRepository.searchByFilterAndActive(filterMock)).thenReturn(mockSlice);

        //when
        Slice<TourItemResDto> results = tourItemService.getSearchFilterTour(filterMock);

        //then
        assertThat(results).hasSize(1);
        assertEquals(tourPeriod,results.getContent().getFirst().getTourPeriod());
        assertEquals(tourPeriod,filterMock.getPeriod());
        verify(tourRepository, times(1)).searchByFilterAndActive(filterMock);

    }

    @Test
    @DisplayName("상품_여행 단일 조회 테스트")
    public void tourItemFindByIdSuccessTest() {
        //given
        Long tourItemId = 1L;

        Tour tourMock = mock(Tour.class);
        when(tourMock.getId()).thenReturn(tourItemId);
        when(tourMock.getItemName()).thenReturn(tourName);

        when(tourRepository.findById(tourItemId)).thenReturn(Optional.of(tourMock));

        //when
        TourItemResDto result = tourItemService.findById(tourItemId);

        //then
        assertEquals(tourName,result.getTourName());
        assertEquals(tourItemId,result.getId());
        verify(tourRepository, times(1)).findById(tourItemId);
    }


    @Test
    @DisplayName("상품_여행 수정 성공 테스트")
    public void tourItemUpdateSuccessTest() {
        // given
        Long tourId = 1L;
        Long categoryId1 = 1L;
        Long categoryId2 = 2L;
        Long guideId1 = 1L;
        Long guideId2 = 2L;

        TourItemUpdateReqDto updateReq = mock(TourItemUpdateReqDto.class);
        when(updateReq.getTourName()).thenReturn("Updated Name");
        when(updateReq.getTourDesc()).thenReturn("Updated Description");
        when(updateReq.getTourPrice()).thenReturn("100");
        when(updateReq.getTourRegion()).thenReturn("Updated Region");
        when(updateReq.getTourPeriod()).thenReturn("Updated Period");
        when(updateReq.getMaximum()).thenReturn("10");
        when(updateReq.getAddCategoryIds()).thenReturn(List.of(categoryId1).toString());
        when(updateReq.getRemoveCategoryIds()).thenReturn(List.of(categoryId2).toString());
        when(updateReq.getAddGuideIds()).thenReturn(List.of(guideId1).toString());
        when(updateReq.getRemoveGuideIds()).thenReturn(List.of(guideId2).toString());

        Tour tour = mock(Tour.class);
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));

        Category category1 = mock(Category.class);
        Category category2 = mock(Category.class);
        when(categoryRepository.findById(categoryId1)).thenReturn(Optional.of(category1));
        when(categoryRepository.findById(categoryId2)).thenReturn(Optional.of(category2));

        Guide guide1 = mock(Guide.class);
        Guide guide2 = mock(Guide.class);
        when(guideRepository.findById(guideId1)).thenReturn(Optional.of(guide1));
        when(guideRepository.findById(guideId2)).thenReturn(Optional.of(guide2));

        // when
        TourItemResDto result = tourItemService.update(tourId, updateReq);

        // then
        verify(tourRepository, times(1)).findById(tourId);
        verify(tour).updateItem("Updated Name", "Updated Description", 100);
        verify(tour).updateSubItem("Updated Region", "Updated Period", 10);

        //카테고리 수정
        verify(categoryRepository, times(1)).findById(categoryId1);
        verify(tourCategoryRepository, times(1)).save(any(TourCategory.class));

        verify(categoryRepository, times(1)).findById(categoryId2);
        verify(tourCategoryRepository, times(1)).deleteByTourAndCategory(tour,category2);

        // Add Guides
        verify(guideRepository, times(1)).findById(guideId1);
        verify(tourGuideRepository, times(1)).save(any(TourGuide.class));

        // Remove Guides
        verify(guideRepository, times(1)).findById(guideId2);
        verify(tourGuideRepository, times(1)).deleteByTourAndGuide(tour,guide2);

        // Result Verification
        assertNotNull(result);
        verify(tour, times(1)).getId();

    }
    @Test
    @DisplayName("상품_여행 수정 실패 - 여행이 존재하지 않을 때")
    void updateTourItemNotFound() {
        // given
        Long tourId = 1L;
        TourItemUpdateReqDto updateReq = mock(TourItemUpdateReqDto.class);

        when(tourRepository.findById(tourId)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> tourItemService.update(tourId, updateReq)
        );
        assertEquals(CustomErrorCode.ITEM_NOT_FOUND, exception.getCustomErrorCode());
        verify(tourRepository, times(1)).findById(tourId);
    }

/*    @Test
    @DisplayName("상품_여행 수정 실패 - 카테고리 존재하지 않을 때")
    void updateTourItemCategoryNotFound() {
        // given
        Long tourId = 1L;
        Long categoryId = 1L;
        TourItemUpdateReqDto updateReq = mock(TourItemUpdateReqDto.class);

        when(tourRepository.findById(tourId)).thenReturn(Optional.of(mock(Tour.class)));
        when(updateReq.getAddCategoryIds()).thenReturn(String.valueOf(List.of(categoryId)));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> tourItemService.update(tourId, updateReq)
        );
        assertEquals(CustomErrorCode.CATEGORY_NOT_FOUND, exception.getCustomErrorCode());
        verify(categoryRepository, times(1)).findById(categoryId);
    }*/


    @Test
    @DisplayName("상품_여행 삭제 성공 테스트")
    public void TourItemDeleteSuccessTest() {
        //given
        Long tourItemId = 1L;
        Tour tour = mock(Tour.class);
        when(tourRepository.findById(tourItemId)).thenReturn(Optional.of(tour));
        doNothing().when(tourRepository).delete(tour);

        //when
       tourItemService.delete(tourItemId);

        //then
        verify(tourRepository,times(1)).delete(tour);
    }
/*    @Test
    @DisplayName("업체별 여행 상품 목록 조회")
    public void tourItemFindBySellerTest(){
        //given
        Auth mockAuth = mock(Auth.class);
        Seller mockSeller = mock(Seller.class);

        Tour mockTour1 = mock(Tour.class);
        when(mockTour1.getId()).thenReturn(1L);
        when(mockTour1.getItemName()).thenReturn("test1");

        Tour mockTour2 = mock(Tour.class);
        when(mockTour2.getId()).thenReturn(2L);
        when(mockTour2.getItemName()).thenReturn("test2");

        List<Tour> mockTourList = Arrays.asList(mockTour1, mockTour2);

        when(sellerService.getSellerByAuth(mockAuth)).thenReturn(mockSeller);
        when(tourRepository.findBySeller(mockSeller)).thenReturn(mockTourList);

        //when
        List<TourItemResDto> result = tourItemService.findBySeller(mockAuth);

        //then
        assertEquals(2, result.size());
        assertEquals("test1", result.get(0).getTourName());
        assertEquals("test2", result.get(1).getTourName());
        verify(tourRepository, times(1)).findBySeller(mockSeller);
    }*/

/*    @Test
    @DisplayName("상품 활성 상태 변경")
    public void tourItemUpdateActive(){
        //given
        Long tourItemId = 1L;

        Tour tour = mock(Tour.class);
        when(tourRepository.findById(tourItemId)).thenReturn(Optional.of(tour));
        when(tour.isActive()).thenReturn(false);

        //when
        tourItemService.updateActive(tourItemId,false);

        //then
        assertFalse(tour.isActive());
        verify(tourRepository, times(1)).findById(tourItemId);
        verify(tour, times(1)).updateActive(false);
    }*/
}
