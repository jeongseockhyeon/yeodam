package com.hifive.yeodam.review.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ItemDetailReviewResponse {

    List<Review> reviews;
    private double totalRate;
    private Long totalCount;
    private boolean hasNext;

    @Data
    @Builder
    public static class Review {

        private double rate;
        private String nickName;
        private String description;
        private List<String> imgPaths;
    }
}
