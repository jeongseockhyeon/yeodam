package com.hifive.yeodam.cart.entity;


import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.seller.entity.Guide;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id")
    private Auth auth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;

    @Column(nullable = false)
    private int count = 1;

    @Getter @Setter
    @Column
    private LocalDate startDate;

    @Getter @Setter
    @Column
    private LocalDate endDate;

    @Builder
    public Cart(Auth auth, Item item, Guide guide, LocalDate startDate, LocalDate endDate) {
        this.auth = auth;
        this.item = item;
        this.guide = guide;
        this.count = 1;
        this.startDate = startDate;
        this.endDate = endDate;

        // Tour 상품만 가이드 정보 설정
//        if (item instanceof Tour) {
//            this.guide = guide;
//        }
    }

    //동일 상품 존재 여부 확인
    public boolean isSameItem(Item item) {
        return this.item.getId().equals(item.getId());
    }

    //수량 변경 가능 여부 확인
    public boolean isCountModifiable() {
        return !item.isReservation();
    }

    //상품의 현재 가격 반환
    public int getTourPrice(){
        return item.getPrice();
    }
}
