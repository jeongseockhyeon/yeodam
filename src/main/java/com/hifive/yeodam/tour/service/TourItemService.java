package com.hifive.yeodam.tour.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.category.entity.Category;
import com.hifive.yeodam.category.repository.CategoryRepository;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.image.service.ImageService;
import com.hifive.yeodam.item.entity.ItemImage;
import com.hifive.yeodam.item.repository.ItemImageRepository;
import com.hifive.yeodam.item.service.ItemImageService;
import com.hifive.yeodam.reservation.entity.Reservation;
import com.hifive.yeodam.reservation.repository.ReservationRepository;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.GuideRepository;
import com.hifive.yeodam.seller.service.GuideService;
import com.hifive.yeodam.seller.service.SellerService;
import com.hifive.yeodam.tour.dto.*;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.tour.entity.TourCategory;
import com.hifive.yeodam.tour.entity.TourGuide;
import com.hifive.yeodam.tour.repository.TourCategoryRepository;
import com.hifive.yeodam.tour.repository.TourGuideRepository;
import com.hifive.yeodam.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class TourItemService {

    private final TourRepository tourRepository;
    private final TourCategoryRepository tourCategoryRepository;
    private final TourGuideRepository tourGuideRepository;
    private final GuideService guideService;
    private final SellerService sellerService;
    private final CategoryRepository categoryRepository;
    private final GuideRepository guideRepository;

    private final static int tourStock = 1;
    private final static boolean reservation = true;
    private final static double defaultRate = 0.0;
    private final static boolean defaultActive = true;
    private final ItemImageService itemImageService;
    private final ItemImageRepository itemImageRepository;
    private final ImageService imageService;
    private final ReservationRepository reservationRepository;


    /*상품_여행 등록*/
    @Transactional
    public TourItemResDto saveTourItem(TourItemReqDto tourItemReqDto,Auth auth)  {

        Seller seller = sellerService.getSellerByAuth(auth);

        Tour tourItem = Tour.builder()
                .seller(seller)
                .itemName(tourItemReqDto.getTourName())
                .region(tourItemReqDto.getTourRegion())
                .period(tourItemReqDto.getTourPeriod())
                .description(tourItemReqDto.getTourDesc())
                .price(Integer.parseInt(tourItemReqDto.getTourPrice()))
                .maximum(Integer.parseInt(tourItemReqDto.getMaximum()))
                .stock(tourStock)
                .rate(defaultRate)
                .reservation(reservation)
                .active(defaultActive)
                .build();

        Tour savedTour = tourRepository.save(tourItem);

        /*여행_카테고리 저장*/
        List<Long> categoryIdList = convertToList(tourItemReqDto.getCategoryIdList());
        if(isNotNullCheck(categoryIdList)) {
            for(Long categoryId : categoryIdList){
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
        List<Long> guideIdList = convertToList(tourItemReqDto.getGuideIdList());
        if(isNotNullCheck(guideIdList)) {
            for(Long guideId : guideIdList){
                Guide guide = guideService.getGuideById(guideId);
                TourGuide tourGuide = TourGuide.builder()
                        .tour(tourItem)
                        .guide(guide)
                        .build();
                tourGuideRepository.save(tourGuide);
            }
        }

        /*여행 상품 이미지 저장*/
        if(tourItemReqDto.getTourImages() != null ){
            for(MultipartFile imageFile : tourItemReqDto.getTourImages()){
                itemImageService.save(imageFile,savedTour);
            }
        }
        return new TourItemResDto(savedTour);
    }

    /*필터링 적용, 커서 페이지네이션 조회*/
    @Transactional(readOnly = true)
    public Slice<TourItemResDto> getSearchFilterTour(SearchFilterDto searchFilterDto) {
        Slice<Tour> filterTours = tourRepository.searchByFilterAndActive(searchFilterDto);
        List<TourItemResDto> tourItemResDtoList = filterTours.getContent().stream()
                .map(TourItemResDto::new)
                .toList();
        return new SliceImpl<>(tourItemResDtoList, filterTours.getPageable(), filterTours.hasNext());
    }

    /*상품_여행 단일 조회*/
    @Transactional(readOnly = true)
    public TourItemResDto findById(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.ITEM_NOT_FOUND));
        return new TourItemResDto(tour);
    }

    /*상품_여행 수정*/
    @Transactional
    public TourItemResDto update(Long id, TourItemUpdateReqDto tourItemUpdateReqDto) {
        Tour targetTour = tourRepository.findById(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.ITEM_NOT_FOUND));
        targetTour.updateItem(tourItemUpdateReqDto.getTourName(),tourItemUpdateReqDto.getTourDesc(),Integer.parseInt(tourItemUpdateReqDto.getTourPrice()));
        targetTour.updateSubItem(tourItemUpdateReqDto.getTourRegion(), tourItemUpdateReqDto.getTourPeriod(),Integer.parseInt(tourItemUpdateReqDto.getMaximum()));

        //카테고리 추가
        List<Long> addCategoryIdList = convertToList(tourItemUpdateReqDto.getAddCategoryIds());
        if(isNotNullCheck(addCategoryIdList)) {
            for(Long categoryId : addCategoryIdList) {
                // Category가 존재하는지 확인
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new CustomException(CustomErrorCode.CATEGORY_NOT_FOUND));

                // 이미 해당 Tour와 Category의 조합이 존재하는지 확인
                boolean exists = tourCategoryRepository.existsByTourAndCategory(targetTour, category);

                if (!exists) {
                    // 존재하지 않으면 새로운 TourCategory 추가
                    TourCategory addTourCategory = TourCategory.builder()
                            .tour(targetTour)
                            .category(category)
                            .build();
                    tourCategoryRepository.save(addTourCategory);
                }
            }
        }

        //카테고리 삭제
        List<Long> removeCategoryIdList = convertToList(tourItemUpdateReqDto.getRemoveCategoryIds());
        if (isNotNullCheck(removeCategoryIdList)) {
            List<Category> categories = categoryRepository.findAllById(removeCategoryIdList);
            for (Category category : categories) {
                tourCategoryRepository.deleteByTourAndCategory(targetTour, category);
            }
        }

        //가이드 추가
        List<Long> addGuideIdList = convertToList(tourItemUpdateReqDto.getAddGuideIds());
        log.info("addGuideIdList: {}", addGuideIdList);
        if(isNotNullCheck(addGuideIdList)){
            for(Long guideId : addGuideIdList){
                Guide guide = guideRepository.findById(guideId)
                        .orElseThrow(()->new CustomException(CustomErrorCode.GUIDE_NOT_FOUND));
                TourGuide addTourGuide = TourGuide.builder()
                        .guide(guide)
                        .tour(targetTour)
                        .build();
                tourGuideRepository.save(addTourGuide);
            }
        }

        //가이드 삭제
        List<Long> removeGuideIdList = convertToList(tourItemUpdateReqDto.getRemoveGuideIds());
        log.info("removeGuideIdList: {}", removeGuideIdList);
        if(isNotNullCheck(removeGuideIdList)){
            List<Guide> guides = guideRepository.findAllById(removeGuideIdList);
            for(Guide guide : guides){
                log.info("deleting guideName: {}", guide.getName());
                tourGuideRepository.deleteByTourAndGuide(targetTour,guide);
            }
        }
        //상품 이미지 추가
        if(isNotNullCheck(tourItemUpdateReqDto.getAddTourImages())){
            for(MultipartFile imageFile : tourItemUpdateReqDto.getAddTourImages()){
                itemImageService.save(imageFile,targetTour);
            }
        }

        //상품 이미지 삭제
        List<Long> removeTourImageList = convertToList(tourItemUpdateReqDto.getRemoveImageIds());
        if(isNotNullCheck(removeTourImageList)){
            for(Long imageId : removeTourImageList){
                itemImageService.delete(imageId);
            }
        }

        return new TourItemResDto(targetTour);
    }

    /*상품_여행 삭제*/
    @Transactional
    public void delete(Long id) {
        Tour targetTour = tourRepository.findById(id)
                        .orElseThrow(() -> new CustomException(CustomErrorCode.ITEM_NOT_FOUND));
        List<ItemImage> targetItemImage = itemImageRepository.findByItemId(id);
        for(ItemImage itemImage : targetItemImage){
            imageService.delete(itemImage.getStorePath());
        }
        tourRepository.delete(targetTour);
    }

    /*상품_여행 판매자 조회*/
    @Transactional(readOnly = true)
    public Slice<TourItemResDto> findBySeller(Long cursorId, int pageSize,Auth auth){
        Seller seller = sellerService.getSellerByAuth(auth);
        Slice<Tour> sellerTours = tourRepository.findBySeller(cursorId,pageSize,seller);
        List<TourItemResDto> tourItemResDtoList =  sellerTours.stream()
                .map(TourItemResDto::new)
                .toList();
        return new SliceImpl<>(tourItemResDtoList, sellerTours.getPageable(), sellerTours.hasNext());
    }

    /**/

    /*상품 내 가이드 예약 일정 조회*/
    @Transactional(readOnly = true)
    public List<ReservationInTourResDto> findReservationByGuide(Long guideId){
        Guide guide = guideService.getGuideById(guideId);
        List<Reservation> reservations = reservationRepository.findByGuide(guide);

        return reservations.stream()
                .map(ReservationInTourResDto::new)
                .toList();
    }

    public boolean checkDuplicateGuide(Long id){return tourGuideRepository.existsById(id);}

    //formData로 인해 문자열로 들어오는 id들을 리스트 List<Long>으로 변환
    public List<Long> convertToList(String arg) {
        if (arg == null || arg.trim().equals("[]")) {
            return new ArrayList<>(); // 빈 리스트 반환
        }
        return Arrays.stream(arg.replaceAll("[\\[\\]\\s]", "").split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .toList();
    }


    //리스트가 비어있는지 검사
    public boolean isNotNullCheck(List<?> arg){
        return arg != null && !arg.isEmpty();
    }
}
