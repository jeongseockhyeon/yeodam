package com.hifive.yeodam.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {


    /**
     * 404 NOT_FOUND
     * 요청 리소스가 없음
     */
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리가 존재하지 않습니다"),
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품이 존재하지 않습니다"),
    GUIDE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 가이드가 존재하지 않습니다"),

    // Wish 관련 ERROR
    WISH_NOT_FOUND(HttpStatus.NOT_FOUND, "찜 목록을 찾을 수 없습니다"),
    WISH_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "찜한 상품을 찾을 수 없습니다"),
    WISH_ITEM_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 찜한 상품입니다"),
    WISH_ITEM_DELETE_FAILED(HttpStatus.BAD_REQUEST, "찜한 상품 삭제에 실패했습니다"),

    // Cart 관련 ERROR
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다"),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니를 찾을 수 없습니다"),
    CART_ITEM_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 장바구니에 존재하는 상품입니다"),
    CART_ITEM_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "상품 유형이 일치하지 않습니다"),
    CART_ITEM_COUNT_NOT_MODIFIABLE(HttpStatus.BAD_REQUEST, "예약 상품은 수량 변경이 불가능합니다"),
    INVALID_ITEM_COUNT(HttpStatus.BAD_REQUEST, "수량은 1개 이상이어야 합니다"),

    //주문 관련 ERROR
    NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, "해당 상품의 재고가 없습니다"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문이 존재하지 않습니다"),
    ORDER_CAN_NOT_CANCEL(HttpStatus.BAD_REQUEST, "해당 주문을 취소할 수 없습니다"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 결제 내역이 존재하지 않습니다."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "결제에 실패했습니다"),
    PAYMENT_CANCELED(HttpStatus.BAD_REQUEST, "결제 취소에 실패했습니다"),
    I_AM_PORT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "아이엠 포트 결제 조회에 실패했습니다"),

    //예약 관련 ERROR
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약 내역이 존재하지 않습니다"),
    ;


    private final HttpStatus httpStatus;
    private final String message;
}
