package com.hifive.yeodam.cart.dto;

import lombok.Getter;

@Getter
public class CartTotalPriceDto {
    private int totalPrice;

    public CartTotalPriceDto(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
