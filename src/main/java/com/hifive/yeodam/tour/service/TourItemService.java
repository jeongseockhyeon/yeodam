package com.hifive.yeodam.tour.service;

import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.tour.entity.Tour;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TourItemService {
    private final ItemRepository itemRepository;

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
}
