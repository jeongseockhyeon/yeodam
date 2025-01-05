package com.hifive.yeodam.cart.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.cart.dto.command.CartRequestDto;
import com.hifive.yeodam.cart.dto.query.CartResponseDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.repository.CartRepository;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.entity.ItemImage;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.tour.entity.TourGuide;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartCommandService {
    private static final int MAX_CART_SLOTS = 20; //장바구니 상품 종류 최대 개수

    private final CartRepository cartRepository;
    private final AuthRepository authRepository;
    private final ItemRepository itemRepository;

    private Auth getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        //USER 권한 체크
        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            throw new CustomException(CustomErrorCode.UNAUTHORIZED_ACCESS);
        }

        //로그인 사용자 정보 가져오기
        String email = authentication.getName();
        return authRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.AUTH_NOT_FOUND));
    }

    //장바구니 최대 상품 종류 개수 반환
    public static int getMaxCartItems() {
        return MAX_CART_SLOTS;
    }

    public void syncCartWithLocal(List<CartRequestDto> cartRequests) {
        Auth auth = getCurrentUser();
        if (auth == null) {
            throw new CustomException(CustomErrorCode.LOGIN_REQUIRED);
        }

        int currentCart = cartRepository.countByAuth(auth);
        int remainingCart = MAX_CART_SLOTS - currentCart;

        if (remainingCart <= 0) {
            throw new CustomException(CustomErrorCode.CART_FULL);
        }

        //추가 가능 개수만큼만 처리
        List<CartRequestDto> itemsToAdd = cartRequests.size() > remainingCart
                ? cartRequests.subList(0, remainingCart)
                : cartRequests;

        for (CartRequestDto cartRequest : itemsToAdd) {
            try {
                Item item = itemRepository.findById(cartRequest.getItemId())
                        .orElseThrow(() -> new CustomException(CustomErrorCode.ITEM_NOT_FOUND));

                boolean exists = cartRepository.findByAuthAndItem(auth, item).isPresent();
                if (exists) {
                    log.warn("장바구니에 이미 존재하는 상품입니다. itemId: " + cartRequest.getItemId());
                    continue;
                }

                addCart(cartRequest);
            } catch (Exception e) {
                log.warn("장바구니 연동 중 오류 발생: " + e.getMessage());
            }
        }

        if (cartRequests.size() > remainingCart) {
            log.warn("장바구니 개수 제한으로 " + remainingCart + "개의 상품만 추가되었습니다.");
        }
    }

    public CartResponseDto addCart(CartRequestDto requestDto) {
        log.info("카트 추가 요청: {}", requestDto);
        Auth auth = getCurrentUser();
        if (auth == null) {
            throw new CustomException(CustomErrorCode.LOGIN_REQUIRED);
        }

        //장바구니 개수 제한 검증 절차
        int currentCart = cartRepository.countByAuth(auth);
        if (currentCart >= MAX_CART_SLOTS) {
            throw new CustomException(CustomErrorCode.CART_FULL);
        }

        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.ITEM_NOT_FOUND));

        //동일 상품 확인
        Optional<Cart> existingCart = cartRepository.findByAuthAndItem(auth, item);
        if (existingCart.isPresent()) {
            throw new CustomException(CustomErrorCode.CART_ITEM_DUPLICATE);
        }

        Cart cart;
        if (item instanceof Tour) {
            Tour tour = (Tour) item;

            // requestDto에서 전달받은 guideId로 해당 가이드 찾기
            Guide guide = null;
            if (requestDto.getGuideId() != null) {
                guide = tour.getTourGuides().stream()
                        .filter(tg -> tg.getGuide().getGuideId().equals(requestDto.getGuideId()))
                        .map(TourGuide::getGuide)
                        .findFirst()
                        .orElse(null);
            }

            cart = Cart.builder()
                    .auth(auth)
                    .item(item)
                    .guide(guide)
                    .startDate(requestDto.getStartDate())
                    .endDate(requestDto.getEndDate())
                    .build();
        } else {
            cart = Cart.builder()
                    .auth(auth)
                    .item(item)
                    .build();
        }

        Cart savedCart = cartRepository.save(cart);
        return CartResponseDto.builder()
                .cart(savedCart)
                .requestDto(CartRequestDto.builder()
                        .itemId(item.getId())
                        .tourName(item.getItemName())
                        .tourRegion(item instanceof Tour ? ((Tour) item).getRegion() : null)
                        .tourPeriod(item instanceof Tour ? ((Tour) item).getPeriod() : null)
                        .maximum(item instanceof Tour ? ((Tour) item).getMaximum() : 0)
                        .guideId(savedCart.getGuide() != null ? savedCart.getGuide().getGuideId() : null)
                        .imgUrl(getItemThumbnailUrl(item))
                        .startDate(requestDto.getStartDate())
                        .endDate(requestDto.getEndDate())
                        .build())
                .build();
    }

    private String getItemThumbnailUrl(Item item) {
        return item.getItemImages().stream()
                .findFirst()
                .map(ItemImage::getStorePath)
                .orElse(null);
    }

    public void removeCart(Long cartId) {
        log.info("카트 삭제 시작 - cartId: {}", cartId);
        try {
            Cart cart = cartRepository.findByIdAndAuth(cartId, getCurrentUser())
                    .orElseThrow(() -> new CustomException(CustomErrorCode.CART_NOT_FOUND));
            cartRepository.delete(cart);
            log.info("카트 삭제 완료 - cartId: {}", cartId);
        } catch (Exception e) {
            log.error("카트 삭제 실패 - cartId: {}, error: {}", cartId, e.getMessage());
            throw e;
        }
    }
}