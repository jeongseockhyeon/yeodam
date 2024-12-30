/*package com.hifive.yeodam.cartTest;

import com.hifive.yeodam.cart.dto.command.CartRequestDto;
import com.hifive.yeodam.cart.dto.command.LocalStorageCartDto;
import com.hifive.yeodam.cart.dto.query.CartResponseDto;
import com.hifive.yeodam.cart.dto.query.CartTotalPriceDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.repository.CartRepository;
import com.hifive.yeodam.cart.service.CartService;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    private Item testItem; // 장바구니 기능용
    private Tour testTour; // 예약 상품 테스트용
    private Cart testCart;

    //getCurrentUser() - 로그인 X, 로그인 O
    //getCartList() - 로그인 X 리스트 반환, 로그인 O 장바구니 목록 반환
    //syncCartWithLocal() - 로컬 - 서버 연동
    //존재하지 않는 상품 추가, 수량 엽데이트 1 미만, 존재하지 않는 유저로 수정

    private static class TestItem extends Item {
        @Builder
        public TestItem(String itemName, String description, int price, boolean reservation, int stock, double rate, boolean active) {
            super(null, itemName, description, price, reservation, stock, rate, active);
        }

        @Override
        public void updateSubItem(String region, String period, int maximum) {
            //테스트용 더미
        }
    }

    @BeforeEach
    void setUp() {
        testUser = new User();
        ReflectionTestUtils.setField(testUser, "id", 1L);

        testItem = TestItem.builder()
                .itemName("테스트 상품")
                .price(10000)
                .reservation(false)
                .stock(100)
                .build();
        ReflectionTestUtils.setField(testItem, "id", 1L);

        testTour = Tour.builder()
                .itemName("제주도 여행")
                .region("제주")
                .period("2박 3일")
                .price(100000)
                .reservation(true)
                .maximum(1)
                .stock(1)
                .build();
        ReflectionTestUtils.setField(testTour, "id", 2L);

        testCart = Cart.builder()
                .user(testUser)
                .item(testItem)
                .build();
        ReflectionTestUtils.setField(testCart, "id", 1L);

    }

    private void mockAuthUser() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@test.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
    }

    private void mockAnonymousUser() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication())
                .thenReturn(new AnonymousAuthenticationToken(
                        "key",
                        "anonymous",
                        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
                ));
        SecurityContextHolder.setContext(securityContext);
    }

    //Item ver.
    @Test
    @DisplayName("장바구니 상품 추가")
    void addCart() {
        //given
        mockAuthUser();
        CartRequestDto requestDto = CartRequestDto.builder()
                .itemId(1L)
                .count(2)
                .reservation(false)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(cartRepository.findByUserAndItem(testUser, testItem)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        //when
        CartResponseDto result = cartService.addCart(requestDto);

        //then
        assertThat(result).isNotNull();
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("장바구니 목록 조회")
    void getCartList() {
        //given
        mockAuthUser();
        List<Cart> expectedCarts = Arrays.asList(testCart);
        when(cartRepository.findByUser(testUser)).thenReturn(expectedCarts);

        //when
        List<Cart> result = cartService.getCartList();

        //then
        assertThat(result).isEqualTo(expectedCarts);
    }

    //장바구니 상품 삭제


    //Tour ver.
    //이미 존재하는 Tour 상품 추가
    //수량 1 미만으로 변경 시도

    @Test
    @DisplayName("로컬 스토리지 연동")
    void syncCart() {
        //given
        mockAuthUser();
        List<LocalStorageCartDto> localCart = Arrays.asList(
                LocalStorageCartDto.builder() //일반상품
                        .itemId(1L)
                        .count(2)
                        .reservation(false)
                        .build(),
                LocalStorageCartDto.builder() //예약상품
                        .itemId(2L)
                        .count(1)
                        .reservation(true)
                        .build()
        );

        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(testTour));
        when(cartRepository.findByUserAndItem(any(), any())).thenReturn(Optional.empty());

        //when
        cartService.syncCartWithLocal(localCart);

        //then
        verify(cartRepository, times(2)).save(any(Cart.class));
    }

    @Test
    @DisplayName("전체 장바구니 금액 계산")
    void getTotalPrice() {
        // given
        mockAuthUser();
        Cart itemCart = Cart.builder()
                .user(testUser)
                .item(testItem)
                .build();
        itemCart.updateCount(2); //20000

        Cart tourCart = Cart.builder()
                .user(testUser)
                .item(testTour)
                .build(); //100000

        List<Cart> carts = Arrays.asList(itemCart, tourCart);
        when(cartRepository.findByUser(testUser)).thenReturn(carts);

        //when
        CartTotalPriceDto result = cartService.getTotalPrice();

        //then
        assertThat(result.getTotalPrice()).isEqualTo(120000);
    }
}*/