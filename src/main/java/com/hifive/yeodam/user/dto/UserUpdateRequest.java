package com.hifive.yeodam.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {

    private String name;

    private String nickname;

    private String password;

    private String phone;
}
