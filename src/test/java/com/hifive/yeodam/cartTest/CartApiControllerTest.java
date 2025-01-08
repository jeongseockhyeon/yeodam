//package com.hifive.yeodam.cartTest;
//
//import com.hifive.yeodam.cart.controller.CartApiController;
//import com.hifive.yeodam.cart.dto.command.CartRequestDto;
//import com.hifive.yeodam.cart.dto.command.CartUpdateCountDto;
//import com.hifive.yeodam.cart.dto.command.LocalStorageCartDto;
//import com.hifive.yeodam.cart.dto.query.CartResponseDto;
//import com.hifive.yeodam.cart.dto.query.CartTotalPriceDto;
//import com.hifive.yeodam.cart.entity.Cart;
//import com.hifive.yeodam.cart.service.CartCommandService;
//import com.hifive.yeodam.cart.service.CartQueryService;
//import com.hifive.yeodam.global.exception.CustomErrorCode;
//import com.hifive.yeodam.global.exception.CustomException;
//import com.hifive.yeodam.seller.entity.Guide;
//import com.hifive.yeodam.tour.entity.Tour;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertThrows;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CartApiControllerTest {
//
//    @Mock
//    private CartCommandService cartCommandService;
//
//    @Mock
//    private CartQueryService cartQueryService;
//
//    @InjectMocks
//    private CartApiController cartController;
//
//    private CartRequestDto tourRequestDto;
//    private CartRequestDto generalRequestDto;
//    private CartResponseDto responseDto;
//    private Tour testTour;
//    private Guide testGuide;
//    private Cart testCart;
//    private LocalDate startDate;
//    private LocalDate endDate;
//
//    @BeforeEach
//    void setUp() {
//
//        startDate = LocalDate.now();
//        endDate = LocalDate.now().plusDays(2);
//
//        testGuide = Guide.builder()
//                .name("테스트 가이드")
//                .build();
//        ReflectionTestUtils.setField(testGuide, "guideId", 1L);
//
//        testTour = Tour.builder()
//                .itemName("테스트 투어")
//                .description("테스트 여행")
//                .period("2박3일")
//                .region("테스트 지역")
//                .price(100000)
//                .reservation(true)
//                .build();
//        ReflectionTestUtils.setField(testTour, "id", 1L);
//
//        testCart = Cart.builder()
//                .item(testTour)
//                .guide(testGuide)
//                .startDate(startDate)
//                .endDate(endDate)
//                .build();
//        ReflectionTestUtils.setField(testCart, "id", 1L);
//
//        generalRequestDto = new CartRequestDto(2L, "일반상품", 50000, 2);
//
//        tourRequestDto = CartRequestDto.builder()
//                .itemId(1L)
//                .itemName("테스트 투어")
//                .description("테스트 여행")
//                .period("2박 3일")
//                .region("테스트 지역")
//                .price(100000)
//                .guideId(1L)
//                .startDate(startDate)
//                .endDate(endDate)
//                .build();
//
//        responseDto = CartResponseDto.builder()
//                .cart(testCart)
//                .build();
//    }
//
//    @Test
//    @DisplayName("로컬 스토리지 연동 성공 - 투어 상품")
//    void syncCart_Tour() {
//        //given
//        List<LocalStorageCartDto> localCart = Arrays.asList(
//                LocalStorageCartDto.builder()
//                        .itemId(1L)
//                        .reservation(true)
//                        .guideId(1L)
//                        .startDate(startDate)
//                        .endDate(endDate)
//                        .build()
//        );
//        doNothing().when(cartCommandService).syncCartWithLocal(localCart);
//
//        //when
//        ResponseEntity<Void> response = cartController.syncCart(localCart);
//
//        //then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        verify(cartCommandService).syncCartWithLocal(localCart);
//    }
//
//    @Test
//    @DisplayName("로컬 스토리지 연동 성공 - 일반 상품")
//    void syncCart_General() {
//        //given
//        List<LocalStorageCartDto> localCart = Arrays.asList(
//                LocalStorageCartDto.builder()
//                        .itemId(2L)
//                        .count(2)
//                        .reservation(false)
//                        .build()
//        );
//        doNothing().when(cartCommandService).syncCartWithLocal(localCart);
//
//        //when
//        ResponseEntity<Void> response = cartController.syncCart(localCart);
//
//        //then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        verify(cartCommandService).syncCartWithLocal(localCart);
//    }
//
//    @Test
//    @DisplayName("로컬 스토리지 연동 실패 - 예약상품 중복")
//    void syncCart_Failure() {
//        //given
//        List<LocalStorageCartDto> localCart = Arrays.asList(
//                LocalStorageCartDto.builder()
//                        .itemId(1L)
//                        .reservation(true)
//                        .build()
//        );
//
//        CustomException expectedException = new CustomException(CustomErrorCode.CART_ITEM_DUPLICATE);
//        doThrow(expectedException).when(cartCommandService).syncCartWithLocal(localCart);
//
//        //when & then
//        assertThrows(CustomException.class, () -> {
//            cartController.syncCart(localCart);
//        });
//        verify(cartCommandService).syncCartWithLocal(localCart);
//    }
//
//    @Test
//    @DisplayName("장바구니 투어 상품 추가")
//    void addTourCart() {
//        //given
//        when(cartCommandService.addCart(any(CartRequestDto.class)))
//                .thenReturn(responseDto);
//
//        //when
//        ResponseEntity<CartResponseDto> response = cartController.addCart(tourRequestDto);
//
//        //then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        verify(cartCommandService).addCart(tourRequestDto);
//    }
//
//    @Test
//    @DisplayName("장바구니 일반 상품 추가")
//    void addGeneralCart() {
//        //given
//        when(cartCommandService.addCart(any(CartRequestDto.class)))
//                .thenReturn(responseDto);
//
//        //when
//        ResponseEntity<CartResponseDto> response = cartController.addCart(generalRequestDto);
//
//        //then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        verify(cartCommandService).addCart(generalRequestDto);
//    }
//
//    @Test
//    @DisplayName("장바구니 총 가격 조회")
//    void getTotalPrice() {
//        // given
//        CartTotalPriceDto expectedDto = CartTotalPriceDto.builder()
//                .totalPrice(200000)
//                .build();
//        when(cartQueryService.getTotalPrice()).thenReturn(expectedDto);
//
//        // when
//        ResponseEntity<CartTotalPriceDto> response = cartController.getTotalPrice();
//
//        // then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getTotalPrice()).isEqualTo(expectedDto.getTotalPrice());
//        verify(cartQueryService).getTotalPrice();
//    }
//
//    @Test
//    @DisplayName("장바구니 선택 상품 가격 조회")
//    void getSelectedPrice() {
//        // given
//        List<Long> cartIds = Arrays.asList(1L, 2L);
//        CartTotalPriceDto expectedDto = CartTotalPriceDto.builder()
//                .totalPrice(200000)
//                .build();
//        when(cartQueryService.getSelectedPrice(cartIds)).thenReturn(expectedDto);
//
//        // when
//        ResponseEntity<CartTotalPriceDto> response = cartController.getSelectedPrice(cartIds);
//
//        // then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getTotalPrice()).isEqualTo(expectedDto.getTotalPrice());
//        verify(cartQueryService).getSelectedPrice(cartIds);
//    }
//
//    @Test
//    @DisplayName("장바구니 일반 상품 수량 변경")
//    void updateCartCount() {
//        // given
//        Long cartId = 1L;
//        CartUpdateCountDto updateDto = new CartUpdateCountDto(2, false);
//
//        Cart mockCart = mock(Cart.class);
//        CartResponseDto expectedResponse = mock(CartResponseDto.class);
//
//        when(cartCommandService.updateCartCount(cartId, updateDto)).thenReturn(expectedResponse);
//
//        // when
//        ResponseEntity<CartResponseDto> response = cartController.updateCartCount(cartId, updateDto);
//
//        // then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        verify(cartCommandService).updateCartCount(cartId, updateDto);
//    }
//
//    @Test
//    @DisplayName("장바구니 예약 상품 수량 변경 실패")
//    void updateCartCount_TourFailure() {
//        // given
//        Long cartId = 1L;
//
//        // when & then
//        assertThrows(IllegalArgumentException.class,
//                () -> new CartUpdateCountDto(2, true));
//    }
//
//    @Test
//    @DisplayName("장바구니 상품 삭제")
//    void removeCart_Success() {
//        // given
//        Long cartId = 1L;
//        doNothing().when(cartCommandService).removeCart(cartId);
//
//        // when
//        ResponseEntity<Void> response = cartController.removeCart(cartId);
//
//        // then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        verify(cartCommandService).removeCart(cartId);
//    }
//
//}