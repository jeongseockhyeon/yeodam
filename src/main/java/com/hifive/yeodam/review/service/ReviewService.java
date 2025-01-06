package com.hifive.yeodam.review.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.image.service.ImageService;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.orderdetail.repository.OrderDetailRepository;
import com.hifive.yeodam.review.domain.Review;
import com.hifive.yeodam.review.domain.ReviewImage;
import com.hifive.yeodam.review.dto.CreateReviewRequest;
import com.hifive.yeodam.review.dto.ItemDetailReviewResponse;
import com.hifive.yeodam.review.dto.SellerReviewResponse;
import com.hifive.yeodam.review.dto.UserReviewResponse;
import com.hifive.yeodam.review.repository.ReviewRepository;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.SellerRepository;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.hifive.yeodam.global.exception.CustomErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ItemRepository itemRepository;
    private final ImageService imageService;

    @Transactional
    public void createReview(Principal principal, String orderUid, Long itemId,
                             String description, double rate, List<MultipartFile> images) {

        OrderDetail orderDetail = getOrderDetail(itemId, orderUid);
        User user = getUser(principal);

        Item item = orderDetail.getItem();
        item.updateRate(rate, reviewRepository.countReviewsByItemId(item.getId()));

        Review review = Review.builder()
                .item(orderDetail.getItem())
                .orderDetail(orderDetail)
                .user(user)
                .description(description)
                .rate(rate)
                .build();

        if (images.size() > 0 && StringUtils.hasText(images.getFirst().getOriginalFilename())) {
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
    public void deleteReview(Principal principal, Long reviewId) {
        User user = getUser(principal);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

        if (user.equals(review.getUser())) {
            reviewRepository.deleteById(reviewId);
            return;
        }

        throw new CustomException(REVIEW_NOT_DELETE);
    }


    @Transactional(readOnly = true)
    public ItemDetailReviewResponse findAllByItemId(Long itemId, int limit) {

        PageRequest pageRequest = PageRequest.ofSize(limit);
        Page<Review> findReviews = reviewRepository.findAllByItemId(itemId, pageRequest);

        return getItemDetailReviewResponse(findReviews);
    }

    @Transactional(readOnly = true)
    public UserReviewResponse findReviewsByUser(Principal principal, int size) {
        User user = getUser(principal);
        PageRequest pageRequest = PageRequest.ofSize(size);
        Slice<Review> findReviews = reviewRepository.findByItemAndUserIdPage(user.getId(), pageRequest);

        List<UserReviewResponse.Review> reviews = getUserReviews(findReviews);

        return UserReviewResponse.builder()
                .hasNext(findReviews.hasNext())
                .reviews(reviews)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<SellerReviewResponse> findAllBySellerId(Principal principal, int offset, int limit) {

        Seller seller = sellerRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(SELLER_NOT_FOUND));

        return itemRepository.findBySellerCompanyId(seller.getCompanyId(), PageRequest.of(offset, limit))
                .map(item -> SellerReviewResponse.builder()
                        .itemId(item.getId())
                        .itemName(item.getItemName())
                        .rate(item.getRate())
                        .count(item.getReviews().size())
                        .build()
                );
    }

    @Transactional(readOnly = true)
    public Page<SellerReviewResponse> findDetailsBySellerAndItem(Principal principal, Long itemId, int offset, int limit) {

        Seller seller = sellerRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(SELLER_NOT_FOUND));

        Page<Review> reviews = reviewRepository
                .findAllBySellerIdAndItemId(seller.getCompanyId(), itemId, PageRequest.of(offset, limit));

        return reviews.map(r -> SellerReviewResponse.builder()
                .itemId(r.getItem().getId())
                .imgPaths(getReviewImgPaths(r))
                .rate(r.getRate())
                .createAt(LocalDate.from(r.getCreateAt()))
                .description(r.getDescription())
                .build());
    }

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

    private User getUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    private List<UserReviewResponse.Review> getUserReviews(Slice<Review> findReviews) {
        return findReviews.stream()
                .map(r -> UserReviewResponse.Review.builder()
                        .reviewId(r.getId())
                        .itemName(r.getItem().getItemName())
                        .createAt(LocalDate.from(r.getCreateAt()))
                        .rate(r.getRate())
                        .description(r.getDescription())
                        .reviewImgPath(getReviewImgPaths(r))
                        .build())
                .toList();
    }

    private OrderDetail getOrderDetail(Long itemId, String orderUid) {
        return orderDetailRepository.findByItemOrderUid(itemId, orderUid)
                .orElseThrow(() -> new CustomException(ORDER_DETAIL_NOT_FOUND));
    }

    private String getItemImgPath(Item item) {
        if (item.getItemImages().isEmpty()) {
            return null;
        }
        return item.getItemImages().getFirst().getStorePath();
    }

    private ItemDetailReviewResponse getItemDetailReviewResponse(Page<Review> findReviews) {
        if (findReviews.getContent().isEmpty()) {
            ItemDetailReviewResponse.builder()
                    .reviews(new ArrayList<>())
                    .build();
        }

        List<ItemDetailReviewResponse.Review> reviews = findReviews.stream()
                .map(review ->
                        ItemDetailReviewResponse.Review.builder()
                                .rate(review.getRate())
                                .nickName(review.getUser().getNickname())
                                .description(review.getDescription())
                                .imgPaths(getReviewImgPaths(review))
                                .build()
                )
                .toList();

        return ItemDetailReviewResponse.builder()
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