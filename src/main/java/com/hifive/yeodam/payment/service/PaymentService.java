package com.hifive.yeodam.payment.service;


import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.repository.OrderRepository;
import com.hifive.yeodam.payment.domain.Payment;
import com.hifive.yeodam.payment.dto.PaymentRequest;
import com.hifive.yeodam.payment.dto.PaymentRequestCallBack;
import com.hifive.yeodam.payment.repository.PaymentRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

        int totalPrice = order.getTotalPrice();
        Payment payment = Payment.create(totalPrice, order);

        paymentRepository.save(payment);

        return payment.getId();
    }

    public PaymentRequest findRequestPayment(Long paymentId) {

        Payment payment = paymentRepository.findByIdFetchJoin(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 결제정보가 없습니다"));

        return PaymentRequest.builder()
                .orderUid(payment.getOrder().getOrderUid())
                .username(payment.getOrder().getUser().getName())
                .phone(payment.getOrder().getUser().getAuth().getPhone())
                .email(payment.getOrder().getUser().getAuth().getEmail())
                .itemName(payment.getOrder().getItemName())
                .price(payment.getPrice())
                .build();
    }

    //응답받은 결제 검증 로직
    public IamportResponse<com.siot.IamportRestClient.response.Payment> paymentCallBack
    (PaymentRequestCallBack request) {

        //결제 단건 조회
        IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse = iamportClient
                .paymentByImpUid(request.getPaymentUid());


        //ORDER, Payment 조회
        Order order = orderRepository.findByOrderUid(request.getOrderUid())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 주문이 없습니다"));

        Payment payment = order.getPayment();

        //결제가 성공 검증
        validateIsSuccess(iamportResponse, payment, order);

        int orderTotalPrice = order.getTotalPrice();
        int iamportPrice = iamportResponse.getResponse().getAmount().intValue();

        //결제 위변조 체크
        validatePaymentPrice(orderTotalPrice, iamportPrice, payment, order, iamportResponse);

        payment.successPayment(iamportResponse.getResponse().getImpUid());
        order.successOrder();

        return iamportResponse;
    }

    public void paymentFail(PaymentRequestCallBack request) {

        Order order = orderRepository.findByOrderUid(request.getOrderUid())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 결제가 없습니다"));

        order.failOrder();

        log.info(request.toString());
        order.getPayment().failPayment(request.getPaymentUid());
    }

    private void validateIsSuccess(IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse, Payment payment, Order order) {
        if (!iamportResponse.getResponse().getStatus().equals("paid")) {
            deletePaymentOrder(payment, order);
            throw new RuntimeException("결제 미완료");
        }
    }

    public void deletePaymentOrder(Payment payment, Order order) {
        paymentRepository.deleteById(payment.getId());
        orderRepository.deleteById(order.getId());
    }

    private void validatePaymentPrice(int orderTotalPrice, int iamportPrice, Payment payment, Order order,
                                      IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse) {

        if (orderTotalPrice != iamportPrice) {
            deletePaymentOrder(payment, order);

            iamportClient.cancelPaymentByImpUid(
                    new CancelData(iamportResponse.getResponse().getImpUid(), true));

            throw new RuntimeException("결제 위변조가 의심됩니다.");
        }
    }
}
