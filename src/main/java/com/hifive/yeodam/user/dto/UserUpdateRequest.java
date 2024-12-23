package com.hifive.yeodam.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {

    private String email;

    @NotBlank
    @Size(max = 25, message = "25자 이하로 작성해 주세요")
    @Pattern(regexp = "^[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$", message = "한글만 입력해주세요")
    private String name;

    @NotBlank
    @Size(max = 25, message = "25자 이하로 작성해 주세요")
    private String nickname;

    @NotBlank
    @Size(min = 8, max = 16, message = "8자 이상 16자 이하로 작성해 주세요")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,15}$"
            , message = "영대소문자, 특수문자, 숫자를 포함해주세요")
    private String password;

    @NotBlank
    @Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호를 입력해 주세요")
    private String phone;

    private LocalDate birthDate;

    private String gender;
}
