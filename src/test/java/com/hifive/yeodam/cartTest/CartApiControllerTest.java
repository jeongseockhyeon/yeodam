import com.hifive.yeodam.cart.controller.CartApiController;
import com.hifive.yeodam.cart.dto.CartRequestDto;
import com.hifive.yeodam.cart.dto.CartResponseDto;
import com.hifive.yeodam.cart.dto.CartTotalPriceDto;
import com.hifive.yeodam.cart.dto.CartUpdateCountDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.service.CartService;
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
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartApiControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartApiController cartController;

    private CartRequestDto requestDto;
    private CartResponseDto responseDto;
    private CartUpdateCountDto updateCountDto;
    private Long cartId;
    private Tour testItem;
    private Cart cart;

    @BeforeEach
    void setUp() {

        cartId = 1L;

        requestDto = new CartRequestDto(1L, 2);

        testItem = Tour.builder()
                .sellerId(1L)
                .itemName("제주도 여행")
                .region("제주도")
                .period("3박 4일")
                .description("제주도 여행 상품")
                .price(100000)
                .build();
        testItem.setId(1L);
        testItem.setReservation(true);

        cart = new Cart(null, testItem);
        cart.setId(cartId);
        cart.setCount(2);

        responseDto = new CartResponseDto(cart);

        updateCountDto = new CartUpdateCountDto();
        updateCountDto.setCount(3);
    }

    @Test
    @DisplayName("장바구니 상품 추가 성공 테스트")
    void addCart_Success() {
        // given
        given(cartService.addCart(any(CartRequestDto.class)))
                .willReturn(responseDto);

        //when
        ResponseEntity<CartResponseDto> response = cartController.addCart(requestDto);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCartId()).isEqualTo(responseDto.getCartId());
        assertThat(response.getBody().getItemId()).isEqualTo(responseDto.getItemId());
        assertThat(response.getBody().getCount()).isEqualTo(responseDto.getCount());
        assertThat(response.getBody().isCountModifiable()).isFalse();
        verify(cartService).addCart(requestDto);
    }


    @Test
    @DisplayName("장바구니 예약상품 수량 변경 실패 테스트")
    void updateCartCount_Fail() {
        // given
        Long cartId = 1L;
        CartUpdateCountDto updateCountDto = new CartUpdateCountDto();
        updateCountDto.setCount(2);

        given(cartService.updateCartCount(eq(cartId), any(CartUpdateCountDto.class)))
                .willThrow(new IllegalStateException("예약 상품은 수량 변경이 불가능합니다."));

        //when & then
        assertThrows(IllegalStateException.class,
                () -> cartController.updateCartCount(cartId, updateCountDto));

        verify(cartService).updateCartCount(cartId, updateCountDto);
    }

    @Test
    @DisplayName("장바구니 상품 삭제 성공 테스트")
    void removeCart_Success() {
        // given
        doNothing().when(cartService).removeCart(cartId);

        // when
        ResponseEntity<Void> response = cartController.removeCart(cartId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(cartService).removeCart(cartId);
    }

    @Test
    @DisplayName("장바구니 전체 가격 조회 테스트")
    void getTotalPrice_Success() {
        // given
        CartTotalPriceDto expectedDto = new CartTotalPriceDto(200000);
        given(cartService.getTotalPrice()).willReturn(expectedDto);

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
    void getSelectedPrice_Success() {
        // given
        List<Long> cartIds = Arrays.asList(1L, 2L);
        CartTotalPriceDto expectedDto = new CartTotalPriceDto(400000); //예약 상품 2개
        given(cartService.getSelectedPrice(cartIds)).willReturn(expectedDto);

        // when
        ResponseEntity<CartTotalPriceDto> response = cartController.getSelectedPrice(cartIds);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalPrice()).isEqualTo(expectedDto.getTotalPrice());
        verify(cartService).getSelectedPrice(cartIds);
    }
}