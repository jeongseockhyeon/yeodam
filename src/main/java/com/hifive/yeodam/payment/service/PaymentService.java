package com.hifive.yeodam.payment.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.order.domain.Order;
import com.hifive.yeodam.order.repository.OrderRepository;
import com.hifive.yeodam.payment.domain.Payment;
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
import org.springframework.util.StringUtils;

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
                payment.successPayment(iamportResponse.getResponse().getImpUid());
                order.successOrder();
                return order.getOrderUid();
            }
        }

        handlePaymentFailure(order, payment, iamportResponse.getResponse().getImpUid());
        throw new CustomException(PAYMENT_FAILED);
    }

    @Transactional
    public void paymentFail(PaymentRequestCallBack request) {
        Order order = findOrderByUid(request.getOrderUid());
        handlePaymentFailure(order, order.getPayment(), request.getPaymentUid());
    }

    @Transactional
    public void cancel(String orderUid) {

        Order order = findOrderByUid(orderUid);
        Payment payment = order.getPayment();

        if (isExistPaymentUid(payment)) {
            handlePaymentCancel(order, payment);
            cancelPayment(payment.getPaymentUid());
            return;
        }

        throw new CustomException(PAYMENT_CANCELED);
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

    private void handlePaymentFailure(Order order, Payment payment, String paymentUid) {
        order.failOrder();
        payment.paymentFail(paymentUid);
    }

    private void handlePaymentCancel(Order order, Payment payment) {
        order.cancelOrder();
        payment.cancel();
    }

    private void cancelPayment(String paymentUid) {
        iamportClient.cancelPaymentByImpUid(new CancelData(paymentUid, true));
    }

    private boolean isExistPaymentUid(Payment payment) {
        return StringUtils.hasText(payment.getPaymentUid());
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
