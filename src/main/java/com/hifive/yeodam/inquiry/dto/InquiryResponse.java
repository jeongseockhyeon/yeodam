package com.hifive.yeodam.inquiry.dto;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.inquiry.entity.Inquiry;
import com.hifive.yeodam.item.entity.Item;
import lombok.Getter;

@Getter
public class InquiryResponse {
    private final Long id;
    private final Long itemId;
    private final String itemName;
    private final String title;
    private final String content;
    private final String isAnswered;

    public InquiryResponse(Inquiry inquiry) {
        this.id = inquiry.getId();
        this.itemId = inquiry.getItem().getId();
        this.itemName = inquiry.getItem().getItemName();
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
        this.isAnswered = inquiry.getIsAnswered();
    }
}
