package com.hifive.yeodam.order.service.code;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MockItem {

    @Id
    private Long itemId;
    private int price;

    private String name;
}
