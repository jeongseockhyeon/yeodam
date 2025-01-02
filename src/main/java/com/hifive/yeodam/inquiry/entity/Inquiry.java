package com.hifive.yeodam.inquiry.entity;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.item.entity.Item;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id")
    private Auth auth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String title;
    private String content;
    private String isAnswered;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_inquiry_id")
    private Inquiry parentInquiry;

    public void update() {
        this.isAnswered = "Y";
    }

    public void changeAuth(Auth auth) {
        this.auth = auth;
    }
}
