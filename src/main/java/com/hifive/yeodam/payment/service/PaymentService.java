package com.hifive.yeodam.payment.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.domain.OrderStatus;
import com.hifive.yeodam.order.repository.OrderRepository;
import com.hifive.yeodam.orderdetail.domain.OrderDetailStatus;
import com.hifive.yeodam.payment.domain.Payment;
import com.hifive.yeodam.payment.domain.PaymentStatus;
import com.hifive.yeodam.payment.dto.PaymentOrderUidResponse;
import com.hifive.yeodam.payment.dto.PaymentRequestCallBack;
import com.hifive.yeodam.payment.repository.PaymentRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.hifive.yeodam.global.constant.PaymentConst.*;
import static com.hifive.yeodam.global.exception.CustomErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;

    @Transactional
    public Long createPayment(String orderUid) {

        Order order = findOrderByUid(orderUid);
        Payment payment = Payment.create(order.getTotalPrice(), order);
        paymentRepository.save(payment);
        return payment.getId();
    }

    @Transactional(readOnly = true)
    public Payment findRequestPayment(Long paymentId) {
        return paymentRepository.findByIdFetchJoin(paymentId)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));
    }

    @Transactional
    public PaymentOrderUidResponse validatePayment(PaymentRequestCallBack request) {

        IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse = fetchPaymentDetails(request.getPaymentUid());
        Order order = findOrderByUid(request.getOrderUid());
        Payment payment = order.getPayment();

        if (iamportResponse.getResponse().getStatus().equals(PAID)) {
            if (isPriceValid(order.getTotalPrice(), iamportResponse)) {

                payment.successPayment(iamportResponse.getResponse().getImpUid(), iamportResponse.getResponse().getCardName());
                order.chanceOrderStatus(OrderStatus.COMPLETED);

                return PaymentOrderUidResponse.builder()
                        .orderUid(order.getOrderUid())
                        .build();
            }
        }
        payment.paymentFail(iamportResponse.getResponse().getImpUid());

        throw new CustomException(PAYMENT_FAILED);
    }

    @Transactional
    public void paymentFail(PaymentRequestCallBack request) {

        Order order = findOrderByUid(request.getOrderUid());
        order.chanceOrderStatus(OrderStatus.FAILED);

        order.getOrderDetails()
                .forEach(od -> {
                    od.changeStatus(OrderDetailStatus.FAILED);
                    od.deleteReservation();
                });

        order.getPayment().paymentFail(request.getPaymentUid());
    }

    @Transactional
    public void cancel(String orderUid, int totalPrice) {

        Payment payment = paymentRepository.findByOrderUid(orderUid)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));

        if (totalPrice == HAS_NO_TOTAL_PRICE) {
            throw new CustomException(PAYMENT_CANCELED);
        }
        if (payment.getPaymentUid().equals(PAYMENT_UID_BEFORE_PAYMENT)) {
            throw new CustomException(PAYMENT_CANCELED);
        }

        cancelPayment(payment.getPaymentUid(), totalPrice);
        payment.cancel();
    }

    @Transactional
    public void checkPaymentStatus(String orderUid) {

        Order order = findOrderByUid(orderUid);
        Payment payment = order.getPayment();

        if (payment.getStatus().equals(PaymentStatus.PENDING)) {
            order.getPayment().paymentFail(PAYMENT_UID_BEFORE_PAYMENT);
        }
    }

    private IamportResponse<com.siot.IamportRestClient.response.Payment> fetchPaymentDetails(String paymentUid) {

        try {
            return iamportClient.paymentByImpUid(paymentUid);
        } catch (Exception e) {
            throw new CustomException(I_AM_PORT_ERROR);
        }
    }

    private boolean isPriceValid(int orderTotalPrice,
                                 IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse) {

        int iamportPrice = iamportResponse.getResponse().getAmount().intValue();

        if (iamportPrice == orderTotalPrice) {
            return true;
        }
        cancelPayment(iamportResponse.getResponse().getImpUid(), iamportPrice);

        return false;
    }

    private void cancelPayment(String paymentUid, int cancelPrice) {

        IamportResponse<com.siot.IamportRestClient.response.Payment> response =
                iamportClient.cancelPaymentByImpUid(new CancelData(paymentUid, true, new BigDecimal(cancelPrice)));

        if (response.getCode() == I_AM_PORT_STATUS_FAIL_CODE) {
            throw new CustomException(PAYMENT_CANCELED);
        }
    }

    private Order findOrderByUid(String orderUid) {
        return orderRepository.findByOrderUid(orderUid)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));
    }
}