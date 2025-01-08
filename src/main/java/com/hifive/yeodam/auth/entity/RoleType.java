package com.hifive.yeodam.auth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {

    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    SELLER("ROLE_SELLER"),
    NONE("ROLE_NONE");

    private final String value;

}
