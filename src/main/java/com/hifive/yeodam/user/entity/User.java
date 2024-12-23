package com.hifive.yeodam.user.entity;

import com.hifive.yeodam.auth.entity.Auth;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Setter
    private String name;

    private LocalDate birthDate;

    @Setter
    private String nickname;

    private String gender;

    @Setter
    private String phone;

    @ManyToOne
    @JoinColumn(name = "auth_id")
    private Auth auth;
}
