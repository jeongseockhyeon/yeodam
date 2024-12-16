package com.hifive.yeodam.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddOrderRequest {
    private Long userId;
    private List<ItemRequest> items;

    @Data
    static class ItemRequest {
        private Long itemId;
        private int count;
    }
}
