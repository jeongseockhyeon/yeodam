package com.hifive.yeodam.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class PaymentRequest {
    private String orderUid;
    private String itemName;
    private int price;

    @Builder
    public PaymentRequest(String orderUid, String itemName, int price) {
        this.orderUid = orderUid;
        this.itemName = itemName;
        this.price = price;
    }
}

