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

    private String name;

    private LocalDate birthDate;

    private String nickname;

    private String gender;

    @ManyToOne
    @JoinColumn(name = "auth_id")
    @Setter
    private Auth auth;
}
