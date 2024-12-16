package com.hifive.yeodam.cart.service;

import com.hifive.yeodam.cart.dto.CartRequestDto;
import com.hifive.yeodam.cart.dto.CartResponseDto;
import com.hifive.yeodam.cart.dto.CartTotalPriceDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.entity.UserCart;
import com.hifive.yeodam.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CartService {

    private final CartRepository cartRepository;
    //private final UserRepository userRepository;
    //private final ItemRepository itemRepository;



    @Transactional
    public CartResponseDto addCart(CartRequestDto cartRequestDto) {
        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        UserCart userCart = user.getUserCart();

        Cart cart = new Cart(item);
        userCart.addCart(cart);
        Cart savedCart = cartRepository.save(cart);

        return new CartResponseDto(savedCart);
    }
    @Transactional
    public CartResponseDto updateCartCount(Long cartId, int count){
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));

        cart.updateCount(count);

        return new CartResponseDto(cart);
    }


    @Transactional
    public void removeCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));

        UserCart userCart = cart.getUserCart();
        userCart.removeCart(cart);
        cartRepository.delete(cart);
    }

    public CartTotalPriceDto getTotalPrice() {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        int totalPrice = user.getUserCart().getCarts().stream()
                .mapToInt(Cart::getPrice)
                .sum();
        return new CartTotalPriceDto(totalPrice);
    }

    public CartTotalPriceDto getSelectedPrice(List<Long> cartIds) {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        int selectedPrice = user.getUserCart().getCarts.stream()
                .filter(cart -> cartIds.contains(cart.getId()))
                .mapToInt(Cart::getPrice)
                .sum();

        return new CartTotalPriceDto(selectedPrice);
    }

}
