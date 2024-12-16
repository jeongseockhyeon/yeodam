package com.hifive.yeodam.auth.repository;

import com.hifive.yeodam.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    Boolean existsByEmail(String email);
}
