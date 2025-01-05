package com.hifive.yeodam.orderdetail.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.order.dto.response.OrderDetailBySellerResponse;
import com.hifive.yeodam.order.dto.response.OrderDetailsResponse;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.orderdetail.domain.OrderDetailStatus;
import com.hifive.yeodam.orderdetail.repository.OrderDetailRepository;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.SellerRepository;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.function.Function;

import static com.hifive.yeodam.global.exception.CustomErrorCode.SELLER_NOT_FOUND;
import static com.hifive.yeodam.global.exception.CustomErrorCode.USER_NOT_FOUND;
import static com.hifive.yeodam.order.domain.OrderStatus.FAILED;
import static com.hifive.yeodam.order.dto.response.OrderDetailsResponse.AfterOrderResponse;
import static com.hifive.yeodam.order.dto.response.OrderDetailsResponse.BeforeOrderResponse;
import static com.hifive.yeodam.orderdetail.domain.OrderDetailStatus.*;

@Service
@RequiredArgsConstructor
public class OrderDetailQueryService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Transactional(readOnly = true)
    public OrderDetailsResponse findOrderDetails(int beforeLimit, int afterLimit, Principal principal) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Pageable beforePageable = Pageable.ofSize(beforeLimit);
        Pageable afterPageable = Pageable.ofSize(afterLimit);

        //이용전 내역 찾아옴
        SliceImpl<BeforeOrderResponse> beforeOrderResponse =
                getOrderResponses(user, beforePageable, List.of(PENDING), BeforeOrderResponse::new);

        //이용후, 취소 내역 찾아옴
        SliceImpl<AfterOrderResponse> afterOrderResponse =
                getOrderResponses(user, afterPageable, List.of(USED, CANCELED), AfterOrderResponse::new);

        return new OrderDetailsResponse(beforeOrderResponse, afterOrderResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderDetailBySellerResponse> findOrderDetailsBySeller(Principal principal, Long itemId, int offset, int limit) {

        Seller seller = sellerRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(SELLER_NOT_FOUND));

        Page<OrderDetail> orderDetails = orderDetailRepository
                .findAllBySeller(OrderDetailStatus.FAILED, seller.getCompanyId(), itemId, PageRequest.of(offset, limit));

        return orderDetails.map(od -> new OrderDetailBySellerResponse(
                od.getOrder().getOrderUid(),
                itemId,
                od.getReservation().getStartDate(),
                od.getReservation().getEndDate(),
                od.getStatus()));
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
