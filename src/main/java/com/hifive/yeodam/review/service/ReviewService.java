package com.hifive.yeodam.review.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.image.service.ImageService;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.orderdetail.repository.OrderDetailRepository;
import com.hifive.yeodam.review.domain.Review;
import com.hifive.yeodam.review.domain.ReviewImage;
import com.hifive.yeodam.review.dto.CreateReviewRequest;
import com.hifive.yeodam.review.dto.ReviewResponse;
import com.hifive.yeodam.review.repository.ReviewRepository;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.hifive.yeodam.global.exception.CustomErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final OrderDetailRepository orderDetailRepository;

    @Transactional
    public void createReview(Principal principal, String orderUid, Long itemId,
                             String description, double rate, List<MultipartFile> images) {

        OrderDetail orderDetail = getOrderDetail(itemId, orderUid);
        User user = getUser(principal);

        updateItemRating(orderDetail.getItem(), rate);

        Review review = Review.builder()
                .item(orderDetail.getItem())
                .orderDetail(orderDetail)
                .user(user)
                .description(description)
                .rate(rate)
                .build();

        if (images.size() > 0) {
            images.forEach(image -> {
                String imgPath = imageService.upload(image);
                ReviewImage reviewImage = ReviewImage.builder()
                        .review(review)
                        .originalName(image.getOriginalFilename())
                        .storePath(imgPath)
                        .build();
                review.addReviewImage(reviewImage);
            });
        }

        reviewRepository.save(review);
    }

    @Transactional
    public CreateReviewRequest getCreateReviewRequest(Long itemId, String orderUid) {
        OrderDetail orderDetail = getOrderDetail(itemId, orderUid);

        return CreateReviewRequest.builder()
                .itemName(orderDetail.getItem().getItemName())
                .itemId(orderDetail.getItem().getId())
                .imagePath(getItemImgPath(orderDetail.getItem()))
                .orderUid(orderDetail.getOrder().getOrderUid())
                .startDate(orderDetail.getReservation().getStartDate())
                .endDate(orderDetail.getReservation().getEndDate())
                .count(orderDetail.getCount())
                .build();
    }

    @Transactional
    public void updateContent(Principal principal, Long reviewId, Long itemId, double rate, String description) {

        User user = getUser(principal);
        Review review = reviewRepository.findByReviewUserItemId(reviewId, itemId, user.getId())
                .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));

        review.updateContent(rate, description);
    }

    @Transactional
    public void deleteReview(Principal principal, Long itemId) {
        User user = getUser(principal);
        reviewRepository.delete(itemId, user.getId());
    }

    @Transactional(readOnly = true)
    public ReviewResponse findAllByItemId(Long itemId, int limit) {

        PageRequest pageRequest = PageRequest.ofSize(limit);
        Page<Review> findReviews = reviewRepository.findAllByItemId(itemId, pageRequest);

        return getReviewResponse(findReviews);
    }

    @Transactional(readOnly = true)
    public Page<Review> findReviewsByUser(Principal principal, Long itemId, int page, int size) {
        User user = getUser(principal);
        PageRequest pageRequest = PageRequest.of(page, size);
        return reviewRepository.findByItemAndUserIdPage(itemId, user.getId(), pageRequest);
    }

    private User getUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    private OrderDetail getOrderDetail(Long itemId, String orderUid) {
        return orderDetailRepository.findByItemOrderUid(itemId, orderUid)
                .orElseThrow(() -> new CustomException(ORDER_DETAIL_NOT_FOUND));
    }

    private void updateItemRating(Item item, double rate) {
        item.updateRate(rate, reviewRepository.countReviewsByItemId(item.getId()));
    }

    private String getItemImgPath(Item item) {
        if (item.getItemImages().isEmpty()) {
            return null;
        }
        return item.getItemImages().getFirst().getStorePath();
    }

    private ReviewResponse getReviewResponse(Page<Review> findReviews) {

        List<ReviewResponse.Review> reviews = findReviews.stream()
                .map(review ->
                        ReviewResponse.Review.builder()
                                .rate(review.getRate())
                                .nickName(review.getUser().getNickname())
                                .description(review.getDescription())
                                .imgPaths(getReviewImgPaths(review))
                                .build()
                )
                .toList();

        return ReviewResponse.builder()
                .reviews(reviews)
                .totalCount(findReviews.getTotalElements())
                .totalRate(findReviews.getContent().getFirst().getItem().getRate())
                .hasNext(findReviews.hasNext())
                .build();
    }

    private List<String> getReviewImgPaths(Review review) {
        if (review.getReviewImages().isEmpty()) {
            return new ArrayList<>();
        }
        return review.getReviewImages().stream()
                .map(ReviewImage::getStorePath)
                .toList();
    }
}