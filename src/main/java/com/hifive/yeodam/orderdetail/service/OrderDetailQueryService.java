package com.hifive.yeodam.orderdetail.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.order.dto.response.OrderDetailsResponse;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.orderdetail.domain.OrderDetailStatus;
import com.hifive.yeodam.orderdetail.repository.OrderDetailRepository;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.function.Function;

import static com.hifive.yeodam.global.exception.CustomErrorCode.USER_NOT_FOUND;
import static com.hifive.yeodam.order.domain.OrderStatus.FAILED;
import static com.hifive.yeodam.orderdetail.domain.OrderDetailStatus.*;

@Service
@RequiredArgsConstructor
public class OrderDetailQueryService {

    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Transactional(readOnly = true)
    public OrderDetailsResponse findOrderDetails(int beforeLimit, int afterLimit, Principal principal) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Pageable beforePageable = Pageable.ofSize(beforeLimit);
        Pageable afterPageable = Pageable.ofSize(afterLimit);

        SliceImpl<OrderDetailsResponse.BeforeOrderResponse> beforeOrderResponse =
                getOrderResponses(user, beforePageable, List.of(PENDING), OrderDetailsResponse.BeforeOrderResponse::new);

        SliceImpl<OrderDetailsResponse.AfterOrderResponse> afterOrderResponse =
                getOrderResponses(user, afterPageable, List.of(USED, CANCELED), OrderDetailsResponse.AfterOrderResponse::new);

        return new OrderDetailsResponse(beforeOrderResponse, afterOrderResponse);
    }

    private <T> SliceImpl<T> getOrderResponses(User user, Pageable pageable, List<OrderDetailStatus> statuses,
            Function<OrderDetail, T> responseMapper) {

        Slice<OrderDetail> page = orderDetailRepository.findOrderByDetailStatus(statuses, FAILED, user, pageable);

        List<T> responses = page.stream()
                .map(responseMapper)
                .toList();

        return new SliceImpl<>(responses, pageable, page.hasNext());
    }

}
