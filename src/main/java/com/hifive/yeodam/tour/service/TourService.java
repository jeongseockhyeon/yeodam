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
    /*여행 단일 조회*/
    public Tour findById(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 여행을 찾을 수 없습니다"));
    }
    /*여행 업데이트*/
    public Tour update(Long id,TourReqDto tourReqDto) {
        Tour targetTour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 투어를 찾을 수 없습니다"));

        targetTour.updateTour(tourReqDto.getRegion(),tourReqDto.getPeriod(),tourReqDto.getDescription(),tourReqDto.getPrice());

        return tourRepository.save(targetTour);
    }
    /*여행 삭제*/
    public void delete(Long id) {
        tourRepository.deleteById(id);
    }


}
