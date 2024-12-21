package com.hifive.yeodam.tour.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
@Setter
public class TourItemReqDto {


    @NotBlank
    @Size(max = 25)
    private String tourName;

    @NotBlank
    private String tourDesc;

    @NotBlank
    @Size(max = 25)
    private String tourPeriod;

    @NotBlank
    @Size(max = 25)
    private String tourRegion;

    @NotNull
    private String tourPrice;

    @NotNull
    private String maximum;

    @NotNull
    private String categoryIdList;

    private List<Long> guideIdList;

    private List<MultipartFile> tourImages;

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
