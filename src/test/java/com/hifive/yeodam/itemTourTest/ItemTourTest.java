package com.hifive.yeodam.itemTourTest;

import com.hifive.yeodam.item.dto.ItemTourReqDto;
import com.hifive.yeodam.item.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.hifive.yeodam.tour.entity.Tour;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemTourTest {

    @Autowired
    ItemService itemService;

    @Test
    public void itemTourSaveTest(){

        //given
        Long sellerId = 1L;
        String tourName = "test";
        String tourDesc = "test";
        String tourPeriod = "1일";
        String tourRegion = "제주";
        int tourPrice = 100;

        ItemTourReqDto itemTourReqDto = new ItemTourReqDto();
        itemTourReqDto.setSellerId(sellerId);
        itemTourReqDto.setTourName(tourName);
        itemTourReqDto.setTourDesc(tourDesc);
        itemTourReqDto.setTourPeriod(tourPeriod);
        itemTourReqDto.setTourRegion(tourRegion);
        itemTourReqDto.setTourPrice(tourPrice);


        //when
        Tour tour = itemService.saveItemTour(itemTourReqDto);

        //then
        assertEquals(sellerId, tour.getSellerId());
        assertEquals(tourName,tour.getItemName());
        assertEquals(tourDesc,tour.getDescription());
        assertEquals(tourPeriod,tour.getPeriod());
        assertEquals(tourRegion,tour.getRegion());
        assertEquals(tourPrice,tour.getPrice());


    }

}
