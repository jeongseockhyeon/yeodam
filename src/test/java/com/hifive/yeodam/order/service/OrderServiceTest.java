package com.hifive.yeodam.order.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.dto.AddOrderRequest;
import com.hifive.yeodam.order.repository.OrderRepository;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static com.hifive.yeodam.order.dto.AddOrderRequest.ItemRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderService orderService;

    Item item;
    AddOrderRequest request;


    @BeforeEach
    void setup() {
        item = createItem();
        request = createRequest();
    }


    @Test
    void 결제_생성_정상() {

        //given
        TestPrincipal principal = new TestPrincipal("123@test.com");

        Order order = Mockito.mock(Order.class);
        User user = Mockito.mock(User.class);

        when(userRepository.findByEmail(principal.getName())).thenReturn(Optional.of(user));
        when(itemRepository.findById(request.getItems().getFirst().getId())).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        //when
        String orderUid = orderService.order(request, principal);

        //then
        assertThat(orderUid).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void 생성_실패_회원없음() {

        //given
        TestPrincipal principal = new TestPrincipal("123@test.com");
        when(userRepository.findByEmail(principal.getName())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> orderService.order(request, principal))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 생성_실패_아이템없음() {

        //given
        TestPrincipal principal = new TestPrincipal("123@test.com");
        User user = Mockito.mock(User.class);
        when(userRepository.findByEmail(principal.getName())).thenReturn(Optional.of(user));
        when(itemRepository.findById(request.getItems().getFirst().getId())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> orderService.order(request, principal))
                .isInstanceOf(CustomException.class);
    }

    private Item createItem() {
        return Tour.builder()
                .itemName("여행가고 싶다")
                .stock(1)
                .price(10_000)
                .build();
    }

    private AddOrderRequest createRequest() {
        request = new AddOrderRequest();
        request.setPhoneNumber("1234-1234");
        request.setOrderMessage("ㅎㅇㅎㅇ");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setName("떠나요 둘이서");
        itemRequest.setCount(2);
        itemRequest.setPrice(10_000);

        request.setItems(List.of(itemRequest));
        return request;
    }

    @AllArgsConstructor
    static class TestPrincipal implements Principal {

        private String name;

        @Override
        public String getName() {
            return this.name;
        }
    }
}