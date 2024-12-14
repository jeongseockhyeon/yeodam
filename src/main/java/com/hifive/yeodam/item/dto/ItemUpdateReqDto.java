package com.hifive.yeodam.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemUpdateReqDto {

    private Long itemId;
    private String updateItemName;
}
