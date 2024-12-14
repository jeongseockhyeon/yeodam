package com.hifive.yeodam.tourTest;

import com.hifive.yeodam.tour.dto.TourReqDto;
import com.hifive.yeodam.tour.service.TourService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.hifive.yeodam.tour.entity.Tour;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TourTest {

    @Autowired
    TourService tourService;

    @Test
    public void tourSaveTest(){
        //given
        String testRegion = "제주";
        String period = "1일";
        String des  = "test";
        int price = 123123;

        TourReqDto tourReqDto = new TourReqDto();
        tourReqDto.setRegion(testRegion);
        tourReqDto.setPeriod(period);
        tourReqDto.setDescription(des);
        tourReqDto.setPrice(price);

        //when
        Tour tour = tourService.save(tourReqDto);

        //then
        assertEquals(testRegion,tour.getRegion());
        assertEquals(period,tour.getPeriod());
        assertEquals(des,tour.getDescription());
        assertEquals(price,tour.getPrice());
    }
    @Test
    public void tourFindAll(){
        //given
        int count = 1;

        //when
        List<Tour> tours = tourService.findAll();

        //then
        assertEquals(count,tours.size());
    }
    @Test
    public void tourFindByIdTest(){
        //given
        Long tourId = 1L;
        String testRegion = "제주";
        String period = "1일";
        String des  = "test";
        int price = 123123;

        //when
        Tour tour = tourService.findById(tourId);

        //then
        assertEquals(tourId,tour.getId());
        assertEquals(testRegion,tour.getRegion());
        assertEquals(period,tour.getPeriod());
        assertEquals(des,tour.getDescription());
        assertEquals(price,tour.getPrice());
    }
}
