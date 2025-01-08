package com.hifive.yeodam.review.domain;

import com.hifive.yeodam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class ReviewImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "review_image_id")
    private Long id;

    @Setter
    @JoinColumn(name = "review_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    private String originalName;
    private String storePath;

    @Builder
    public ReviewImage(Review review, String originalName, String storePath) {
        this.review = review;
        this.originalName = originalName;
        this.storePath = storePath;
    }
}
