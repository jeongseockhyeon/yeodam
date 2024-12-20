package com.hifive.yeodam.tour.service;

import com.hifive.yeodam.category.entity.Category;
import com.hifive.yeodam.category.repository.CategoryRepository;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.GuideRepository;
import com.hifive.yeodam.seller.service.GuideService;
import com.hifive.yeodam.seller.service.SellerService;
import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.dto.TourItemResDto;
import com.hifive.yeodam.tour.dto.TourItemUpdateReqDto;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.tour.entity.TourCategory;
import com.hifive.yeodam.tour.entity.TourGuide;
import com.hifive.yeodam.tour.repository.TourCategoryRepository;
import com.hifive.yeodam.tour.repository.TourGuideRepository;
import com.hifive.yeodam.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class TourItemService {

    private final TourRepository tourRepository;
    private final TourCategoryRepository tourCategoryRepository;
    private final TourGuideRepository tourGuideRepository;
    private final GuideService guideService;
    private final SellerService sellerService;

    private final static int tourStock = 1;
    private final static boolean reservation = true;
    private final CategoryRepository categoryRepository;
    private final GuideRepository guideRepository;


    /*상품_여행 등록*/
    public TourItemResDto saveTourItem(TourItemReqDto tourItemReqDto) {

        Seller seller = sellerService.getSellerById(tourItemReqDto.getSellerId());

        Tour tourItem = Tour.builder()
                .seller(seller)
                .itemName(tourItemReqDto.getTourName())
                .region(tourItemReqDto.getTourRegion())
                .period(tourItemReqDto.getTourPeriod())
                .description(tourItemReqDto.getTourDesc())
                .price(tourItemReqDto.getTourPrice())
                .maximum(tourItemReqDto.getMaximum())
                .stock(tourStock)
                .reservation(reservation)
                .build();

        Tour savedTour = tourRepository.save(tourItem);

        /*여행_카테고리 저장*/
        if(tourItemReqDto.getCategoryIdList() != null ) {
            for(Long categoryId : tourItemReqDto.getCategoryIdList()){
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new CustomException(CustomErrorCode.CATEGORY_NOT_FOUND));
                TourCategory tourCategory = TourCategory.builder()
                        .tour(tourItem)
                        .category(category)
                        .build();
                tourCategoryRepository.save(tourCategory);
            }
        }
        /*여행_가이드 저장*/
        if(tourItemReqDto.getGuideIdList() != null ) {
            for(Long guideId : tourItemReqDto.getGuideIdList()){
                Guide guide = guideService.getGuideById(guideId);
                TourGuide tourGuide = TourGuide.builder()
                        .tour(tourItem)
                        .guide(guide)
                        .build();
                tourGuideRepository.save(tourGuide);
            }

        }
        return new TourItemResDto(savedTour);
    }
    /*상품_여행 목록 조회*/
    public List<TourItemResDto> findAll() {
        List<Tour> tours = tourRepository.findAll();
        List<TourItemResDto> tourItemResDtos = new ArrayList<>();
        for(Tour tour : tours){
            TourItemResDto tourItemResDto = new TourItemResDto(tour);
            tourItemResDtos.add(tourItemResDto);
        }
        return tourItemResDtos;
    }

    /*카테고리 적용 조회*/
    public List<TourItemResDto> getSearchFilterTour(SearchFilterDto searchFilterDto) {
        List<Tour> filterTours = tourRepository.searchByFilter(searchFilterDto);
        List<TourItemResDto> tourItemResDtos = new ArrayList<>();
        for(Tour tour : filterTours){
            TourItemResDto tourItemResDto = new TourItemResDto(tour);
            tourItemResDtos.add(tourItemResDto);
        }
        return tourItemResDtos;
    }

    /*상품_여행 단일 조회*/
    public TourItemResDto findById(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.ITEM_NOT_FOUND));
        return new TourItemResDto(tour);
    }

    /*상품_여행 수정*/
    public TourItemResDto update(Long id, TourItemUpdateReqDto tourItemUpdateReqDto) {
        Tour targetTour = tourRepository.findById(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.ITEM_NOT_FOUND));
        targetTour.updateItem(tourItemUpdateReqDto.getTourName(),tourItemUpdateReqDto.getDescription(),tourItemUpdateReqDto.getPrice());
        targetTour.updateSubItem(tourItemUpdateReqDto.getRegion(), tourItemUpdateReqDto.getPeriod(),tourItemUpdateReqDto.getMaximum());

        if(tourItemUpdateReqDto.getAddCategoryIds() != null){
            for(Long categoryId : tourItemUpdateReqDto.getAddCategoryIds()){
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new CustomException(CustomErrorCode.CATEGORY_NOT_FOUND));//예외처리 추가
                TourCategory addTourCategory = TourCategory.builder()
                                .tour(targetTour)
                                .category(category)
                                .build();
                tourCategoryRepository.save(addTourCategory);

            }
        }
        if(tourItemUpdateReqDto.getRemoveCategoryIds() != null){
            for(Long categoryId : tourItemUpdateReqDto.getRemoveCategoryIds()){
                tourCategoryRepository.deleteByCategoryId(categoryId);
            }
        }
        if(tourItemUpdateReqDto.getAddGuideIds() != null){
            for(Long guideId : tourItemUpdateReqDto.getAddGuideIds()){
                Guide guide = guideRepository.findById(guideId)
                        .orElseThrow(()->new CustomException(CustomErrorCode.GUIDE_NOT_FOUND));
                TourGuide addTourGuide = TourGuide.builder()
                        .guide(guide)
                        .tour(targetTour)
                        .build();
                tourGuideRepository.save(addTourGuide);
            }
        }
        if(tourItemUpdateReqDto.getRemoveGuideIds() != null){
            for(Long guideId : tourItemUpdateReqDto.getRemoveGuideIds()){
                Guide guide = guideRepository.findById(guideId)
                        .orElseThrow(()->new CustomException(CustomErrorCode.GUIDE_NOT_FOUND));
                tourGuideRepository.deleteByGuide(guide);
            }
        }

        return new TourItemResDto(targetTour);
    }
    /*상품_여행 삭제*/
    public void delete(Long id) {
        Tour targetTour = tourRepository.findById(id)
                        .orElseThrow(() -> new CustomException(CustomErrorCode.ITEM_NOT_FOUND));
        tourRepository.delete(targetTour);
    }

}
