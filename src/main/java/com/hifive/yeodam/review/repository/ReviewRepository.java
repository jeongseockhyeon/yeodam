package com.hifive.yeodam.review.repository;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.review.domain.Review;
import com.hifive.yeodam.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review r " +
            "join fetch r.item i " +
            "join fetch r.user u " +
            "where i.id = :itemId " +
            "and u.id = :userId")
    Page<Review> findByItemAndUserIdPage(Long itemId, Long userId, Pageable pageable);

    @Query("select r from Review r " +
            "join fetch r.item i " +
            "join fetch r.user u " +
            "where r.id = :ReviewId " +
            "and i.id = :item " +
            "and u.id = :userId ")
    Optional<Review> findByReviewUserItemId(Long reviewId, Long itemId, Long userId);

    @Query("select count(r) from Review r where r.item.id = :itemId")
    int countReviewsByItemId(Long itemId);

    @Query(value = "select r from Review r join fetch r.user u where r.item.id = :itemId")
    Page<Review> findAllByItemId(Long itemId, Pageable pageable);

    @Modifying
    @Query("delete from Review r " +
            "where r.item.id = :itemId " +
            "and r.user.id = :userId")
    void delete(Long itemId, Long userId);

    boolean existsByUserAndItem(User user, Item item);
}
