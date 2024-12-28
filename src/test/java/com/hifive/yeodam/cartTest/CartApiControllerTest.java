/*package com.hifive.yeodam.cartTest;

import com.hifive.yeodam.cart.controller.CartApiController;
import com.hifive.yeodam.cart.dto.command.CartRequestDto;
import com.hifive.yeodam.cart.dto.command.LocalStorageCartDto;
import com.hifive.yeodam.cart.dto.query.CartResponseDto;
import com.hifive.yeodam.cart.dto.query.CartTotalPriceDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.service.CartService;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.tour.entity.Tour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartApiControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartApiController cartController;

    private CartRequestDto requestDto;
    private CartResponseDto responseDto;
    private Tour testTour;
    private Cart testCart;

    @BeforeEach
    void setUp() {

        testTour = Tour.builder()
                .itemName("테스트 상품")
                .region("테스트 지역")
                .period("1박2일")
                .description("테스트 여행")
                .price(100000)
                .reservation(true)
                .build();
        ReflectionTestUtils.setField(testTour, "id", 1L);

        testCart = Cart.builder()
                .item(testTour)
                .build();
        ReflectionTestUtils.setField(testCart, "id", 1L);

        requestDto = CartRequestDto.builder()
                .itemId(1L)
                .reservation(true)
                .build();

        responseDto = CartResponseDto.builder()
                .cart(testCart)
                .build();
    }

    @Test
    @DisplayName("로컬 스토리지 연동 성공")
    void syncCart_Success() {
        //given
        List<LocalStorageCartDto> localCart = Arrays.asList(
                LocalStorageCartDto.builder()
                        .itemId(1L)
                        .reservation(true)
                        .build()
        );
        doNothing().when(cartService).syncCartWithLocal(localCart);

        //when
        ResponseEntity<Void> response = cartController.syncCart(localCart);
        verify(cartService).syncCartWithLocal(localCart);
    }

    @Test
    @DisplayName("로컬 스토리지 동기화 실패 - 예약상품 중복")
    void syncCart_Failure() {
        //given
        List<LocalStorageCartDto> localCart = Arrays.asList(
                LocalStorageCartDto.builder()
                        .itemId(1L)
                        .reservation(true)
                        .build()
        );
        doThrow(new CustomException(CustomErrorCode.CART_ITEM_DUPLICATE))
                .when(cartService).syncCartWithLocal(localCart);
        //when
        ResponseEntity<Void> response = cartController.syncCart(localCart);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(cartService).syncCartWithLocal(localCart);
    }

    @Test
    @DisplayName("장바구니 상품 추가 성공")
    void addCart_Success() {
        // given
        when(cartService.addCart(any(CartRequestDto.class)))
                .thenReturn(responseDto);

        //when
        ResponseEntity<CartResponseDto> response = cartController.addCart(requestDto);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(cartService).addCart(requestDto);
    }

    //장바구니 예약 상품 수량 변경 실패

    @Test
    @DisplayName("장바구니 총 가격 조회 테스트")
    void getTotalPrice() {
        // given
        CartTotalPriceDto expectedDto = CartTotalPriceDto.builder()
                .totalPrice(200000)
                .build();
        when(cartService.getTotalPrice()).thenReturn(expectedDto);

        // when
        ResponseEntity<CartTotalPriceDto> response = cartController.getTotalPrice();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalPrice()).isEqualTo(expectedDto.getTotalPrice());
        verify(cartService).getTotalPrice();
    }

    @Test
    @DisplayName("장바구니 선택 상품 가격 조회 테스트")
    void getSelectedPrice() {
        // given
        List<Long> cartIds = Arrays.asList(1L, 2L);
        CartTotalPriceDto expectedDto = CartTotalPriceDto.builder()
                .totalPrice(200000)
                .build();
        when(cartService.getSelectedPrice(cartIds)).thenReturn(expectedDto);

        // when
        ResponseEntity<CartTotalPriceDto> response = cartController.getSelectedPrice(cartIds);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalPrice()).isEqualTo(expectedDto.getTotalPrice());
        verify(cartService).getSelectedPrice(cartIds);
    }

    @Test
    @DisplayName("장바구니 상품 삭제")
    void removeCart_Success() {
        // given
        Long cartId = 1L;
        doNothing().when(cartService).removeCart(cartId);

        // when
        ResponseEntity<Void> response = cartController.removeCart(cartId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(cartService).removeCart(cartId);
    }

}*/