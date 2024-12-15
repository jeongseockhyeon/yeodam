package com.hifive.yeodam.tour.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourItemReqDto {

    private Long sellerId;

    private String tourName;

    private String tourDesc;

    private String tourPeriod;

    private String tourRegion;

    private int tourPrice;
}
