package com.hifive.yeodam.cart.dto;

import com.hifive.yeodam.cart.entity.Cart;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class CartResponseDto {
    private Long cartId;
    private Long itemId;
    private String itemName;
    private int price;
    private int count;
    private boolean countModifiable; //수량 변경 가능 여부

    public CartResponseDto(Cart cart) {
        this.cartId = cart.getId();
        this.itemId = cart.getItem().getId();
        this.itemName = cart.getItem().getName();
        this.price = cart.getPrice();
        this.count = cart.getCount();
        this.countModifiable = cart.isCountModifiable();
    }

}
