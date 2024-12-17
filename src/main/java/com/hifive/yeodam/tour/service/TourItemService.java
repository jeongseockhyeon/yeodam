package com.hifive.yeodam.tour.service;

import com.hifive.yeodam.category.entity.Category;
import com.hifive.yeodam.category.repository.CategoryRepository;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.GuideRepository;
import com.hifive.yeodam.seller.repository.SellerRepository;
import com.hifive.yeodam.tour.dto.SearchFilterDto;
import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.dto.TourItemUpdateReqDto;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.tour.entity.TourCategory;
import com.hifive.yeodam.tour.entity.TourGuide;
import com.hifive.yeodam.tour.repository.TourCategoryRepository;
import com.hifive.yeodam.tour.repository.TourGuideRepository;
import com.hifive.yeodam.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TourItemService {

    private final TourRepository tourRepository;
    private final CategoryRepository categoryRepository;
    private final TourCategoryRepository tourCategoryRepository;
    private final SellerRepository sellerRepository;
    private final GuideRepository guideRepository;
    private final TourGuideRepository tourGuideRepository;

    private final static int tourStock = 1;
    private final static boolean reservation = true;

    /*상품_여행 등록*/
    public Tour saveTourItem(TourItemReqDto tourItemReqDto) {

        Seller seller = sellerRepository.findById(tourItemReqDto.getSellerId())
                .orElseThrow(() -> new RuntimeException("해당 가이드가 존재하지 않습니다"));

        Tour itemTour = Tour.builder()
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

        Tour savedTour = tourRepository.save(itemTour);

        /*여행_카테고리 저장*/
        if(tourItemReqDto.getCategoryIdList() != null ) {
            for(Long categoryId : tourItemReqDto.getCategoryIdList()){
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("해당 카테고리는 존재하지 않습니다."));
                TourCategory tourCategory = TourCategory.builder()
                        .tour(itemTour)
                        .category(category)
                        .build();
                tourCategoryRepository.save(tourCategory);
            }
        }
        /*여행_가이드 저장*/
        if(tourItemReqDto.getGuideIdList() != null ) {
            for(Long guideId : tourItemReqDto.getGuideIdList()){
                Guide guide = guideRepository.findById(guideId)
                        .orElseThrow(() -> new RuntimeException("해당 가이드가 존재하지 않습니다"));
                TourGuide tourGuide = TourGuide.builder()
                        .tour(itemTour)
                        .guide(guide)
                        .build();
                tourGuideRepository.save(tourGuide);
            }

        }
        return savedTour;
    }
    /*상품_여행 목록 조회*/
    public List<Tour> findAll() {
        return tourRepository.findAll();
    }

    /*카테고리 적용 조회*/
    public List<Tour> getSearchFilterTour(SearchFilterDto searchFilterDto) {
        return tourRepository.searchByFilter(searchFilterDto);
    }

    /*상품_여행 단일 조회*/
    public Tour findById(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 여행을 찾을 수 없습니다"));
    }
    /*상품_여행 수정*/
    public Tour update(Long id, TourItemUpdateReqDto tourItemUpdateReqDto) {
        Tour targetTour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 여행을 찾을 수 없습니다"));
        targetTour.updateItem(tourItemUpdateReqDto.getTourName(),tourItemUpdateReqDto.getPrice());
        targetTour.updateSubItem(tourItemUpdateReqDto.getRegion(), tourItemUpdateReqDto.getPeriod(), tourItemUpdateReqDto.getDescription());

        return tourRepository.save(targetTour);
    }
    /*상품_여행 삭제*/
    public void delete(Long id) {
        tourRepository.deleteById(id);
    }

}
