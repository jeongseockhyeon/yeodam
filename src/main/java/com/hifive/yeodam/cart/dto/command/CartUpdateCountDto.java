package com.hifive.yeodam.cart.dto.command;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartUpdateCountDto {
    private int count;
    private boolean reservation;

    @Builder
    public CartUpdateCountDto(int count, boolean reservation) {
        if (reservation) {
            throw new IllegalArgumentException("예약 상품은 수량 변경이 불가능 합니다.");
        }
        if (count < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        this.count = count;
        this.reservation = reservation;
    }
}
