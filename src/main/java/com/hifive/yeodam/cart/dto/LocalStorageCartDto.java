package com.hifive.yeodam.cart.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalStorageCartDto {
    private Long itemId;
    private int count;
    private boolean reservation;

    @Builder
    public LocalStorageCartDto(Long itemId, int count, boolean reservation) {
        this.itemId = itemId;
        this.count = reservation ? 1 : count; //예약상품 1로 고정
        this.reservation = reservation;
    }
}
