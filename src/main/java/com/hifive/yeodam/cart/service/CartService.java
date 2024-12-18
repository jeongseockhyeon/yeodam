package com.hifive.yeodam.cart.service;

import com.hifive.yeodam.cart.dto.*;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.repository.CartRepository;
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

        return userRepository.findById(1L) //인증 구현 후 수정 예정
                .orElse(null);

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
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        for (LocalStorageCartDto localItem : localStorageCart) {
            CartRequestDto requestDto = new CartRequestDto(localItem.getItemId(), localItem.getCount());
            try {
                addCart(requestDto);
            } catch (Exception e) {
                //연동 오류 처리
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
            throw new IllegalStateException("장바구니에 상품을 저장할 수 없습니다.");
        }

        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        //동일 상품 확인
        Optional<Cart> existingCart = cartRepository.findByUserAndItem(user, item);

        if (existingCart.isPresent()) {
            if (!item.isReservation()) {
                //일반 상품인 경우 수량 증가
                Cart cart = existingCart.get();
                cart.updateCount(cart.getCount() + requestDto.getCount());
                return new CartResponseDto(cart);
            } else {
                throw new IllegalStateException("이미 장바구니에 존재하는 상품입니다.");
            }
        }

        //장바구니에 없는 상품인 경우 추가
        Cart cart = new Cart(user, item);
        if (!item.isReservation()) {
            cart.updateCount(requestDto.getCount());
        }
        Cart savedCart = cartRepository.save(cart);

        return new CartResponseDto(savedCart);
    }

    @Transactional
    public CartResponseDto updateCartCount(Long cartId, CartUpdateCountDto updateDto) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));

        //수량 변경 가능 여부 확인
        if (!cart.isCountModifiable()) {
            throw new IllegalStateException("예약 상품은 수량 변경이 불가능합니다.");
        }
        if (updateDto.getCount() < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        cart.updateCount(updateDto.getCount());
        return new CartResponseDto(cart);
    }

    public CartTotalPriceDto getTotalPrice() {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        int totalPrice = cartRepository.findByUser(user).stream()
                .mapToInt(Cart::getPrice)
                .sum();
        return new CartTotalPriceDto(totalPrice);
    }

    public CartTotalPriceDto getSelectedPrice(List<Long> cartIds) {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        int selectedPrice = cartRepository.findByUser(user).stream()
                .filter(cart -> cartIds.contains(cart.getId()))
                .mapToInt(Cart::getPrice)
                .sum();

        return new CartTotalPriceDto(selectedPrice);
    }


    @Transactional
    public void removeCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));

        cartRepository.delete(cart);
    }


}
