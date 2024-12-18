package com.hifive.yeodam.seller.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerJoinRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    @Size(min = 9, max = 11)
    @Pattern(regexp = "\\d+")
    private String phone;

    @NotBlank
    @Size(max = 25)
    private String companyName;

    @NotBlank
    @Size(max = 25)
    private String owner;

    @NotBlank
    private String bio;
}