package com.hifive.yeodam.review.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class UserReviewResponse {

    private boolean hasNext;
    private List<UserReviewResponse.Review> reviews;

    @Builder
    @Data
    public static class Review {
        private Long reviewId;
        private String itemName;
        private double rate;
        private LocalDate createAt;
        private String description;
        private List<String> reviewImgPath;
    }
}
