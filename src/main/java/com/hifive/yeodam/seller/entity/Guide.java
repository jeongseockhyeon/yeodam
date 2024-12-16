package com.hifive.yeodam.seller.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Guide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guideId;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Seller seller;

    private String name;
    private String birth;
    private String gender;
    private String bio;
}
