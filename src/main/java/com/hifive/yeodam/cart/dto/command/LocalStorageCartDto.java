package com.hifive.yeodam.cart.dto.command;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalStorageCartDto {
    private Long itemId;
    private int count;
    private boolean reservation;
    private Long guideId;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    public LocalStorageCartDto(Long itemId, int count, boolean reservation,
                               Long guideId, LocalDate startDate, LocalDate endDate) {
        this.itemId = itemId;
        this.count = reservation ? 1 : count; //예약상품 1로 고정
        this.reservation = reservation;
        if (reservation) {
            this.guideId = guideId;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}
