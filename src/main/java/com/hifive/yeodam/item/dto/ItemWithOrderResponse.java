package com.hifive.yeodam.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemWithOrderResponse {
    private Long itemId;
    private String itemName;
    private int reservationCnt;
    private int cancelCnt;
}
