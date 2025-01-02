package com.hifive.yeodam.payment.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class PaymentTest {

    @Test
    void test() {
        LocalDateTime localDateTime = LocalDateTime.of(2000, 1, 1, 00, 00);
        System.out.println(localDateTime);
    }
}