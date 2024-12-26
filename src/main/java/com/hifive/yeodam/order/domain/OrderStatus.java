package com.hifive.yeodam.order.domain;

public enum OrderStatus {
    PENDING, // 주문 생성만 한 상태
    COMPLETED, //주문 성공
    FAILED, //주문 실패
    CANCELED, //전체 취소
//    PARTIALLY_CANCELED //부분 취소
}
