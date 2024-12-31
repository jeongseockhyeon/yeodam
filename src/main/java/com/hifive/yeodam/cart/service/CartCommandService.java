package com.hifive.yeodam.cart.service;

import com.hifive.yeodam.cart.dto.command.CartRequestDto;
import com.hifive.yeodam.cart.dto.command.CartUpdateCountDto;
import com.hifive.yeodam.cart.dto.command.LocalStorageCartDto;
import com.hifive.yeodam.cart.dto.query.CartResponseDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.repository.CartRepository;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;


import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartCommandService {
    private static final int MAX_CART_SLOTS = 20; //장바구니 상품 종류 최대 개수

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return null;
        }

        //USER 권한 체크
        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            throw new AccessDeniedException("사용자 권한이 필요합니다.");
        }

        //로그인 사용자 정보 가져오기
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    //장바구니 최대 상품 종류 개수 반환
    public static int getMaxCartItems() {
        return MAX_CART_SLOTS;
    }

    public void syncCartWithLocal(List<LocalStorageCartDto> localStorageCart) {
        User user = getCurrentUser();
        if (user == null) {
            throw new CustomException(CustomErrorCode.LOGIN_REQUIRED);
        }

        int currentCart = cartRepository.countByUser(user);
        int remainingCart = MAX_CART_SLOTS - currentCart;

        if (remainingCart <= 0) {
            throw new CustomException(CustomErrorCode.CART_FULL);
        }

        //추가 가능 개수만큼만 처리
        List<LocalStorageCartDto> itemsToAdd = localStorageCart.size() > remainingCart
                ? localStorageCart.subList(0, remainingCart)
                : localStorageCart;

        for (LocalStorageCartDto localItem : itemsToAdd) {
            try {
                Item item = itemRepository.findById(localItem.getItemId())
                        .orElseThrow(() -> new CustomException(CustomErrorCode.ITEM_NOT_FOUND));

                if (localItem.isReservation()) {
                    boolean exists = cartRepository.findByUserAndItem(user, item).isPresent();
                    if (exists) {
                        log.warn("장바구니에 이미 존재하는 상품입니다. itemId: " + localItem.getItemId());
                        continue;
                    }
                }

                CartRequestDto requestDto;
                if (!localItem.isReservation()) {
                    requestDto = new CartRequestDto(
                            localItem.getItemId(),
                            item.getItemName(),
                            item.getPrice(),
                            localItem.getCount()
                    );
                } else {
                    Tour tour = (Tour) item;
                    requestDto = CartRequestDto.builder()
                            .itemId(localItem.getItemId())
                            .itemName(tour.getItemName())
                            .description(tour.getDescription())
                            .period(tour.getPeriod())
                            .region(tour.getRegion())
                            .price(tour.getPrice())
                            .build();
                }

                addCart(requestDto);
            } catch (Exception e) {
                log.warn("장바구니 연동 중 오류 발생: " + e.getMessage());
            }
        }

        if (localStorageCart.size() > remainingCart) {
            log.warn("장바구니 개수 제한으로 " + remainingCart + "개의 상품만 추가되었습니다.");
        }
    }

    public CartResponseDto addCart(CartRequestDto requestDto) {
        User user = getCurrentUser();
        if (user == null) {
            throw new CustomException(CustomErrorCode.LOGIN_REQUIRED);
        }

        //징바구니 개수 제한 검증 절차
        int currentCart = cartRepository.countByUser(user);
        if (currentCart >= MAX_CART_SLOTS) {
            throw new CustomException(CustomErrorCode.CART_FULL);
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

    public void removeCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CART_NOT_FOUND));

        cartRepository.delete(cart);
    }
}
