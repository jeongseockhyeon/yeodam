package com.hifive.yeodam.order.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CancelOrderResponse {
    private String orderUid;
    private int totalPrice;

    @Builder
    public CancelOrderResponse(String orderUid, int totalPrice) {
        this.orderUid = orderUid;
        this.totalPrice = totalPrice;
    }
}
