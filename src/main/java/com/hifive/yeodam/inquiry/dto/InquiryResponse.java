package com.hifive.yeodam.inquiry.dto;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.inquiry.entity.Inquiry;
import com.hifive.yeodam.item.entity.Item;
import lombok.Getter;

@Getter
public class InquiryResponse {
    private final Long id;
    private final Auth auth;
    private final Item item;
    private final String title;
    private final String content;
    private final String isAnswered;
    private final Inquiry parentInquiry;

    public InquiryResponse(Inquiry inquiry) {
        this.id = inquiry.getId();
        this.auth = inquiry.getAuth();
        this.item = inquiry.getItem();
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
        this.isAnswered = inquiry.getIsAnswered();
        this.parentInquiry = inquiry.getParentInquiry();
    }
}
