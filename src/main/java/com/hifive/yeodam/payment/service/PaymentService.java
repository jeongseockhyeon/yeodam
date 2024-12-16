package com.hifive.yeodam.payment.service;


import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.repository.OrderRepository;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.payment.domain.Payment;
import com.hifive.yeodam.payment.dto.PaymentRequest;
import com.hifive.yeodam.payment.dto.PaymentRequestCallBack;
import com.hifive.yeodam.payment.repository.PaymentRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.hifive.yeodam.payment.domain.PaymentStatus.COMPLETED;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;

    public Long createPayment(String orderUid) {
        Order order = orderRepository.findByOrderUid(orderUid)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 주문이 없습니다"));

        int totalPrice = getTotalPrice(order);
        Payment payment = Payment.create(totalPrice, order);

        paymentRepository.save(payment);

        return payment.getId();
    }

    public PaymentRequest findRequestPayment(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 결제정보가 없습니다"));

        return PaymentRequest.builder()
                .itemName(getItemName(payment.getOrder()))
                .price(payment.getPrice())
                .orderUid(payment.getOrder().getOrderUid())
                .build();
    }

    //TODO
    public void paymentCallBack(PaymentRequestCallBack request) {

        //결제 단건 조회
        IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse = iamportClient
                .paymentByImpUid(request.getPaymentUid());

        Order order = orderRepository.findByOrderUid(request.getOrderUid())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 주문이 없습니다"));

        Payment payment = paymentRepository.findByOrderId(order.getId());

        //결제가 안된 경우 데이터 삭제
        if (!iamportResponse.getResponse().getStatus().equals("paid")) {
            paymentRepository.deleteById(payment.getId());
            orderRepository.deleteById(order.getId());

            throw new RuntimeException("결제 미완료");
        }

        int orderTotalPrice = order.getOrderDetails().stream()
                .mapToInt(OrderDetail::getTotalPrice)
                .sum();

        int iamportPrice = iamportResponse.getResponse().getAmount().intValue();

        //결제 위변조 체크
        if (orderTotalPrice != iamportPrice) {
            paymentRepository.deleteById(payment.getId());
            orderRepository.deleteById(order.getId());

            iamportClient.cancelPaymentByImpUid(
                    new CancelData(iamportResponse.getResponse().getImpUid(), true));

            throw new RuntimeException("결제 위변조");
        }

        payment.changePaymentBySuccess(COMPLETED, iamportResponse.getResponse().getImpUid());
        order.successOrder();
    }

    private int getTotalPrice(Order order) {
        return order.getOrderDetails().stream()
                .mapToInt(OrderDetail::getTotalPrice)
                .sum();
    }

    private String getItemName(Order order) {
        StringBuilder sb = new StringBuilder();
        List<OrderDetail> orderDetails = order.getOrderDetails();

        int count = orderDetails.size();
        if (count > 1) {

            sb.append(orderDetails.getFirst().getItem())
                    .append(" 외 ")
                    .append(count)
                    .append("건");

            return sb.toString();
        }

        return sb.append(orderDetails.getFirst().getItem())
                .toString();
    }
}
