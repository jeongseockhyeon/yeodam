package com.hifive.yeodam.auth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {

    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    SELLER("ROLE_SELLER"),
    ;

    private final String value;

    public static RoleType fromValue(String v) {
        for (RoleType roleType : RoleType.values()) {
            if (roleType.getValue().equals(v)) {
                return roleType;
            }
        }

        throw new IllegalArgumentException("Invalid role value: " + v);
    }
}
