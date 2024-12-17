package com.hifive.yeodam.order.service.code;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MockUserRespiratory extends JpaRepository<MockUser, Long> {
    Optional<MockUser> findById(Long id);
}
