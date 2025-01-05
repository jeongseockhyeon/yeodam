package com.hifive.yeodam.user.dto;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class UserResponse {

    private Long id;

    private String name;

    private LocalDate birthDate;

    private String nickname;

    private String gender;

    private String phone;

    private Auth auth;

    public UserResponse(User entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.birthDate = entity.getBirthDate();
        this.nickname = entity.getNickname();
        this.gender = entity.getGender();
        this.phone = entity.getPhone();
        this.auth = entity.getAuth();
    }
}
