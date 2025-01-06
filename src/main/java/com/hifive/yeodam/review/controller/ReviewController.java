package com.hifive.yeodam.review.controller;

import com.hifive.yeodam.review.dto.CreateReviewRequest;
import com.hifive.yeodam.review.dto.ItemDetailReviewResponse;
import com.hifive.yeodam.review.dto.UserReviewResponse;
import com.hifive.yeodam.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        return "review/review-form";
    }

    @PostMapping("/items/{itemId}/reviews/new")
    public String createReview(CreateReviewRequest request, Principal principal) {

        reviewService.createReview(
                principal, request.getOrderUid(),
                request.getItemId(), request.getDescription(),
                request.getRate(), request.getImages());

        return "redirect:/";
    }

    @GetMapping("/users/reviews")
    public String reviewPage() {
        return "review/review-user-list";
    }

    @GetMapping("/sellers/items/reviews")
    public String findItemListWithCountBySeller(
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "limit", defaultValue = "6") int limit,
            Principal principal, Model model) {

        model.addAttribute("response", reviewService.findAllBySellerId(principal, offset, limit));
        return "review/review-seller-list";
    }

    @GetMapping("/sellers/items/{itemId}/reviews")
    public String getReviewsBySellerItem(
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "limit", defaultValue = "6") int limit,
            @PathVariable Long itemId, Principal principal, Model model) {


        model.addAttribute("response", reviewService.findDetailsBySellerAndItem(principal, itemId, offset, limit));

        return "review/review-seller-detail";
    }

    @ResponseBody
    @GetMapping("/api/users/reviews")
    public UserReviewResponse getReviewsByUser(
            Principal principal,
            @RequestParam(name = "limit", defaultValue = "6") int limit) {

        return reviewService.findReviewsByUser(principal, limit);
    }

    @ResponseBody
    @GetMapping("/api/{itemId}/reviews")
    public ItemDetailReviewResponse reviewList(
            @PathVariable Long itemId,
            @RequestParam(name = "limit", defaultValue = "6") int limit) {

        return reviewService.findAllByItemId(itemId, limit);
    }

    @ResponseBody
    @DeleteMapping("/api/users/reviews/{reviewId}")
    public ResponseEntity deleteReview(@PathVariable Long reviewId, Principal principal) {
        reviewService.deleteReview(principal, reviewId);
        return ResponseEntity.ok().build();
    }
}
