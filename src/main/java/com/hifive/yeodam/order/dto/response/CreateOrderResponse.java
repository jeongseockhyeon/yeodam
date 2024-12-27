package com.hifive.yeodam.order.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateOrderResponse {

    private String orderUid;

    @Builder
    public CreateOrderResponse(String orderUid) {
        this.orderUid = orderUid;
    }
}
