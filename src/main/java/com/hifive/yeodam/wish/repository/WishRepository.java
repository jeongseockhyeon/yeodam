package com.hifive.yeodam.wish.repository;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.wish.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishRepository extends JpaRepository<Wish, Long> {
    List<Wish> findByUserOrderByCreatedAtDesc(User user);
    boolean existsByUserAndItem(User user, Item item);
    void deleteByUserAndItem(User user, Item item);
}