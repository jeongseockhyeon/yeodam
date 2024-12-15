package com.hifive.yeodam.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemTourReqDto {

    private Long sellerId;

    private String tourName;

    private String tourDesc;

    private String tourPeriod;

    private String tourRegion;

    private int tourPrice;
}
