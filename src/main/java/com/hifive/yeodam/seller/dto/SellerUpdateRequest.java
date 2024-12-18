package com.hifive.yeodam.seller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerUpdateRequest {
    @NotBlank
    private String companyName;

    @NotBlank
    private String owner;

    @NotBlank
    private String bio;
}