package com.hifive.yeodam.order.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Entity
public class MockUser {

    @Id
    private Long userId;
    private String name;
    private String phone;
}
