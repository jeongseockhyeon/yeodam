package com.hifive.yeodam.tour.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourReqDto {
    private String region;

    private String period;

    private String description;

    private int price;

}
