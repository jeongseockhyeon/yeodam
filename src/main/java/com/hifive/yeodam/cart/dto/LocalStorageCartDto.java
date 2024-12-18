package com.hifive.yeodam.cart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocalStorageCartDto {
    private Long itemId;
    private int count;
    private boolean reservation;
}
