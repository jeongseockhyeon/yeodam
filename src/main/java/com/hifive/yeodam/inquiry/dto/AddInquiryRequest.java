package com.hifive.yeodam.inquiry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddInquiryRequest {
    @NotNull
    private Long itemId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Long parentId;
}
