package com.hifive.yeodam.itemTourTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.service.TourItemService;
import com.hifive.yeodam.tour.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import com.hifive.yeodam.tour.entity.Tour;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TourItemTest {

    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    protected ObjectMapper objectMapper;


    @Autowired
    TourItemService tourItemService;

    @Autowired
    TourService tourService;

    @BeforeEach
    public void setMockMvc(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("itemTourSaveTest 테스트")
    public void itemTourSaveTest() throws Exception {

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

        String url = "/tour";
        String json = objectMapper.writeValueAsString(tourItemReqDto);


        //when
        ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));

        List<Tour> tours = tourService.findAll();
        Tour tour = tours.getLast();

        //then
        result.andExpect(status().isCreated());
        assertEquals(sellerId, tour.getSellerId());
        assertEquals(tourName,tour.getItemName());
        assertEquals(tourDesc,tour.getDescription());
        assertEquals(tourPeriod,tour.getPeriod());
        assertEquals(tourRegion,tour.getRegion());
        assertEquals(tourPrice,tour.getPrice());
    }

}
