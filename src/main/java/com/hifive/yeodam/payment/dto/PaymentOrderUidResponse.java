package com.hifive.yeodam.payment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentOrderUidResponse {

    private String orderUid;

    @Builder
    public PaymentOrderUidResponse(String orderUid) {
        this.orderUid = orderUid;
    }
}
