package com.hifive.yeodam.seller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerUpdateRequest {
    private String companyName;
    private String owner;
    private String bio;
}