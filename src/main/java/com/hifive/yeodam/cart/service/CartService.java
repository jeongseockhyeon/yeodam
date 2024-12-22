package com.hifive.yeodam.cart.service;

import com.hifive.yeodam.cart.dto.*;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.repository.CartRepository;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

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
            //AuthException으로 처리 예정
    }

    public List<Cart> getCartList() {
        User user = getCurrentUser();
        if (user == null) {
            return new ArrayList<>(); //비로그인 시 빈 리스트 반환
        }

        return cartRepository.findByUser(user);
    }

    @Transactional
    public void syncCartWithLocal(List<LocalStorageCartDto> localStorageCart) {
        User user = getCurrentUser();
        if (user == null) {
            throw new CustomException(CustomErrorCode.LOGIN_REQUIRED);
        }

        for (LocalStorageCartDto localItem : localStorageCart) {
            try {
                if (localItem.isReservation()) {
                    Item item = itemRepository.findById(localItem.getItemId())
                            .orElseThrow(() -> new CustomException(CustomErrorCode.ITEM_NOT_FOUND));

                    boolean exists = cartRepository.findByUserAndItem(user, item).isPresent();
                    if (exists) {
                        log.warn("장바구니에 이미 존재하는 상품입니다. itemId: " + localItem.getItemId());
                        continue;
                    }
                }
                CartRequestDto requestDto = CartRequestDto.builder()
                        .itemId(localItem.getItemId())
                        .count(localItem.getCount())
                        .reservation(localItem.isReservation())
                        .build();

                addCart(requestDto);
            } catch (Exception e) {
                log.warn("장바구니 연동 중 오류 발생: " + e.getMessage());
            }
        }
    }


    @Transactional
    public CartResponseDto addCart(CartRequestDto requestDto) {
        // 현재 사용자 확인
        User user = getCurrentUser();
        if (user == null) {
            //비로그인 상태는 로컬 스토리지 저장 - 예외 발생
            throw new CustomException(CustomErrorCode.LOGIN_REQUIRED);
        }

        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.ITEM_NOT_FOUND));

        if (item.isReservation() != requestDto.isReservation()) {
            throw new CustomException(CustomErrorCode.CART_ITEM_TYPE_MISMATCH);
        }
        //동일 상품 확인
        Optional<Cart> existingCart = cartRepository.findByUserAndItem(user, item);

        if (existingCart.isPresent()) {
            if (!item.isReservation()) {
                //일반 상품인 경우 수량 증가
                Cart cart = existingCart.get();
                cart.addCount(requestDto.getCount());
                return CartResponseDto.builder()
                        .cart(cart)
                        .build();
            }
            throw new CustomException(CustomErrorCode.CART_ITEM_DUPLICATE);
        }

        Cart cart = Cart.builder()
                .user(user)
                .item(item)
                .build();

        if (!item.isReservation()) {
            cart.updateCount(requestDto.getCount());
        }

        Cart savedCart = cartRepository.save(cart);
        return CartResponseDto.builder()
                .cart(savedCart)
                .build();
    }

    @Transactional
    public CartResponseDto updateCartCount(Long cartId, CartUpdateCountDto updateDto) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CART_NOT_FOUND));

        //수량 변경 가능 여부 확인
        if (cart.getItem().isReservation() != updateDto.isReservation()) {
            throw new CustomException(CustomErrorCode.CART_ITEM_TYPE_MISMATCH);
        }
        if (!cart.isCountModifiable()) {
            throw new CustomException(CustomErrorCode.CART_ITEM_COUNT_NOT_MODIFIABLE);
        }

        cart.updateCount(updateDto.getCount());
        return CartResponseDto.builder()
                .cart(cart)
                .build();
    }

    public CartTotalPriceDto getTotalPrice() {
        User user = getCurrentUser();
        if (user == null) {
            //로컬 스토리지 이용
            return CartTotalPriceDto.builder()
                    .totalPrice(0)
                    .build();
        }

        int totalPrice = cartRepository.findByUser(user).stream()
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

        int selectedPrice = cartRepository.findByUser(user).stream()
                .filter(cart -> cartIds.contains(cart.getId()))
                .mapToInt(Cart::getPrice)
                .sum();

        return CartTotalPriceDto.builder()
                .totalPrice(selectedPrice)
                .build();
    }


    @Transactional
    public void removeCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CART_NOT_FOUND));

        cartRepository.delete(cart);
    }


}
