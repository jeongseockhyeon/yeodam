package com.hifive.yeodam.order.dto.request;

import lombok.Data;

@Data
public class CancelOrderRequest {
    private String orderUid;
//    private List<Long> itemIds;
}
