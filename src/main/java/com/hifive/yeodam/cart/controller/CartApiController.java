package com.hifive.yeodam.cart.controller;

import com.hifive.yeodam.cart.dto.command.CartRequestDto;
import com.hifive.yeodam.cart.dto.query.CartResponseDto;
import com.hifive.yeodam.cart.service.CartCommandService;
import com.hifive.yeodam.cart.service.CartQueryService;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartApiController {

    private final CartCommandService cartCommandService;
    private final CartQueryService cartQueryService;

    //로컬 - 서버 동기화
    @PostMapping("/sync")
    public ResponseEntity<Void> syncCart(@RequestBody List<CartRequestDto> cartRequests) {
        cartCommandService.syncCartWithLocal(cartRequests);
        return ResponseEntity.ok().build();
    }

    //장바구니 상품 추가
    @PostMapping
    public ResponseEntity<CartResponseDto> addCart(@RequestBody CartRequestDto requestDto) {
        try {
            log.info("장바구니 추가 요청 데이터: {}", requestDto);
            CartResponseDto responseDto = cartCommandService.addCart(requestDto);
            log.info("장바구니 추가 성공");
            return ResponseEntity.ok(responseDto);
        } catch (CustomException e) {
            log.error("장바구니 추가 실패 (CustomException): {}", e.getMessage());
            if (e.getCustomErrorCode() == CustomErrorCode.LOGIN_REQUIRED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            throw e;
        } catch (Exception e) {
            log.error("장바구니 추가 중 예외 발생: ", e);
            throw e;
        }
    }

    //장바구니 전체 조회
    @GetMapping
    public ResponseEntity<List<CartResponseDto>> getCarts() {
        List<CartResponseDto> carts = cartQueryService.getCarts();
        return ResponseEntity.ok(carts);
    }


    //장바구니 선택 조회
    @GetMapping("/selected")
    public ResponseEntity<List<CartResponseDto>> getSelectedCarts(@RequestParam("cartIds") List<Long> cartIds) {
        List<CartResponseDto> selectedCarts = cartQueryService.getSelectedCarts(cartIds);
        return ResponseEntity.ok(selectedCarts);
    }

    //장바구니 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCart(@PathVariable Long id) {
        log.info("카트 삭제 요청 ID: {}", id);
        try {
            cartCommandService.removeCart(id);
            log.info("카트 삭제 성공");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("카트 삭제 실패: {}", e.getMessage());
            throw e;
        }
    }
}