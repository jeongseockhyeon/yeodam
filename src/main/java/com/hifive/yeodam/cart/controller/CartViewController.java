package com.hifive.yeodam.cart.controller;

import com.hifive.yeodam.cart.dto.query.CartTotalPriceDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.service.CartCommandService;
import com.hifive.yeodam.cart.service.CartQueryService;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import com.hifive.yeodam.seller.entity.Guide;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartViewController {
    private final CartCommandService cartCommandService;
    private final CartQueryService cartQueryService;

    private boolean isAnonymous() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth == null || auth instanceof AnonymousAuthenticationToken);
    }

    @GetMapping
    public String cartList(Model model){
        boolean anonymous = isAnonymous();

        if (!anonymous){
            //로그인 상태 - 서버 장바구니 데이터
            List<Cart> cartList = cartQueryService.getCartList();
            CartTotalPriceDto totalPrice = cartQueryService.getTotalPrice();
            model.addAttribute("carts", cartList);
            model.addAttribute("totalPrice", totalPrice);
        }
        model.addAttribute("anonymous", anonymous);
        return "cart/cart-list";
    }

    @GetMapping("/selected-price")
    public String selectedPrice(@RequestParam List<Long> cartIds, Model model) {
        boolean anonymous = isAnonymous();

        if (!anonymous){
            CartTotalPriceDto selectedPrice = cartQueryService.getSelectedPrice(cartIds);
            model.addAttribute("selectedPrice", selectedPrice);
        }
        model.addAttribute("anonymous", anonymous);
        return "cart/selectedPrice";
    }

    //장바구니 - 주문 페이지 연결
    @PostMapping("/order")
    public String orderForm(@RequestParam List<Long> cartIds, Model model) {
        if (isAnonymous()) {
            throw new CustomException(CustomErrorCode.LOGIN_REQUIRED);
        }

        // 선택 상품 주문 dto 변환
        List<Cart> selectedCarts = cartQueryService.findByCartIds(cartIds);
        List<AddOrderRequest.orderRequest> orderRequests = selectedCarts.stream()
                .map(cart -> {
                    Item item = cart.getItem();
                    return AddOrderRequest.orderRequest.builder()
                            .itemId(item.getId())
                            .name(item.getItemName())
                            .count(cart.getCount())
                            .price(item.getPrice())
                            .bookerName("")
                            .phoneNumber("")
                            .orderMessage("")
                            .guideId(Optional.ofNullable(cart.getGuide()).map(Guide::getGuideId).orElse(null))
                            .startDate(cart.getStartDate())
                            .endDate(cart.getEndDate())
                            .build();
                })
                .toList();

        AddOrderRequest addOrderRequest = new AddOrderRequest(orderRequests);
        model.addAttribute("addOrderRequest", addOrderRequest);
        return "order/order-form";
    }
}
