package com.hifive.yeodam.tour.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TourItemReqDto {

    private Long sellerId;

    private String tourName;

    private String tourDesc;

    private String tourPeriod;

    private String tourRegion;

    private int tourPrice;

    private int maximum;

    private List<Long> categoryIdList;

    private List<Long> guideIdList;
}
