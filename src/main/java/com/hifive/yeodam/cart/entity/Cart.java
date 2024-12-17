package com.hifive.yeodam.cart.entity;


import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
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

    private int count; //티켓 타입 수량

    public Cart(User user, Item item) {
        this.user = user;
        this.item = item;
        this.count = 1; //기본 수량
    }


    //상품의 현재 가격 실시간 반환
    public int getPrice(){
        return item.getPrice() * count;
    }

//
//    //티켓 타입 수량 변경
//    public void updateCount(int count) {
//        if (item.getItemType() == ItemType.RESERVATION) {
//            throw new IllegalStateException("예약 상품은 수량 변경이 불가능합니다.");
//        }
//        this.count = count;
//    }
//
//    //수량 변경 가능 여부 확인
//    public boolean isCountModifiable() {
//        return item.getItemType() != ItemType.RESERVATION;
//    }

}
