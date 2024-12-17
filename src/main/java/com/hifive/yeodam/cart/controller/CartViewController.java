package com.hifive.yeodam.cart.controller;

import com.hifive.yeodam.cart.dto.CartTotalPriceDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartViewController {
    private final CartService cartService;

//    @GetMapping
//    public String cartList(Model model){
//        List<Cart> cartList = cartService.getCartList();
//        CartTotalPriceDto totalPrice = cartService.getTotalPrice();
//        model.addAttribute("carts", cartList);
//        model.addAttribute("totalPrice", totalPrice);
//        return "cartList";
//    }
}
