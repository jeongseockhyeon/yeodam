package com.hifive.yeodam.seller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
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
    private String name;

    @Past
    private LocalDate birth;

    @NotBlank
    private String gender;

    @NotBlank
    @Pattern(regexp = "^010\\d{8}$")
    private String phone;

    @NotBlank
    private String bio;
}
