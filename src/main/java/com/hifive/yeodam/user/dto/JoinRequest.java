package com.hifive.yeodam.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JoinRequest {

    @NotBlank
    @Email
    @Size(max = 50, message = "50자 이하로 작성해 주세요")
    @Pattern(regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+(\\.[a-z]+)+$",
            message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank
    @Size(min = 8, max = 16, message = "8자 이상 16자 이하로 작성해 주세요")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,16}$"
            , message = "영대소문자, 특수문자, 숫자를 포함해주세요")
    private String password;

    @NotBlank
    @Size(max = 25, message = "25자 이하로 작성해 주세요")
    @Pattern(regexp = "^[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$", message = "한글만 입력해주세요")
    private String name;

    @NotBlank
    @Size(max = 25, message = "25자 이하로 작성해 주세요")
    private String nickname;

    @NotBlank
    @Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호를 입력해 주세요") // 01012345678
    private String phone;

    private LocalDate birthDate;

    @NotBlank
    private String gender;
}
