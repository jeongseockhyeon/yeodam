package com.hifive.yeodam.cart.dto.command;

import lombok.*;

import java.time.LocalDate;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartRequestDto {
    private Long itemId;
    private String tourName;
    private String tourRegion;
    private String tourPeriod;
    private int tourPrice;
    private int maximum;
    private Long guideId;
    private String guideName;
    private String imgUrl;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    public CartRequestDto(Long itemId, String tourName, String tourRegion,
                          String tourPeriod, int tourPrice, int maximum,
                          Long guideId, String guideName, String imgUrl,
                          LocalDate startDate, LocalDate endDate) {
        this.itemId = itemId;
        this.tourName = tourName;
        this.tourRegion = tourRegion;
        this.tourPeriod = tourPeriod;
        this.tourPrice = tourPrice;
        this.maximum = maximum;
        this.guideId = guideId;
        this.guideName = guideName;
        this.imgUrl = imgUrl;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
