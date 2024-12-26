package com.hifive.yeodam.order.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CancelOrderRequest {
    private String orderUid;
//    private List<Long> itemIds;
}
