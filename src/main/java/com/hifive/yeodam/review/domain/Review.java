package com.hifive.yeodam.review.domain;

import com.hifive.yeodam.global.entity.BaseEntity;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    private Item item;

    @JoinColumn(name = "order_detail_id")
    @OneToOne(fetch = LAZY)
    private OrderDetail orderDetail;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = LAZY)
    private User user;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImages = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    private LocalDateTime createAt;

    private double rate;

    @Builder
    public Review(Item item, User user, OrderDetail orderDetail, String description, double rate) {
        this.user = user;
        this.description = description;
        this.rate = rate;
        setOrderDetail(orderDetail);
        setItem(item);
    }

    private void setItem(Item item) {
        this.item = item;
        item.getReviews().add(this);
    }

    private void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
        orderDetail.setReview(this);
    }

    public void addReviewImage(ReviewImage reviewImage) {
        reviewImages.add(reviewImage);
        reviewImage.setReview(this);
    }

    public void updateContent(double rate, String description) {
        this.rate = rate;
        this.description = description;
    }
}
