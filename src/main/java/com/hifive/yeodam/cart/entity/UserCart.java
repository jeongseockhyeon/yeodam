package com.hifive.yeodam.cart.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class UserCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "userCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> carts = new ArrayList<>();

    //장바구니 생성
    public static UserCart createCart(User user) {
        UserCart userCart = new UserCart();
        userCart.user = user;
        return userCart;
    }

    //장바구니 상품 추가
    public void addCart(Cart cart) {
        this.carts.add(cart);
        cart.setUserCart(this);
    }

    //장바구니 상품 제거
    public void removeCart(Cart cart) {
        this.carts.remove(cart);
        cart.setUserCart(null);
    }


}
