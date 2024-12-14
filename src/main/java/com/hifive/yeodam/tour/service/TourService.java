package com.hifive.yeodam.tour.service;

import com.hifive.yeodam.tour.dto.TourReqDto;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TourService {

    private final TourRepository tourRepository;

    /*여행 등록*/
    public Tour save(TourReqDto tourReqDto) {
        Tour newTour = Tour.builder()
                .region(tourReqDto.getRegion())
                .period(tourReqDto.getPeriod())
                .description(tourReqDto.getDescription())
                .price(tourReqDto.getPrice())
                .build();

        return tourRepository.save(newTour);
    }
    /*여행 전체 조회*/
    public List<Tour> findAll() {
        return tourRepository.findAll();
    }
}
