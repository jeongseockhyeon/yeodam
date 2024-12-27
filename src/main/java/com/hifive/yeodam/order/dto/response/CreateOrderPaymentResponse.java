package com.hifive.yeodam.order.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class CreateOrderPaymentResponse {
    private String orderUid;
    private String username;
    private String phone;
    private String email;
    private String itemName;
    private int price;

    @Builder
    public CreateOrderPaymentResponse(String orderUid, String username, String phone, String email, String itemName, int price) {
        this.orderUid = orderUid;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.itemName = itemName;
        this.price = price;
    }
}