import com.hifive.yeodam.cart.controller.CartApiController;
import com.hifive.yeodam.cart.dto.CartRequestDto;
import com.hifive.yeodam.cart.dto.CartResponseDto;
import com.hifive.yeodam.cart.dto.CartTotalPriceDto;
import com.hifive.yeodam.cart.dto.CartUpdateCountDto;
import com.hifive.yeodam.cart.entity.Cart;
import com.hifive.yeodam.cart.service.CartService;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.user.entity.User;
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

    @Test
    @DisplayName("장바구니 상품 추가 성공 테스트")
    void addCart_Success() {
        // given
        CartRequestDto requestDto = new CartRequestDto();
        requestDto.setItemId(1L);
        requestDto.setCount(2);

        // Create test Item
        Item testItem = new Item(1L, "Test Item", 1000) {
            @Override
            public void updateSubItem(String... args) {
                // Not needed for test
            }
        };
        testItem.setId(1L);

        // Create test User
        User testUser = new User();

        // Create test Cart
        Cart testCart = new Cart(testUser, testItem);
        testCart.setId(1L);
        testCart.setCount(2);

        CartResponseDto expectedResponse = new CartResponseDto(testCart);

        given(cartService.addCart(any(CartRequestDto.class)))
                .willReturn(expectedResponse);

        // when
        ResponseEntity<CartResponseDto> response = cartController.addCart(requestDto);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCartId()).isEqualTo(expectedResponse.getCartId());
        assertThat(response.getBody().getItemId()).isEqualTo(expectedResponse.getItemId());
        assertThat(response.getBody().getCount()).isEqualTo(expectedResponse.getCount());
        verify(cartService).addCart(requestDto);
    }

    @Test
    @DisplayName("장바구니 상품 수량 변경 성공 테스트")
    void updateCartCount_Success() {
        // given
        Long cartId = 1L;
        CartUpdateCountDto updateDto = new CartUpdateCountDto();
        updateDto.setCount(3);

        // Create test objects
        Item testItem = new Item(1L, "Test Item", 1000) {
            @Override
            public void updateSubItem(String... args) {
                // Not needed for test
            }
        };
        testItem.setId(1L);

        User testUser = new User();

        Cart testCart = new Cart(testUser, testItem);
        testCart.setId(cartId);
        testCart.setCount(3);

        CartResponseDto expectedResponse = new CartResponseDto(testCart);

        given(cartService.updateCartCount(eq(cartId), any(CartUpdateCountDto.class)))
                .willReturn(expectedResponse);

        // when
        ResponseEntity<CartResponseDto> response = cartController.updateCartCount(cartId, updateDto);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCount()).isEqualTo(updateDto.getCount());
        verify(cartService).updateCartCount(cartId, updateDto);
    }

    @Test
    @DisplayName("장바구니 상품 수량 변경 실패 테스트 - 예약 상품")
    void updateCartCount_Failure_Reserved() {
        // given
        Long cartId = 1L;
        CartUpdateCountDto updateDto = new CartUpdateCountDto();
        updateDto.setCount(3);

        given(cartService.updateCartCount(eq(cartId), any(CartUpdateCountDto.class)))
                .willThrow(new IllegalStateException("예약 상품은 수량 변경이 불가능합니다."));

        // when
        ResponseEntity<CartResponseDto> response = cartController.updateCartCount(cartId, updateDto);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("장바구니 상품 삭제 성공 테스트")
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

    @Test
    @DisplayName("장바구니 전체 가격 조회 테스트")
    void getTotalPrice_Success() {
        // given
        CartTotalPriceDto expectedDto = new CartTotalPriceDto(2000);
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
        CartTotalPriceDto expectedDto = new CartTotalPriceDto(3000);
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