package com.hifive.yeodam.cart.entity;


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
    private UserCart userCart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int count; //티켓과 같은 수량 상품의 경우 수량 저장

    public Cart(Item item) {
        this.item = item;
        this.count = 1; //기본 수량 1로 설정
    }


    // UserCart 설정 메서드
    protected void setUserCart(UserCart userCart) {
        this.userCart = userCart;
    }

    //상품의 현재 가격 실시간 반환
    public int getPrice(){
        return item.getPrice() * count;
    }


    //티켓 예시 수량 변경 메서드
    public void updateCount(int count) {
        if (item.getItemType() != ItemType.TICKET) {
            throw new IllegalStateException("예약 상품은 수량 변경이 불가능합니다.");
        }
        this.count = count;
    }

    //수량 변경 가능 여부 확인
    public boolean isCountModifiable() {
        return item.getItemType() == ItemType.TICKET;
    }

}
