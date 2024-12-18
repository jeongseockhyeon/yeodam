package com.hifive.yeodam.tour.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TourItemReqDto {

    @NotNull
    private Long sellerId;

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
    private int tourPrice;

    @NotNull
    private int maximum;

    @NotNull
    private List<Long> categoryIdList;

    @NotNull
    private List<Long> guideIdList;
}
