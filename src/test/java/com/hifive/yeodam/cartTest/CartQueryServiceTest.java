//package com.hifive.yeodam.cartTest;
//
//import com.hifive.yeodam.auth.entity.Auth;
//import com.hifive.yeodam.auth.entity.RoleType;
//import com.hifive.yeodam.cart.entity.Cart;
//import com.hifive.yeodam.cart.repository.CartRepository;
//import com.hifive.yeodam.tour.entity.Tour;
//import com.hifive.yeodam.user.entity.User;
//import com.hifive.yeodam.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import com.hifive.yeodam.cart.service.CartQueryService;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CartQueryServiceTest {
//    @InjectMocks
//    private CartQueryService cartQueryService;
//
//    @Mock
//    private CartRepository cartRepository;
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private SecurityContext securityContext;
//    @Mock
//    private Authentication authentication;
//
//    private User testUser;
//    private Cart testCart;
//    private Tour testTour;
//
//    @BeforeEach
//    void setUp() {
//        // User 설정
//        testUser = User.builder()
//                .id(1L)
//                .name("Test User")
//                .nickname("Tester")
//                .phone("01012345678")
//                .auth(Auth.builder()
//                        .id(1L)
//                        .email("test@test.com")
//                        .role(RoleType.USER)
//                        .build())
//                .build();
//
//        // Tour 설정
//        testTour = mock(Tour.class);
//        lenient().when(testTour.getId()).thenReturn(1L);
//        lenient().when(testTour.getItemName()).thenReturn("Test Tour");
//        lenient().when(testTour.getPrice()).thenReturn(50000);
//        lenient().when(testTour.isReservation()).thenReturn(true);
//        lenient().when(testTour.getItemImages()).thenReturn(new ArrayList<>());
//
//        testCart = Cart.builder()
//                .user(testUser)
//                .item(testTour)
//                .build();
//        ReflectionTestUtils.setField(testCart, "id", 1L);
//        ReflectionTestUtils.setField(testCart, "count", 1);
//
//        // Security Context 설정
//        SecurityContextHolder.setContext(securityContext);
//    }
//
//    @Nested
//    @DisplayName("장바구니 목록 조회")
//    class GetCartListTest {
//        @Test
//        @DisplayName("로그인 사용자의 장바구니 목록 조회 성공")
//        void getCartListSuccess() {
//            // Given
//            when(securityContext.getAuthentication()).thenReturn(authentication);
//            when(authentication.getName()).thenReturn("test@test.com");
//            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
//
//            List<Cart> expectedCarts = List.of(testCart);
//            when(cartRepository.findByUserWithItemsAndImages(testUser)).thenReturn(expectedCarts);
//
//            // When
//            List<Cart> result = cartQueryService.getCartList();
//
//            // Then
//            assertThat(result).hasSize(1);
//            Cart cart = result.get(0);
//            assertThat(cart.getUser()).isEqualTo(testUser);
//            assertThat(cart.getItem()).isEqualTo(testTour);
//            assertThat(cart.getItem().getItemImages()).isNotNull();
//        }
//
//        @Test
//        @DisplayName("비로그인 사용자는 빈 장바구니 목록 반환")
//        void getCartListAnonymous() {
//            // Given - 로그인하지 않은 상태 설정
//            when(securityContext.getAuthentication()).thenReturn(null);
//
//            // When
//            List<Cart> result = cartQueryService.getCartList();
//
//            // Then
//            assertThat(result).isEmpty();
//            verify(cartRepository, never()).findByUserWithItemsAndImages(any());
//        }
//    }
//}
