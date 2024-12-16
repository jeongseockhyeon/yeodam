package com.hifive.yeodam.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerUpdateRequest {
    private String companyName;
    private String owner;
    private String bio;
}