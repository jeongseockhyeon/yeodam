package com.hifive.yeodam.wish.entity;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wish_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Wish (User user, Item item) {
        this.user = user;
        this.item = item;
        this.createdAt = LocalDateTime.now();
    }

    public static Wish createWish(User user, Item item) {
        return new Wish(user, item);
    }
}