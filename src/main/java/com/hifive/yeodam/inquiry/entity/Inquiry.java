package com.hifive.yeodam.inquiry.entity;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.user.entity.User;
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
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String title;
    private String content;
    private String isAnswered;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_inquiry_id")
    private Inquiry parentInquiry;
}
