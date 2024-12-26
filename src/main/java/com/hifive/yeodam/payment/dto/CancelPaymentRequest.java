package com.hifive.yeodam.payment.dto;

import lombok.Data;

@Data
public class CancelPaymentRequest {
    private String orderUid;
    private int totalPrice;
}
