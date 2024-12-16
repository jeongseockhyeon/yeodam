package com.hifive.yeodam.order.repository;

import com.hifive.yeodam.order.domain.MockUser;

import java.util.Optional;

public interface MockUserRespiratory {
    Optional<MockUser> findById(Long id);
}
