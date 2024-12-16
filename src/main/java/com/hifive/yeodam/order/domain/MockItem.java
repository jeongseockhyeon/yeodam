package com.hifive.yeodam.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MockItem {
    private Long itemId;
    private int price;

    private String name;
}
