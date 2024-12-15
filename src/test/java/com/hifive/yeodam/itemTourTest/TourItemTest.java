package com.hifive.yeodam.itemTourTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifive.yeodam.tour.dto.TourItemReqDto;
import com.hifive.yeodam.tour.dto.TourItemUpdateReqDto;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    @DisplayName("상품_여행 등록 테스트")
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

        List<Tour> tours = tourItemService.findAll();
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

    @Test
    @DisplayName("상품_여행 목록 조회 테스트")
    public void itemFindAllTest() throws Exception {
        //given
        String url ="/tour";
        int testCount = 4;

        //when
        ResultActions resultActions = mockMvc.perform(get(url));

        MvcResult mvcResult = resultActions.andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<?> responseList = objectMapper.readValue(jsonResponse, List.class);

        //then
        assertEquals(testCount, responseList.size());
        resultActions.andExpect(status().isOk());
    }
    @Test
    @DisplayName("상품_여행 단일 조회 테스트")
    public void itemFindByIdTest() throws Exception {
        //given
        Long sellerId = 1L;
        String tourName = "test";
        String tourDesc = "test";
        String tourPeriod = "1일";
        String tourRegion = "제주";
        int tourPrice = 100;
        String url = "/tour/{id}";
        Long tourItemId = 1L;

        //when
        ResultActions resultActions = mockMvc.perform(get(url,tourItemId));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.sellerId").value(sellerId))
                .andExpect(jsonPath("$.itemName").value(tourName))
                .andExpect(jsonPath("$.description").value(tourDesc))
                .andExpect(jsonPath("$.period").value(tourPeriod))
                .andExpect(jsonPath("$.region").value(tourRegion))
                .andExpect(jsonPath("$.price").value(tourPrice));
    }

    @Test
    @DisplayName("상품_여행 수정 테스트")
    public void itemTourUpdateTest() throws Exception {
        //given
        String url = "/tour/{id}";
        Long tourItemId = 1L;

        String tourName = "update name";
        String tourDesc = "update desc";
        String tourPeriod = "반나절";
        String tourRegion = "강원도";
        int tourPrice = 100;

        TourItemUpdateReqDto tourItemUpdateReqDto = new TourItemUpdateReqDto();

        tourItemUpdateReqDto.setTourName(tourName);
        tourItemUpdateReqDto.setDescription(tourDesc);
        tourItemUpdateReqDto.setPeriod(tourPeriod);
        tourItemUpdateReqDto.setRegion(tourRegion);
        tourItemUpdateReqDto.setPrice(tourPrice);

        String json = objectMapper.writeValueAsString(tourItemUpdateReqDto);


        //when
        ResultActions resultActions = mockMvc.perform(patch(url,tourItemId).contentType(MediaType.APPLICATION_JSON).content(json));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value(tourName))
                .andExpect(jsonPath("$.description").value(tourDesc))
                .andExpect(jsonPath("$.period").value(tourPeriod))
                .andExpect(jsonPath("$.region").value(tourRegion))
                .andExpect(jsonPath("$.price").value(tourPrice));

    }

    @Test
    @DisplayName("상품_여행 삭제 테스트")
    public void itemTourDeleteTest() throws Exception {
        //given
        String url = "/tour/{id}";
        Long tourItemId = 1L;

        //when
        ResultActions resultActions = mockMvc.perform(delete(url,tourItemId));

        //then
        resultActions.andExpect(status().isNoContent());
    }
}
