package com.hifive.yeodam.tour.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class TourItemUpdateReqDto {

    private String tourName;

    private String tourDesc;

    private String tourPeriod;

    private String tourRegion;

    private String tourPrice;

    private String maximum;

    private String addCategoryIds;
    private String removeCategoryIds;

    private String addGuideIds;
    private String removeGuideIds;

    public TourItemUpdateReqDto(String tourName,
                                String tourDesc,
                                String tourPeriod,
                                String tourRegion,
                                String tourPrice,
                                String maximum,
                                String addCategoryIds,
                                String removeCategoryIds,
                                String addGuideIds,
                                String removeGuideIds) {
        this.tourName = tourName;
        this.tourDesc = tourDesc;
        this.tourPeriod = tourPeriod;
        this.tourRegion = tourRegion;
        this.tourPrice = tourPrice;
        this.maximum = maximum;
        this.addCategoryIds = addCategoryIds;
        this.removeCategoryIds = removeCategoryIds;
        this.addGuideIds = addGuideIds;
        this.removeGuideIds = removeGuideIds;
    }

}
