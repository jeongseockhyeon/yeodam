package com.hifive.yeodam.tour.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TourItemUpdateReqDto {

    private String tourName;

    private String region;

    private String period;

    private String description;

    private int price;

    private int maximum;

    private List<Long> addCategoryIds;
    private List<Long> removeCategoryIds;

    private List<Long> addGuideIds;
    private List<Long> removeGuideIds;

}
