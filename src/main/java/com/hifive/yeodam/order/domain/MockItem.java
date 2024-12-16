package com.hifive.yeodam.order.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Getter
public class MockItem {

    @Id
    private Long itemId;
    private int price;

    private String name;
}
