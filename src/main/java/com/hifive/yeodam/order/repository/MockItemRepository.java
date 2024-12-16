package com.hifive.yeodam.order.repository;

import com.hifive.yeodam.order.domain.MockItem;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface MockItemRepository {
    Optional<MockItem> findById(Long id);
}
