package com.hifive.yeodam.cart.controller;

import com.hifive.yeodam.cart.dto.CartRequestDto;
import com.hifive.yeodam.cart.dto.CartResponseDto;
import com.hifive.yeodam.cart.dto.CartTotalPriceDto;
import com.hifive.yeodam.cart.dto.CartUpdateCountDto;
import com.hifive.yeodam.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartApiController {

    private final CartService cartService;

//    //장바구니 상품 추가
//    @PostMapping
//    public ResponseEntity<CartResponseDto> addCart(@RequestBody CartRequestDto requestDto) {
//        CartResponseDto responseDto = cartService.addCart(requestDto);
//        return ResponseEntity.ok(responseDto);
//    }
//
//    //장바구니 상품 수량 변경
//    @PatchMapping("/{id}/count")
//    public ResponseEntity<CartResponseDto> updateCartCount(
//            @PathVariable Long cartId, @RequestBody CartUpdateCountDto updateDto) {
//        try {
//            CartResponseDto responseDto = cartService.updateCartCount(cartId, updateDto);
//            return ResponseEntity.ok(responseDto);
//        } catch (IllegalStateException | IllegalArgumentException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//
//    //장바구니 상품 삭제
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> removeCart(@PathVariable Long cartId) {
//        cartService.removeCart(cartId);
//        return ResponseEntity.ok().build();
//    }
//
//    //장바구니 전체 조회
//    @GetMapping
//    public ResponseEntity<CartTotalPriceDto> getTotalPrice() {
//        CartTotalPriceDto totalPrice = cartService.getTotalPrice();
//        return ResponseEntity.ok(totalPrice);
//    }
//
//    //장바구니 선택 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<CartTotalPriceDto> getSelectedPrice(@RequestBody List<Long> cartIds) {
//        CartTotalPriceDto selectedPrice = cartService.getSelectedPrice(cartIds);
//        return ResponseEntity.ok(selectedPrice);
//    }
}
