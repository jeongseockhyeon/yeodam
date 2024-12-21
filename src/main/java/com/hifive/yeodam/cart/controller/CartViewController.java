package com.hifive.yeodam.cart.controller;

import com.hifive.yeodam.cart.dto.CartTotalPriceDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartViewController {
    private final CartService cartService;

    private boolean isAnonymous() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth == null || auth instanceof AnonymousAuthenticationToken);
    }

    @GetMapping
    public String cartList(Model model){
        boolean anonymous = isAnonymous();

        if (!anonymous){
            //로그인 상태 - 서버 장바구니 데이터
            List<Cart> cartList = cartService.getCartList();
            CartTotalPriceDto totalPrice = cartService.getTotalPrice();
            model.addAttribute("carts", cartList);
            model.addAttribute("totalPrice", totalPrice);
        }
        model.addAttribute("anonymous", anonymous);
        return "cart/cartList";
    }

    @GetMapping("/selected-price")
    public String selectedPrice(@RequestParam List<Long> cartIds, Model model) {
        boolean anonymous = isAnonymous();

        if (!anonymous){
            CartTotalPriceDto selectedPrice = cartService.getSelectedPrice(cartIds);
            model.addAttribute("selectedPrice", selectedPrice);
        }
        model.addAttribute("anonymous", anonymous);
        return "cart/selectedPrice";
    }
}
