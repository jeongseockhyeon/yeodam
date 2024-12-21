package com.hifive.yeodam.cart.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartRequestDto {
    private Long itemId;
    private int count;

    @Builder
    private CartRequestDto(Long itemId, int count) {
        this.itemId = itemId;
        this.count = count;
    }
}
