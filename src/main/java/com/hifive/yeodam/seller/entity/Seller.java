package com.hifive.yeodam.seller.entity;

import com.hifive.yeodam.auth.entity.Auth;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long companyId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id")
    private Auth auth;

    private String companyName;
    private String owner;
    private String bio;
    private String phone;

    public void update(String companyName, String owner, String bio, String phone) {
        this.companyName = companyName;
        this.owner = owner;
        this.bio = bio;
        this.phone = phone;
    }
}
