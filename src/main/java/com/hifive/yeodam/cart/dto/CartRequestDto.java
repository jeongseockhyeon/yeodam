package com.hifive.yeodam.cart.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartRequestDto {
    private Long itemId;
    private int count;
    private boolean reservation;

    @Builder
    public CartRequestDto(Long itemId, int count, boolean reservation) {
        this.itemId = itemId;
        this.count = reservation ? 1 : count;
        this.reservation = reservation;
    }
}
