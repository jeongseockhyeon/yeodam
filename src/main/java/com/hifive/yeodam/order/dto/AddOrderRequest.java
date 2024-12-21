package com.hifive.yeodam.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrderRequest {

    private String bookerName;
    private String phoneNumber;
    private String orderMessage;
    private List<ItemRequest> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemRequest {
        private Long id;
        private String name;
        private int count;
        private String period;
        private int price;
    }
}
