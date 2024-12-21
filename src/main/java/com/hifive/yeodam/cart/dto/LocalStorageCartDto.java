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

    @Builder
    public LocalStorageCartDto(Long itemId, int count) {
        this.itemId = itemId;
        this.count = count;
    }
}
