package com.hifive.yeodam.review.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class SellerReviewResponse {
    private Long itemId;
    private String itemName;
    private List<String> imgPaths;
    private LocalDate createAt;
    private String description;
    private double rate; // 상품 총 rate or 리뷰 단건의 rate
    private int count;
}
