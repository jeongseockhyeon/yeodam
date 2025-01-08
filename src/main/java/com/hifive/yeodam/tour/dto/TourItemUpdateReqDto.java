package com.hifive.yeodam.tour.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class TourItemUpdateReqDto {

    @NotBlank(message = "여행 상품명은 입력해주세요")
    @Size(max = 25)
    private final String tourName;

    @NotBlank(message = "여행 상품의 설명을 입력해주세요.")
    private final String tourDesc;

    @NotBlank(message = "기간을 입력해주세요.")
    @Size(max = 25)
    private final String tourPeriod;

    @NotBlank(message = "지역을 입력해주세요.")
    @Size(max = 25)
    private final String tourRegion;

    @NotNull(message = "가격을 입력해주세요.")
    private final String tourPrice;

    @NotNull(message = "최대 인원을 입력해주세요.")
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
