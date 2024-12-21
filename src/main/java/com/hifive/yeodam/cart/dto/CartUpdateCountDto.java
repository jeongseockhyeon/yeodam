package com.hifive.yeodam.cart.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartUpdateCountDto {
    private int count;

    @Builder
    private CartUpdateCountDto(int count) {
        this.count = count;
    }
}
