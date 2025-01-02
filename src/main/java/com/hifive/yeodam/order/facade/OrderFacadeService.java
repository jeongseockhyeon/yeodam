package com.hifive.yeodam.order.facade;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.order.dto.request.AddOrderRequest;
import com.hifive.yeodam.order.dto.request.CancelOrderRequest;
import com.hifive.yeodam.order.dto.response.CancelOrderResponse;
import com.hifive.yeodam.order.dto.response.CreateOrderPaymentResponse;
import com.hifive.yeodam.order.service.OrderCommandService;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.orderdetail.service.OrderDetailCommandService;
import com.hifive.yeodam.payment.domain.Payment;
import com.hifive.yeodam.payment.service.PaymentService;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

import static com.hifive.yeodam.global.exception.CustomErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderFacadeService {

    private final OrderCommandService orderService;
    private final OrderDetailCommandService orderDetailService;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @Transactional
    public CreateOrderPaymentResponse createOrderPayment(AddOrderRequest request, Principal principal) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        List<OrderDetail> details = orderDetailService.createDetails(request);

        String orderUid = orderService.order(user, details);

        Long paymentId = paymentService.createPayment(orderUid);
        Payment payment = paymentService.findRequestPayment(paymentId);

        return CreateOrderPaymentResponse.builder()
                .orderUid(orderUid)
                .username(user.getName())
                .phone(user.getPhone())
                .email(user.getAuth().getEmail())
                .itemName(payment.getOrder().getItemSummary())
                .price(payment.getPrice())
                .build();
    }

    @Transactional
    public void cancelOrderPayment(CancelOrderRequest request) {
        CancelOrderResponse response = orderService.cancelOrder(request);
        paymentService.cancel(response.getOrderUid(), response.getTotalPrice());
    }
}
