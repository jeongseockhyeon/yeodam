package com.hifive.yeodam.seller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerLoginRequest {

    @NotEmpty(message = "이메일을 입력하세요.")
    @Email(message = "유효한 이메일을 입력하세요.")
    private String email;

    @NotEmpty(message = "비밀번호를 입력하세요.")
    private String password;
}