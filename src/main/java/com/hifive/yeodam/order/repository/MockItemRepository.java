package com.hifive.yeodam.order.repository;

import com.hifive.yeodam.order.domain.MockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface MockItemRepository extends JpaRepository<MockItem, Long> {
    Optional<MockItem> findById(Long id);
}
