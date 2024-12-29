package com.hifive.yeodam.cart.dto.query;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartTotalPriceDto {
    private int totalPrice;

    @Builder
    private CartTotalPriceDto(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
