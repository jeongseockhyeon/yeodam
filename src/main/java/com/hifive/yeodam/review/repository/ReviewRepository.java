package com.hifive.yeodam.review.repository;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.review.domain.Review;
import com.hifive.yeodam.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review r " +
            "join fetch r.item i " +
            "join fetch r.user u " +
            "where u.id = :userId ")
    Slice<Review> findByItemAndUserIdPage(Long userId, Pageable pageable);

    @Query("select count(r) from Review r where r.item.id = :itemId")
    int countReviewsByItemId(Long itemId);

    @Query("select r from Review r join fetch r.user u where r.item.id = :itemId")
    Page<Review> findAllByItemId(Long itemId, Pageable pageable);

    @Query("select r " +
            "from Review r " +
            "join r.item i " +
            "where i.id = :itemId " +
            "and i.seller.companyId = :sellerId")
    Page<Review> findAllBySellerIdAndItemId(Long sellerId, Long itemId, Pageable pageable);

    List<Review> findAllByUserId(Long userId);
}
