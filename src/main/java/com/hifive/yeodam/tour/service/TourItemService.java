package com.hifive.yeodam.tour.service;

import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.tour.dto.TourItemUpdateReqDto;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TourItemService {
    private final ItemRepository itemRepository;
    private final TourRepository tourRepository;

    /*상품_여행 등록*/
    public Tour saveTourItem(TourItemReqDto tourItemReqDto) {
        Tour itemTour = Tour.builder()
                .sellerId(tourItemReqDto.getSellerId())
                .itemName(tourItemReqDto.getTourName())
                .region(tourItemReqDto.getTourRegion())
                .period(tourItemReqDto.getTourPeriod())
                .description(tourItemReqDto.getTourDesc())
                .price(tourItemReqDto.getTourPrice())
                .build();

        return itemRepository.save(itemTour);
    }
    /*상품_여행 목록 조회*/
    public List<Tour> findAll() {
        return tourRepository.findAll();
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

        targetTour.updateItem(tourItemUpdateReqDto.getTourName());
        targetTour.updateSubItem(tourItemUpdateReqDto.getRegion(), tourItemUpdateReqDto.getPeriod(), tourItemUpdateReqDto.getDescription());

        return tourRepository.save(targetTour);
    }

}
