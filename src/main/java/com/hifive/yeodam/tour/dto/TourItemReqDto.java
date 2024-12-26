package com.hifive.yeodam.tour.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class TourItemReqDto {


    @NotBlank(message = "여행 상품명은 입력해주세요")
    @Size(max = 25)
    private final String tourName;


    @NotBlank(message = "여행 상품의 설명을 입력해주세요")
    private final String tourDesc;

    @NotBlank(message = "기간을 입력해주세요")
    @Size(max = 25)
    private final String tourPeriod;

    @NotBlank(message = "지역을 입력해주세요")
    @Size(max = 25)
    private final String tourRegion;

    @NotNull
    private final String tourPrice;

    @NotNull
    private final String maximum;

    @NotNull
    private final String categoryIdList;

    private final List<Long> guideIdList;

    private final List<MultipartFile> tourImages;

    public TourItemReqDto(String tourName,
                          String tourDesc,
                          String tourPeriod,
                          String tourRegion,
                          String tourPrice,
                          String maximum,
                          String categoryIdList,
                          List<Long> guideIdList,
                          List<MultipartFile> tourImages) {
        this.tourName = tourName;
        this.tourDesc = tourDesc;
        this.tourPeriod = tourPeriod;
        this.tourRegion = tourRegion;
        this.tourPrice = tourPrice;
        this.maximum = maximum;
        this.categoryIdList = categoryIdList;
        this.guideIdList = guideIdList;
        this.tourImages = tourImages;
    }
}
