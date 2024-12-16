package com.hifive.yeodam.payment.dto;

import lombok.Data;

@Data
public class PaymentRequestCallBack {
    private String paymentUid;
    private String orderUid;
}
