package com.hifive.yeodam.review.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewRequest {

    private String itemName;
    private Long itemId;

    @Min(value = 50)
    private String orderUid;
    private String imagePath;
    private LocalDate startDate;
    private LocalDate endDate;
    private int count;
    private double rate;
    private String description;

    private List<MultipartFile> images;
}
