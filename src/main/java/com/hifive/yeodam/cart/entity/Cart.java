package com.hifive.yeodam.cart.entity;


import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;

    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    public Cart(User user, Item item, Guide guide, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.item = item;
        this.count = 1;
        //Tour 상품만 예약 정보 설정
        if (item instanceof Tour) {
            this.guide = guide;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    //동일 상품 존재 여부 확인
    public boolean isSameItem(Item item) {
        return this.item.getId().equals(item.getId());
    }

    //수량 변경 가능 여부 확인
    public boolean isCountModifiable() {
        return !item.isReservation();
    }

    //상품의 현재 가격 실시간 반환
    public int getPrice(){
        return item.getPrice() * count;
    }

    //장바구니에서 수량 변경
    public void updateCount(int count) {
        if (item.isReservation()) {
            throw new CustomException(CustomErrorCode.CART_ITEM_COUNT_NOT_MODIFIABLE);
        }
        if (count <= 1) {
            throw new CustomException(CustomErrorCode.INVALID_ITEM_COUNT);
        }

        this.count = count;
    }

    //로컬 연동시 수량 추가
    public void addCount(int count) {
        if (item.isReservation()) {
            throw new CustomException(CustomErrorCode.CART_ITEM_COUNT_NOT_MODIFIABLE);
        }
        if (count <= 1) {
            throw new CustomException(CustomErrorCode.INVALID_ITEM_COUNT);
        }
        this.count += count;
    }

    //Tour 예약 정보 업데이트
    public void updateTourReservation(Guide guide, LocalDate startDate, LocalDate endDate) {
        if (!(item instanceof Tour)) {
            throw new CustomException(CustomErrorCode.CART_ITEM_TYPE_MISMATCH);
        }
        this.guide = guide;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
