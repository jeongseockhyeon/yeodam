package com.hifive.yeodam.payment.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;

    @Value("${imp.api-key}")
    private String apiKey;

    @Value("${imp.secret-key}")
    private String secretKey;

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
                .itemName(payment.getOrder().getItemSummary())
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

        //결제 성공 검증
        validateIsSuccess(iamportResponse, payment, order);

        int orderTotalPrice = order.getTotalPrice();
        int iamportPrice = iamportResponse.getResponse().getAmount().intValue();

        //결제 위변조 체크
        validateModulation(orderTotalPrice, iamportPrice, payment, order, iamportResponse);

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

    private String getToken() {

        String path = "https://api.iamport.kr/users/getToken";
        URI uri = URI.create(path);
        HttpClient httpClient = HttpClient.newHttpClient();

        String body = String.format("{\"imp_key\":\"%s\", \"imp_secret\":\"%s\"}", apiKey, secretKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", APPLICATION_JSON_VALUE)
                .header("Accept", "*/*")
                .POST(HttpRequest.BodyPublishers.ofString(body, UTF_8))
                .build();

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String resBody = response.body();
            JsonNode jsonNode = objectMapper.readTree(resBody);
            return jsonNode.get("response").get("access_token").asText();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    private void validateModulation(int orderTotalPrice, int iamportPrice, Payment payment, Order order,
                                    IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse) {

        if (orderTotalPrice != iamportPrice) {
            deletePaymentOrder(payment, order);

            iamportClient.cancelPaymentByImpUid(
                    new CancelData(iamportResponse.getResponse().getImpUid(), true));

            throw new RuntimeException("결제 위변조가 의심됩니다.");
        }
    }
}
