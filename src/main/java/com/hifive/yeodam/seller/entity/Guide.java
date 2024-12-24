package com.hifive.yeodam.seller.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Guide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guideId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Seller seller;

    private String name;
    private LocalDate birth;
    private String gender;
    private String phone;
    private String bio;

    public void update(String name, String phone, String bio) {
        this.name = name;
        this.phone = phone;
        this.bio = bio;
    }
}
