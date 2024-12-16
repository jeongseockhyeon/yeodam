package com.hifive.yeodam.order.repository;

import com.hifive.yeodam.order.domain.MockUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MockUserRespiratory extends JpaRepository<MockUser, Long> {
    Optional<MockUser> findById(Long id);
}
