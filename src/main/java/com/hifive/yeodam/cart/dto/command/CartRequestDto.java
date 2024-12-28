package com.hifive.yeodam.cart.dto.command;

import lombok.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartRequestDto {
    private Long itemId;
    private String itemName;
    private String description;
    private String period;
    private String region;
    private int price;
    private int count;
    private boolean reservation;
    private Long guideId;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    public CartRequestDto(Long itemId, String itemName, String description,
                          String period, String region, int price,
                          Long guideId, LocalDate startDate, LocalDate endDate) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.period = period;
        this.region = region;
        this.price = price;
        this.count = 1;
        this.reservation = true;
        this.guideId = guideId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // 일반 상품
    @Builder
    public CartRequestDto(Long itemId, String itemName, int price, int count) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.count = count;
        this.reservation = false;
    }
}
