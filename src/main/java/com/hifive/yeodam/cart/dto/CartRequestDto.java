package com.hifive.yeodam.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequestDto {
    private Long itemId;
    private int count; //추가할 상품 수량
    private boolean reservation;

    public CartRequestDto(Long itemId, int count, boolean reservation) {
        this.itemId = itemId;
        this.count = count;
        this.reservation = reservation;
    }
}
