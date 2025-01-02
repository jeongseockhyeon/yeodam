package com.hifive.yeodam.review.controller;

import com.hifive.yeodam.review.dto.CreateReviewRequest;
import com.hifive.yeodam.review.dto.ReviewResponse;
import com.hifive.yeodam.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/items/{itemId}/reviews/new")
    public String createReviewForm(@PathVariable Long itemId, @RequestParam String orderUid, Model model) {
        model.addAttribute("request", reviewService.getCreateReviewRequest(itemId, orderUid));
        return "/review/review-form";
    }

    @PostMapping("/items/{itemId}/reviews/new")
    public String createReview(CreateReviewRequest request, Principal principal) {
        reviewService.createReview(principal, request.getOrderUid(), request.getItemId(), request.getDescription(), request.getRate(), request.getImages());
        return "redirect:/";
    }

    @ResponseBody
    @GetMapping("/api/{itemId}/reviews")
    public ReviewResponse reviewList(@PathVariable Long itemId, @RequestParam(name = "limit", defaultValue = "6") int limit) {
        return reviewService.findAllByItemId(itemId, limit);
    }
}
