package com.hifive.yeodam.payment.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.domain.OrderStatus;
import com.hifive.yeodam.order.repository.OrderRepository;
import com.hifive.yeodam.payment.domain.Payment;
import com.hifive.yeodam.payment.domain.PaymentStatus;
import com.hifive.yeodam.payment.dto.CancelPaymentRequest;
import com.hifive.yeodam.payment.dto.PaymentRequestCallBack;
import com.hifive.yeodam.payment.dto.PaymentResponse;
import com.hifive.yeodam.payment.repository.PaymentRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.hifive.yeodam.global.constant.PaymentConst.PAYMENT_UID_BEFORE_PAYMENT;
import static com.hifive.yeodam.global.exception.CustomErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    public final String PAID = "paid";

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
    public PaymentResponse findRequestPayment(Long paymentId) {
        Payment payment = paymentRepository.findByIdFetchJoin(paymentId)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));

        return buildPaymentResponse(payment);
    }

    @Transactional
    public String validatePayment(PaymentRequestCallBack request) {
        IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse = fetchPaymentDetails(request.getPaymentUid());
        Order order = findOrderByUid(request.getOrderUid());
        Payment payment = order.getPayment();

        if (isPaymentSuccessful(iamportResponse)) {
            if (isPriceValid(order.getTotalPrice(), iamportResponse)) {
                payment.successPayment(iamportResponse.getResponse().getImpUid(), iamportResponse.getResponse().getCardName());
                order.chanceOrderStatus(OrderStatus.COMPLETED);
                return order.getOrderUid();
            }
        }

        payment.paymentFail(iamportResponse.getResponse().getImpUid());
        throw new CustomException(PAYMENT_FAILED);
    }

    @Transactional
    public void paymentFail(PaymentRequestCallBack request) {
        Order order = findOrderByUid(request.getOrderUid());
        order.chanceOrderStatus(OrderStatus.FAILED);
        order.getOrderDetails().forEach(od -> od.getItem().addStock());
        order.getPayment().paymentFail(request.getPaymentUid());
    }

    @Transactional
    public void cancel(CancelPaymentRequest request) {
        Payment payment = paymentRepository.findByOrderUid(request.getOrderUid())
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));

        if (request.getTotalPrice() == 0) {
            throw new CustomException(PAYMENT_CANCELED);
        }
        if (payment.getPaymentUid().equals(PAYMENT_UID_BEFORE_PAYMENT)) {
            throw new CustomException(PAYMENT_CANCELED);
        }

        IamportResponse<com.siot.IamportRestClient.response.Payment> response = iamportClient.cancelPaymentByImpUid(new CancelData(payment.getPaymentUid(), true, new BigDecimal(request.getTotalPrice())));

        if (response.getCode() == -1) {
            throw new CustomException(I_AM_PORT_ERROR);
        }

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

    private boolean isPaymentSuccessful(IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse) {
        return PAID.equals(iamportResponse.getResponse().getStatus());
    }

    private boolean isPriceValid(int orderTotalPrice, IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse) {
        int iamportPrice = iamportResponse.getResponse().getAmount().intValue();
        if (orderTotalPrice == iamportPrice) {
            return true;
        }
        cancelPayment(iamportResponse.getResponse().getImpUid());
        return false;
    }

    private void cancelPayment(String paymentUid) {
        iamportClient.cancelPaymentByImpUid(new CancelData(paymentUid, true));
    }

    private Order findOrderByUid(String orderUid) {
        return orderRepository.findByOrderUid(orderUid)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));
    }

    private PaymentResponse buildPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .orderUid(payment.getOrder().getOrderUid())
                .username(payment.getOrder().getUser().getName())
                .phone(payment.getOrder().getUser().getPhone())
                .email(payment.getOrder().getUser().getAuth().getEmail())
                .itemName(payment.getOrder().getItemSummary())
                .price(payment.getPrice())
                .build();
    }

    /*@Deprecated
    private String getToken() {
        String path = "https://api.iamport.kr/users/getToken";
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = buildTokenRequest(path);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return parseTokenFromResponse(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("토큰 생성 실패", e);
        }
    }

    private HttpRequest buildTokenRequest(String path) {
        String body = String.format("{\"imp_key\":\"%s\", \"imp_secret\":\"%s\"}", apiKey, secretKey);
        return HttpRequest.newBuilder()
                .uri(URI.create(path))
                .header("Content-Type", APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(body, UTF_8))
                .build();
    }

    private String parseTokenFromResponse(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("response").get("access_token").asText();
    }*/
}
