package com.hifive.yeodam.itemTourTest;

import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.service.TourItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.hifive.yeodam.tour.entity.Tour;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemTourTest {

    @Autowired
    TourItemService tourItemService;

    @Test
    public void itemTourSaveTest(){

        //given
        Long sellerId = 1L;
        String tourName = "test";
        String tourDesc = "test";
        String tourPeriod = "1일";
        String tourRegion = "제주";
        int tourPrice = 100;

        TourItemReqDto tourItemReqDto = new TourItemReqDto();
        tourItemReqDto.setSellerId(sellerId);
        tourItemReqDto.setTourName(tourName);
        tourItemReqDto.setTourDesc(tourDesc);
        tourItemReqDto.setTourPeriod(tourPeriod);
        tourItemReqDto.setTourRegion(tourRegion);
        tourItemReqDto.setTourPrice(tourPrice);


        //when
        Tour tour = tourItemService.saveTourItem(tourItemReqDto);

        //then
        assertEquals(sellerId, tour.getSellerId());
        assertEquals(tourName,tour.getItemName());
        assertEquals(tourDesc,tour.getDescription());
        assertEquals(tourPeriod,tour.getPeriod());
        assertEquals(tourRegion,tour.getRegion());
        assertEquals(tourPrice,tour.getPrice());


    }

}
