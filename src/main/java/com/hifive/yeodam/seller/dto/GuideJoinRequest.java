package com.hifive.yeodam.seller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuideJoinRequest {
    @NotBlank
    @Size(max = 25)
    @Pattern(regexp = "^[a-zA-Z가-힣]+$", message = "이름은 영어 또는 한글로만 입력 가능합니다.")
    private String name;

    @Past
    private LocalDate birth;

    @NotBlank
    private String gender;

    @NotBlank
    @Pattern(regexp = "^010\\d{8}$", message = "전화번호는 010으로 시작하는 11자리 숫자만 가능합니다.")
    private String phone;

    @NotBlank
    private String bio;
}
