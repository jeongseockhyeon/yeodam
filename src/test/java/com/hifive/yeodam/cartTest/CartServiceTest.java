package com.hifive.yeodam.cartTest;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.cart.dto.CartRequestDto;
import com.hifive.yeodam.cart.dto.CartResponseDto;
import com.hifive.yeodam.cart.dto.CartTotalPriceDto;
import com.hifive.yeodam.cart.dto.CartUpdateCountDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.repository.CartRepository;
import com.hifive.yeodam.cart.service.CartService;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Cart testCart;

    private Tour createTestTour(String itemName, String region, int price, boolean isReservation) {
        Tour tour = Tour.builder()
                .sellerId(1L)
                .itemName(itemName)
                .region(region)
                .period("1박2일")
                .description(region + "여행")
                .price(price)
                .build();

        tour.setReservation(isReservation);
        ReflectionTestUtils.setField(tour, "id",1L);
        return tour;
    }

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .name("테스트유저")
                .birthDate(LocalDate.of(2024,12,11))
                .nickname("테스트닉네임")
                .gender("M")
                .auth(new Auth())
                .build();
        ReflectionTestUtils.setField(testUser, "id", 1L);

        Tour testTour = createTestTour("제주도 여행", "제주도", 100000, true);

        testCart = new Cart(testUser, testTour);
        ReflectionTestUtils.setField(testCart, "id",1L);
    }


    @Test
    @DisplayName("장바구니 추가 - 새로운 상품")
    void addCart_NewItem() {
        // given
        CartRequestDto requestDto = new CartRequestDto();
        requestDto.setItemId(1L);
        requestDto.setCount(2);

        Tour tour = createTestTour("서울 여행", "서울", 50000, true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(cartRepository.findByUserAndItem(testUser, tour)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // when
        CartResponseDto responseDto = cartService.addCart(requestDto);

        // then
        assertThat(responseDto).isNotNull();
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("장바구니 추가 - 이미 장바구니에 존재하는 상품")
    void addCart_ExistingItem() {
        // given
        CartRequestDto requestDto = new CartRequestDto();
        requestDto.setItemId(1L);
        requestDto.setCount(2);

        Tour tour = createTestTour("제주도 여행", "제주도", 100000,true);
        tour.setReservation(false);
        Cart cart = new Cart(testUser, tour);
        cart.setCount(1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(cartRepository.findByUserAndItem(testUser, tour)).thenReturn(Optional.of(cart));

        // when
        CartResponseDto responseDto = cartService.addCart(requestDto);

        // then
        assertThat(responseDto.getCount()).isEqualTo(3); // 1 + 2
    }

    @Test
    @DisplayName("장바구니 추가 - 이미 존재하는 예약 상품")
    void addCart_ExistingReservationItem() {
        // given
        CartRequestDto requestDto = new CartRequestDto();
        requestDto.setItemId(1L);
        requestDto.setCount(1);

        Tour tour = createTestTour("부산 여행", "부산", 80000, true);
        tour.setReservation(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(cartRepository.findByUserAndItem(testUser, tour)).thenReturn(Optional.of(testCart));

        // when & then
        assertThatThrownBy(() -> cartService.addCart(requestDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 장바구니에 존재하는 상품입니다.");
    }

    @Test
    @DisplayName("장바구니 수량 수정 - 일반 상품")
    void updateCartCount_NormalItem() {
        // given
        Tour tour = createTestTour("부산 여행", "부산", 80000,false);
        ReflectionTestUtils.setField(tour, "id", 1L);
        tour.setReservation(false); //일반 상품

        Cart cart = new Cart(testUser, tour);
        ReflectionTestUtils.setField(cart, "id", 1L);
        cart.setCount(2);

        CartUpdateCountDto updateDto = new CartUpdateCountDto();
        updateDto.setCount(5);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        // when
        CartResponseDto responseDto = cartService.updateCartCount(1L, updateDto);

        // then
        assertThat(responseDto.getCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("장바구니 수량 수정 - 예약 상품")
    void updateCartCount_ReservationItem() {
        // given
        CartUpdateCountDto updateDto = new CartUpdateCountDto();
        updateDto.setCount(2);

        Tour tour = createTestTour("부산 여행", "부산", 80000, true);
        Cart cart = new Cart(testUser, tour);
        cart.setCount(1);
        ReflectionTestUtils.setField(cart, "id", 1L);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        // when & then
        assertThatThrownBy(() -> cartService.updateCartCount(1L, updateDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("예약 상품은 수량 변경이 불가능합니다.");
    }

    @Test
    @DisplayName("장바구니 삭제")
    void removeCart() {
        // given
        when(cartRepository.findById(1L)).thenReturn(Optional.of(testCart));

        // when
        cartService.removeCart(1L);

        // then
        verify(cartRepository).delete(testCart);
    }

    @Test
    @DisplayName("전체 장바구니 금액 계산")
    void getTotalPrice() {
        // given
        Tour reservationTour = createTestTour("경주 여행", "경주", 60000, true);
        Tour ticketTour = createTestTour("경주 여행", "경주", 6000, false);

        Cart reservationCart = new Cart(testUser, reservationTour);
        Cart ticketCart = new Cart(testUser, ticketTour);
        ticketCart.setCount(2); //일반 상품 수량 적용

        List<Cart> carts = Arrays.asList(reservationCart, ticketCart);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(carts);

        // when
        CartTotalPriceDto totalPrice = cartService.getTotalPrice();

        // then
        assertThat(totalPrice.getTotalPrice()).isEqualTo(72000);
    }

    @Test
    @DisplayName("선택된 장바구니 항목 금액 계산")
    void getSelectedPrice() {
        // given
        Tour reservationTour = createTestTour("여수 여행", "여수", 30000, true);
        Tour ticketTour1 = createTestTour("여수 여행", "여수", 5000, false);
        Tour ticketTour2 = createTestTour("여수 여행", "여수", 3000, false);

        Cart reservationCart = new Cart(testUser, reservationTour);
        Cart ticketCart1 = new Cart(testUser, ticketTour1);
        Cart ticketCart2 = new Cart(testUser, ticketTour2);
        ReflectionTestUtils.setField(reservationCart, "id", 1L);
        ReflectionTestUtils.setField(ticketCart1, "id", 2L);
        ReflectionTestUtils.setField(ticketCart2, "id", 3L);
        ticketCart1.setCount(2);
        ticketCart2.setCount(1);

        List<Long> selectedIds = Arrays.asList(1L,2L,3L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Arrays.asList(reservationCart, ticketCart1, ticketCart2));

        // when
        CartTotalPriceDto selectedPrice = cartService.getSelectedPrice(selectedIds);

        // then
        assertThat(selectedPrice.getTotalPrice()).isEqualTo(43000);
    }
}