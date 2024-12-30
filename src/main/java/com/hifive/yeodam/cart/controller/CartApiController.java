package com.hifive.yeodam.cart.controller;

import com.hifive.yeodam.cart.dto.command.CartRequestDto;
import com.hifive.yeodam.cart.dto.command.CartUpdateCountDto;
import com.hifive.yeodam.cart.dto.command.LocalStorageCartDto;
import com.hifive.yeodam.cart.dto.query.CartResponseDto;
import com.hifive.yeodam.cart.dto.query.CartTotalPriceDto;
import com.hifive.yeodam.cart.service.CartCommandService;
import com.hifive.yeodam.cart.service.CartQueryService;
import com.hifive.yeodam.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartApiController {

    private final CartCommandService cartCommandService;
    private final CartQueryService cartQueryService;

    //로컬 - 서버 동기화
    @PostMapping("/sync")
    public ResponseEntity<Void> syncCart(@RequestBody List<LocalStorageCartDto> localStorageCart) {
        cartCommandService.syncCartWithLocal(localStorageCart);
        return ResponseEntity.ok().build();
    }

    //장바구니 상품 추가
    @PostMapping
    public ResponseEntity<CartResponseDto> addCart(@RequestBody CartRequestDto requestDto) {
        CartResponseDto responseDto = cartCommandService.addCart(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //장바구니 전체 조회
    @GetMapping
    public ResponseEntity<CartTotalPriceDto> getTotalPrice() {
        CartTotalPriceDto totalPrice = cartQueryService.getTotalPrice();
        return ResponseEntity.ok(totalPrice);
    }

    //장바구니 선택 조회
    @GetMapping("/selected")
    public ResponseEntity<CartTotalPriceDto> getSelectedPrice(@RequestBody List<Long> cartIds) {
        CartTotalPriceDto selectedPrice = cartQueryService.getSelectedPrice(cartIds);
        return ResponseEntity.ok(selectedPrice);
    }

    //장바구니 상품 수량 변경
    @PatchMapping("/{id}/count")
    public ResponseEntity<CartResponseDto> updateCartCount(
            @PathVariable("id") Long cartId, @RequestBody CartUpdateCountDto updateDto) {
        try {
            CartResponseDto responseDto = cartCommandService.updateCartCount(cartId, updateDto);
            return ResponseEntity.ok(responseDto);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getCustomErrorCode().getHttpStatus()).build();
        }
    }

    //장바구니 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCart(@PathVariable Long cartId) {
        cartCommandService.removeCart(cartId);
        return ResponseEntity.ok().build();
    }
}
