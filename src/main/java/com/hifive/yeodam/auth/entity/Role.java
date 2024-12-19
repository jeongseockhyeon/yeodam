package com.hifive.yeodam.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id")
    private Auth auth;

    @Enumerated(value = EnumType.STRING)
    private RoleType roleType;

    public Role(Auth auth, RoleType roleType) {
        this.auth = auth;
        this.roleType = roleType;
    }
}
