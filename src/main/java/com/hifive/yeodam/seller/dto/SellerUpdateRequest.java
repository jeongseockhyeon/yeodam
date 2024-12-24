package com.hifive.yeodam.seller.dto;

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
public class SellerUpdateRequest {
    @NotBlank
    @Size(min = 8, max = 16)
    private String password;

    @NotBlank
    @Size(max = 25)
    private String companyName;

    @NotBlank
    @Size(max = 25)
    @Pattern(regexp = "^[a-zA-Z가-힣]+$", message = "이름은 영어 또는 한글로만 입력 가능합니다.")
    private String owner;

    @NotBlank
    private String bio;

    @NotBlank
    @Pattern(regexp = "^\\d{9,11}$")
    private String phone;
}