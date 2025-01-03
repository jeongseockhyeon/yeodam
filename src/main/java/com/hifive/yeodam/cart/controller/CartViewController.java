package com.hifive.yeodam.cart.controller;

import com.hifive.yeodam.cart.dto.query.CartResponseDto;
import com.hifive.yeodam.cart.dto.query.CartTotalPriceDto;
import com.hifive.yeodam.cart.service.CartCommandService;
import com.hifive.yeodam.cart.service.CartQueryService;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
        try {
            log.info("장바구니 페이지 접속");
            boolean anonymous = isAnonymous();
            log.info("로그인 상태: {}", !anonymous);

            if (!anonymous){
                List<CartResponseDto> cartList = cartQueryService.getCarts();
                log.info("장바구니 목록 조회 결과: {}", cartList != null ? cartList.size() : "null");

                int totalPrice = cartList.stream()
                        .mapToInt(CartResponseDto::getTourPrice)
                        .sum();

                model.addAttribute("carts", cartList);
                model.addAttribute("totalPrice", totalPrice);
            }
            model.addAttribute("anonymous", anonymous);
            return "cart/cart-list";
        } catch (Exception e) {
            log.error("장바구니 조회 중 에러 발생: ", e);
            throw e;
        }
    }

    @GetMapping("/selected-price")
    @ResponseBody
    public CartTotalPriceDto selectedPrice(@RequestParam List<Long> cartIds) {
        if (isAnonymous()) {
            return CartTotalPriceDto.builder()
                    .tourPrice(0)
                    .build();
        }
        return cartQueryService.calculateSelectedTotal(cartIds);
    }

    //장바구니 - 주문 페이지 연결
    @PostMapping("/order")
    public String orderForm(@RequestParam List<Long> cartIds, Model model) {
        log.info("주문 폼 요청 받음. cartIds: {}", cartIds);
        if (isAnonymous()) {
            throw new CustomException(CustomErrorCode.LOGIN_REQUIRED);
        }

        // 선택 상품 주문 dto 변환
        List<CartResponseDto> selectedCarts = cartQueryService.getSelectedCarts(cartIds);
        List<AddOrderRequest.orderRequest> orderRequests = selectedCarts.stream()
                .map(cart -> cart.toOrderRequest())
                .collect(Collectors.toList());

        AddOrderRequest addOrderRequest = new AddOrderRequest(orderRequests);
        model.addAttribute("addOrderRequest", addOrderRequest);
        return "order/order-form";
    }
}
