package com.hifive.yeodam.cart.service;

import com.hifive.yeodam.cart.dto.CartRequestDto;
import com.hifive.yeodam.cart.dto.CartResponseDto;
import com.hifive.yeodam.cart.dto.CartTotalPriceDto;
import com.hifive.yeodam.cart.dto.CartUpdateCountDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.repository.CartRepository;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public List<Cart> getCartList() {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        return cartRepository.findByUser(user);
    }


    @Transactional
    public CartResponseDto addCart(CartRequestDto requestDto) {
        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        //이미 장바구니에 있는 상품인지 확인
        Optional<Cart> existedCart = cartRepository.findByUserAndItem(user, item);

        if (existedCart.isPresent()) {
            if (!item.isReservation()) {
                //예약 상품이 아닌 경우 수량 증가
                Cart cart = existedCart.get();
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


    @Transactional
    public void removeCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));

        cartRepository.delete(cart);
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

}
