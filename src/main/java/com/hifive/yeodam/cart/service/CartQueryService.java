package com.hifive.yeodam.cart.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.cart.dto.command.CartRequestDto;
import com.hifive.yeodam.cart.dto.query.CartResponseDto;
import com.hifive.yeodam.cart.dto.query.CartTotalPriceDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.repository.CartRepository;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.entity.ItemImage;
import com.hifive.yeodam.tour.entity.Tour;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartQueryService {
    private final CartRepository cartRepository;
    private final AuthRepository authRepository;

    private Auth getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //비로그인
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return null;
        }

        //로그인 사용자 정보 가져오기
        String email = authentication.getName();
        return authRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.AUTH_NOT_FOUND));
    }

    public List<Cart> getCartList() {
        Auth auth = getCurrentUser();
        if (auth == null) {
            return new ArrayList<>(); //비로그인 시 빈 리스트 반환
        }
        log.info("Current auth: {}", auth.getId());
        List<Cart> carts = cartRepository.findByAuthWithItemsAndImages(auth);
        log.info("Found carts: {}", carts.size());
        return carts;
    }

    //장바구니 전체 조회 - Dto 변환
    public List<CartResponseDto> getCarts() {
        return getCartList().stream()
                .map(cart -> {
                    CartRequestDto requestDto = CartRequestDto.builder()
                            .itemId(cart.getItem().getId())
                            .tourName(cart.getItem().getItemName())
                            .tourRegion(cart.getItem() instanceof Tour ? ((Tour) cart.getItem()).getRegion() : null)
                            .tourPeriod(cart.getItem() instanceof Tour ? ((Tour) cart.getItem()).getPeriod() : null)
                            .maximum(cart.getItem() instanceof Tour ? ((Tour) cart.getItem()).getMaximum() : 0)
                            .guideId(cart.getGuide() != null ? cart.getGuide().getGuideId() : null)
                            .guideName(cart.getGuide() != null ? cart.getGuide().getName() : null) // guideName 추가
                            .imgUrl(cart.getItem().getItemImages().stream()
                                    .findFirst()
                                    .map(ItemImage::getStorePath)
                                    .orElse(null))
                            .startDate(cart.getStartDate())
                            .endDate(cart.getEndDate())
                            .build();

                    return new CartResponseDto(cart, requestDto);
                })
                .collect(Collectors.toList());
    }

    //장바구니 선택 조회
    public List<CartResponseDto> getSelectedCarts(List<Long> cartIds) {
        if (cartIds == null || cartIds.isEmpty()) {
            return Collections.emptyList();
        }

        return cartRepository.findByIdsWithItemsAndImages(cartIds).stream()
                .map(cart -> CartResponseDto.builder()
                        .cart(cart)
                        .requestDto(CartRequestDto.builder()
                                .itemId(cart.getItem().getId())
                                .tourName(cart.getItem().getItemName())
                                .tourRegion(cart.getItem() instanceof Tour ? ((Tour) cart.getItem()).getRegion() : null)
                                .tourPeriod(cart.getItem() instanceof Tour ? ((Tour) cart.getItem()).getPeriod() : null)
                                .maximum(cart.getItem() instanceof Tour ? ((Tour) cart.getItem()).getMaximum() : 0)
                                .guideId(cart.getGuide() != null ? cart.getGuide().getGuideId() : null)
                                .imgUrl(cart.getItem().getItemImages().stream()
                                        .filter(ItemImage::isThumbnail)
                                        .findFirst()
                                        .map(ItemImage::getStorePath)
                                        .orElse(null))
                                .startDate(cart.getStartDate())
                                .endDate(cart.getEndDate())
                                .build())
                        .build())
                .collect(Collectors.toList());
    }

    //장바구니 선택 항목 총 가격 계산
    public CartTotalPriceDto calculateSelectedTotal(List<Long> cartIds) {
        if (cartIds == null || cartIds.isEmpty()) {
            return CartTotalPriceDto.builder().tourPrice(0).build();
        }

        Auth auth = getCurrentUser();
        if (auth == null) {
            throw new CustomException(CustomErrorCode.LOGIN_REQUIRED);
        }

        int totalPrice = cartRepository.findByIdsWithItemsAndImages(cartIds).stream()
                .mapToInt(Cart::getTourPrice)
                .sum();

        return CartTotalPriceDto.builder()
                .tourPrice(totalPrice)
                .build();
    }
}
