package com.hifive.yeodam.cart.controller;

import com.hifive.yeodam.cart.dto.*;
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

    //로컬 - 서버 동기화
    @PostMapping("/sync")
    public ResponseEntity<Void> syncCart(@RequestBody List<LocalStorageCartDto> localStorageCart) {
        try {
            cartService.syncCartWithLocal(localStorageCart);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //장바구니 상품 추가
    @PostMapping
    public ResponseEntity<CartResponseDto> addCart(@RequestBody CartRequestDto requestDto) {
        CartResponseDto responseDto = cartService.addCart(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //장바구니 전체 조회
    @GetMapping
    public ResponseEntity<CartTotalPriceDto> getTotalPrice() {
        CartTotalPriceDto totalPrice = cartService.getTotalPrice();
        return ResponseEntity.ok(totalPrice);
    }

    //장바구니 선택 조회
    @GetMapping("/selected")
    public ResponseEntity<CartTotalPriceDto> getSelectedPrice(@RequestBody List<Long> cartIds) {
        CartTotalPriceDto selectedPrice = cartService.getSelectedPrice(cartIds);
        return ResponseEntity.ok(selectedPrice);
    }

    //장바구니 상품 수량 변경
    @PatchMapping("/{id}/count")
    public ResponseEntity<CartResponseDto> updateCartCount(
            @PathVariable("id") Long cartId, @RequestBody CartUpdateCountDto updateDto) {
        try {
            CartResponseDto responseDto = cartService.updateCartCount(cartId, updateDto);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    //장바구니 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCart(@PathVariable Long cartId) {
        cartService.removeCart(cartId);
        return ResponseEntity.ok().build();
    }

}
