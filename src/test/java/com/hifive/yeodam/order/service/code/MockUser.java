package com.hifive.yeodam.order.service.code;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MockUser {

    @Id
    private Long userId;
    private String name;
    private String phone;
}
