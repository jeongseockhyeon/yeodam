//package com.hifive.yeodam.cartTest;
//
//import com.hifive.yeodam.auth.entity.Auth;
//import com.hifive.yeodam.auth.entity.RoleType;
//import com.hifive.yeodam.cart.dto.command.CartRequestDto;
//import com.hifive.yeodam.cart.dto.command.CartUpdateCountDto;
//import com.hifive.yeodam.cart.dto.command.LocalStorageCartDto;
//import com.hifive.yeodam.cart.dto.query.CartResponseDto;
//import com.hifive.yeodam.cart.entity.Cart;
//import com.hifive.yeodam.cart.repository.CartRepository;
//import com.hifive.yeodam.cart.service.CartCommandService;
//import com.hifive.yeodam.global.exception.CustomErrorCode;
//import com.hifive.yeodam.global.exception.CustomException;
//import com.hifive.yeodam.item.entity.Item;
//import com.hifive.yeodam.item.repository.ItemRepository;
//import com.hifive.yeodam.tour.entity.Tour;
//import com.hifive.yeodam.user.entity.User;
//import com.hifive.yeodam.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.*;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.Assert.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class CartCommandServiceTest {
//    @InjectMocks
//    private CartCommandService cartCommandService;
//
//    @Mock
//    private CartRepository cartRepository;
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private ItemRepository itemRepository;
//    @Mock
//    private SecurityContext securityContext;
//    @Mock
//    private Authentication authentication;
//
//    private User testUser;
//    private Auth testAuth;
//    private Cart testCart;
//    private Tour testTour;
//
//    @BeforeEach
//    void setUp() {
//        // Auth 설정
//        testAuth = Auth.builder()
//                .id(1L)
//                .email("test@test.com")
//                .password("password")
//                .role(RoleType.USER)
//                .build();
//
//        // User 설정
//        testUser = User.builder()
//                .id(1L)
//                .name("Test User")
//                .nickname("Tester")
//                .phone("01012345678")
//                .auth(testAuth)
//                .build();
//
//        // Security Context 설정
//        SecurityContextHolder.setContext(securityContext);
//        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
//        lenient().when(authentication.getName()).thenReturn("test@test.com");
//        lenient().when(authentication.getAuthorities()).thenAnswer(invocation ->
//                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
//        );
//    }
//
//    @Nested
//    @DisplayName("Tour 상품 장바구니")
//    class TourCartTest {
//        @BeforeEach
//        void setUp() {
//            testTour = mock(Tour.class);
//            // Tour mock 설정 - 중복 체크에 필요한 것만
//            lenient().when(testTour.getId()).thenReturn(2L);
//            lenient().when(testTour.isReservation()).thenReturn(true);
//
//            // Cart 설정
//            testCart = Cart.builder()
//                    .user(testUser)
//                    .item(testTour)
//                    .build();
//            ReflectionTestUtils.setField(testCart, "id", 2L);
//        }
//
//        @Test
//        @DisplayName("Tour 상품 장바구니 추가")
//        void addTourCart() {
//            // Tour mock 설정
//            lenient().when(testTour.getId()).thenReturn(2L);
//            lenient().when(testTour.getItemName()).thenReturn("Test Tour");
//            lenient().when(testTour.getPrice()).thenReturn(50000);
//            lenient().when(testTour.isReservation()).thenReturn(true);
//            lenient().when(testTour.getDescription()).thenReturn("Tour 설명");
//
//            // Given
//            CartRequestDto requestDto = CartRequestDto.builder()
//                    .itemId(2L)
//                    .itemName("Test Tour")
//                    .description("Tour Description")
//                    .period("3박 4일")
//                    .region("Test Region")
//                    .price(50000)
//                    .build();
//
//            lenient().when(userRepository.findByEmail(any())).thenReturn(Optional.of(testUser));
//            lenient().when(itemRepository.findById(any())).thenReturn(Optional.of(testTour));
//            lenient().when(cartRepository.findByUserAndItem(any(), any())).thenReturn(Optional.empty());
//            lenient().when(cartRepository.countByUser(any())).thenReturn(0);
//
//
//            Cart tourCart = Cart.builder()
//                    .user(testUser)
//                    .item(testTour)
//                    .build();
//            ReflectionTestUtils.setField(tourCart, "id", 2L);
//
//            ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
//            when(cartRepository.save(cartCaptor.capture())).thenReturn(tourCart);
//
//            // when
//            CartResponseDto responseDto = cartCommandService.addCart(requestDto);
//
//            // then
//            assertThat(responseDto).isNotNull();
//            assertThat(responseDto.getItemId()).isEqualTo(2L);
//            assertThat(responseDto.getItemName()).isEqualTo("Test Tour");
//            assertThat(responseDto.getPrice()).isEqualTo(50000);
//            assertThat(responseDto.isReservation()).isTrue();
//            assertThat(responseDto.isCountModifiable()).isFalse();
//            assertThat(responseDto.getTourItem()).isNotNull();
//
//            Cart savedCart = cartCaptor.getValue();
//            assertThat(savedCart.getUser()).isEqualTo(testUser);
//            assertThat(savedCart.getItem()).isEqualTo(testTour);
//
//            verify(cartRepository).save(any(Cart.class));
//        }
//
//        @Test
//        @DisplayName("Tour 상품 중복 추가 시 예외 발생")
//        void addDuplicateTour() {
//            // Given
//            CartRequestDto requestDto = CartRequestDto.builder()
//                    .itemId(2L)
//                    .itemName("Test Tour")
//                    .description("Tour 설명")
//                    .period("3박 4일")
//                    .region("Test Region")
//                    .price(50000)
//                    .build();
//
//            lenient().when(userRepository.findByEmail(any())).thenReturn(Optional.of(testUser));
//            lenient().when(itemRepository.findById(any())).thenReturn(Optional.of(testTour));
//            lenient().when(cartRepository.findByUserAndItem(any(), any())).thenReturn(Optional.of(testCart));
//
//            // when & then
//            CustomException exception = assertThrows(CustomException.class,
//                    () -> cartCommandService.addCart(requestDto));
//
//            assertThat(exception.getCustomErrorCode()).isEqualTo(CustomErrorCode.CART_ITEM_DUPLICATE);
//            verify(cartRepository, never()).save(any());
//        }
//    }
//
//    @Nested
//    @DisplayName("장바구니 상품 수량 업데이트")
//    class UpdateCartCountTest {
//        @Test
//        @DisplayName("일반 상품 수량 업데이트")
//        void updateCartCount() {
//            //Given
//            Item item = mock(Item.class);
//            lenient().when(item.getId()).thenReturn(1L);
//            lenient().when(item.isReservation()).thenReturn(false);
//
//            Cart cart = Cart.builder()
//                    .user(testUser)
//                    .item(item)
//                    .build();
//            ReflectionTestUtils.setField(cart, "id", 1L);
//
//            CartUpdateCountDto updateCountDto = new CartUpdateCountDto(5, false);
//
//            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
//
//            // When
//            CartResponseDto responseDto = cartCommandService.updateCartCount(1L, updateCountDto);
//
//            // Then
//            assertThat(responseDto).isNotNull();
//            assertThat(responseDto.getCount()).isEqualTo(5);
//            assertThat(responseDto.isCountModifiable()).isTrue();
//        }
//
//        @Test
//        @DisplayName("Tour 상품 수량 변경 - 예외 발생")
//        void updateTourCount() {
//            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                    () -> CartUpdateCountDto.builder()
//                            .count(2)
//                            .reservation(true)  // Tour 상품
//                            .build());
//
//            assertThat(exception.getMessage())
//                    .isEqualTo("예약 상품은 수량 변경이 불가능 합니다.");
//        }
//
//        @Test
//        @DisplayName("수량이 1 미만이면 예외 발생")
//        void validateItemCount() {
//            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                    () -> CartUpdateCountDto.builder()
//                            .count(0)
//                            .reservation(false)
//                            .build());
//
//            assertThat(exception.getMessage())
//                    .isEqualTo("수량은 1개 이상이어야 합니다.");
//        }
//    }
//
//    @Nested
//    @DisplayName("장바구니 상품 삭제 테스트")
//    class RemoveCartTest {
//        @Test
//        @DisplayName("장바구니 상품 삭제")
//        void removeCart() {
//            // Given
//            Cart cart = Cart.builder()
//                    .user(testUser)
//                    .item(testTour)
//                    .build();
//            ReflectionTestUtils.setField(cart, "id", 1L);
//
//            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
//
//            // When
//            cartCommandService.removeCart(1L);
//
//            // Then
//            verify(cartRepository).delete(cart);
//        }
//
//        @Test
//        @DisplayName("존재하지 않는 장바구니 상품 삭제 - 예외 발생")
//        void removeNonExistentCart() {
//            /// Given
//            when(cartRepository.findById(1L)).thenReturn(Optional.empty());
//
//            // When & Then
//            CustomException exception = assertThrows(CustomException.class,
//                    () -> cartCommandService.removeCart(1L));
//            assertThat(exception.getCustomErrorCode()).isEqualTo(CustomErrorCode.CART_NOT_FOUND);
//        }
//    }
//
//    @Nested
//    @DisplayName("장바구니 최대 개수")
//    class MaxCartSlotsTest {
//        @Test
//        @DisplayName("장바구니 최대 개수 반환")
//        void getMaxCartSlots() {
//            // When
//            int maxItems = CartCommandService.getMaxCartItems();
//
//            // Then
//            assertThat(maxItems).isEqualTo(20);
//        }
//    }
//
//    @Nested
//    @DisplayName("로컬 장바구니 동기화")
//    class SyncCartWithLocalTest {
//        @Test
//        @DisplayName("일반 상품 동기화")
//        void syncNormalWithLocal() {
//            // Given
//            Item normalItem = mock(Item.class);
//            lenient().when(normalItem.getId()).thenReturn(1L);
//            lenient().when(normalItem.getItemName()).thenReturn("Normal Item");
//            lenient().when(normalItem.getPrice()).thenReturn(10000);
//            lenient().when(normalItem.isReservation()).thenReturn(false);
//
//            LocalStorageCartDto localItem = LocalStorageCartDto.builder()
//                    .itemId(1L)
//                    .count(2)
//                    .reservation(false)
//                    .build();
//
//            List<LocalStorageCartDto> localCart = List.of(localItem);
//
//            when(userRepository.findByEmail(any())).thenReturn(Optional.of(testUser));
//            when(cartRepository.countByUser(any())).thenReturn(0);
//            when(itemRepository.findById(1L)).thenReturn(Optional.of(normalItem));
//            when(cartRepository.findByUserAndItem(any(), any())).thenReturn(Optional.empty());
//
//            // When
//            cartCommandService.syncCartWithLocal(localCart);
//
//            // Then
//            verify(cartRepository).save(any(Cart.class));
//        }
//
//        @Test
//        @DisplayName("장바구니 최대 수량 - 동기화 실패")
//        void syncFullCartWithLocal() {
//            // Given
//            List<LocalStorageCartDto> localCart = List.of(
//                    LocalStorageCartDto.builder()
//                            .itemId(1L)
//                            .count(1)
//                            .reservation(false)
//                            .build()
//            );
//
//            when(userRepository.findByEmail(any())).thenReturn(Optional.of(testUser));
//            when(cartRepository.countByUser(any())).thenReturn(20);
//
//            // When & Then
//            CustomException exception = assertThrows(CustomException.class,
//                    () -> cartCommandService.syncCartWithLocal(localCart));
//            assertThat(exception.getCustomErrorCode()).isEqualTo(CustomErrorCode.CART_FULL);
//        }
//    }
//}
