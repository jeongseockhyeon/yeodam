package com.hifive.yeodam.tour.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class TourItemUpdateReqDto {

    private final String tourName;

    private final String tourDesc;

    private final String tourPeriod;

    private final String tourRegion;

    private final String tourPrice;

    private final String maximum;


    /*카테고리 추가 삭제*/
    private final String addCategoryIds;
    private final String removeCategoryIds;

    /*가이드 추가 삭제*/
    private final String addGuideIds;
    private final String removeGuideIds;

    /*이미지 추가 삭제*/
    private final List<MultipartFile> addTourImages;
    private final String removeImageIds;

    public TourItemUpdateReqDto(String tourName,
                                String tourDesc,
                                String tourPeriod,
                                String tourRegion,
                                String tourPrice,
                                String maximum,
                                String addCategoryIds,
                                String removeCategoryIds,
                                String addGuideIds,
                                String removeGuideIds,
                                List<MultipartFile> addTourImages,
                                String removeImageIds) {
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
        this.addTourImages = addTourImages;
        this.removeImageIds = removeImageIds;
    }

}
