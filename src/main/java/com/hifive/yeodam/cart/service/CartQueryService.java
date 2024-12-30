package com.hifive.yeodam.cart.service;

import com.hifive.yeodam.cart.dto.query.CartTotalPriceDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.repository.CartRepository;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartQueryService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //비로그인
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return null;
        }

        //로그인 사용자 정보 가져오기
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    public List<Cart> getCartList() {
        User user = getCurrentUser();
        if (user == null) {
            return new ArrayList<>(); //비로그인 시 빈 리스트 반환
        }
        return cartRepository.findByUserWithItemsAndImages(user);
    }

    public CartTotalPriceDto getTotalPrice() {
        User user = getCurrentUser();
        if (user == null) {
            return CartTotalPriceDto.builder()
                    .totalPrice(0)
                    .build();
        }

        List<Cart> carts = cartRepository.findByUserWithItemsAndImages(user);
        int totalPrice = carts.stream()
                .mapToInt(Cart::getPrice)
                .sum();

        return CartTotalPriceDto.builder()
                .totalPrice(totalPrice)
                .build();
    }

    public CartTotalPriceDto getSelectedPrice(List<Long> cartIds) {
        User user = getCurrentUser();
        if (user == null) {
            //로컬 스토리지 이용
            return CartTotalPriceDto.builder()
                    .totalPrice(0)
                    .build();
        }

        List<Cart> carts = cartRepository.findByUserWithItemsAndImages(user);
        int selectedPrice = carts.stream()
                .filter(cart -> cartIds.contains(cart.getId()))
                .mapToInt(Cart::getPrice)
                .sum();

        return CartTotalPriceDto.builder()
                .totalPrice(selectedPrice)
                .build();
    }

    public List<Cart> findByCartIds(List<Long> cartIds) {
        return cartRepository.findByIdsWithItemsAndImages(cartIds);
    }
}
