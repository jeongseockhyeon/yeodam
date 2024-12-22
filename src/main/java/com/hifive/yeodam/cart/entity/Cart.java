package com.hifive.yeodam.cart.entity;


import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Builder
    public Cart(User user, Item item) {
        this.user = user;
        this.item = item;
        this.count = 1;
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
}
