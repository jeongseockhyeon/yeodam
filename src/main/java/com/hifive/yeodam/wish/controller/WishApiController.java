package com.hifive.yeodam.wish.controller;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomErrorResponseDto;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.wish.dto.WishDto;
import com.hifive.yeodam.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishes")
@RequiredArgsConstructor
public class WishApiController {
    private final WishService wishService;

    @PostMapping("/{itemId}")
    public ResponseEntity<CustomErrorResponseDto> addWish(
            @PathVariable Long itemId,
            @AuthenticationPrincipal Auth auth
    ) {
        if (auth == null) {
            throw new CustomException(CustomErrorCode.AUTH_NOT_FOUND);
        }
        wishService.addWish(auth.getId(), itemId);
        return ResponseEntity.ok()
                .body(CustomErrorResponseDto.builder()
                        .statusCode(HttpStatus.OK.value())
                        .code("SUCCESS")
                        .message("찜 목록에 추가되었습니다.")
                        .build()
                );
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<CustomErrorResponseDto> removeWish(
            @PathVariable Long itemId,
            @AuthenticationPrincipal Auth auth
    ) {
        if (auth == null) {
            throw new CustomException(CustomErrorCode.AUTH_NOT_FOUND);
        }
        wishService.removeWish(auth.getId(), itemId);
        return ResponseEntity.ok()
                .body(CustomErrorResponseDto.builder()
                        .statusCode(HttpStatus.OK.value())
                        .code("SUCCESS")
                        .message("찜이 취소되었습니다.")
                        .build()
                );
    }

    @GetMapping
    public ResponseEntity<List<WishDto>> getWish(
            @AuthenticationPrincipal Auth auth
    ) {
        if (auth == null) {
            throw new CustomException(CustomErrorCode.AUTH_NOT_FOUND);
        }
        List<WishDto> wishes = wishService.getWish(auth.getId());
        return ResponseEntity.ok(wishes);
    }

    @GetMapping("/check/{itemId}")
    public ResponseEntity<CustomErrorResponseDto> isWished(
            @PathVariable Long itemId,
            @AuthenticationPrincipal Auth auth
    ){
        if (auth == null) {
            throw new CustomException(CustomErrorCode.AUTH_NOT_FOUND);
        }
        boolean isWished = wishService.isWished(auth.getId(), itemId);
        return ResponseEntity.ok()
                .body(CustomErrorResponseDto.builder()
                        .statusCode(HttpStatus.OK.value())
                        .code("SUCCESS")
                        .message(String.valueOf(isWished))
                        .build()
                );
    }
}