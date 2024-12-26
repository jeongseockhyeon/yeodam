/*
package com.hifive.yeodam.order.service;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import com.hifive.yeodam.order.repository.OrderRepository;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static com.hifive.yeodam.order.dto.request.AddOrderRequest.orderRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    OrderQueryService orderService;

    AddOrderRequest addOrderRequest;
    Item item;

    @BeforeEach
    void setup() {

        //주문 dto 생성
        Long itemId = 1L;
        String itemName = "testItem";
        int count = 2;
        int price = 10_000;

        orderRequest orderRequest = new orderRequest();
        orderRequest.setItemId(itemId);
        orderRequest.setName(itemName);
        orderRequest.setCount(count);
        orderRequest.setPrice(price);

        addOrderRequest = new AddOrderRequest(List.of(orderRequest));

        //item 생성
        item = Tour.builder()
                .stock(1)
                .price(price)
                .itemName(itemName)
                .build();
    }

    @Test
    void 주문_생성_정상() {

        //given
        Principal principal = mock(Principal.class);
        User user = mock(User.class);

        when(principal.getName()).thenReturn("test@test.com");
        when(userRepository.findByEmail(principal.getName())).thenReturn(Optional.of(user));
        when(itemRepository.findById(addOrderRequest.getOrderRequests().getFirst().getItemId())).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        String orderUid = orderService.order(addOrderRequest, principal);

        //then
        assertThat(orderUid).isNotNull();
        verify(itemRepository, times(1)).findById(addOrderRequest.getOrderRequests().getLast().getItemId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void 주문_생성_회원없음_오류() {

    }
}*/
