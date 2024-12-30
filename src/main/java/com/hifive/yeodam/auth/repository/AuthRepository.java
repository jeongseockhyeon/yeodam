package com.hifive.yeodam.auth.repository;

import com.hifive.yeodam.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<Auth> findByExpirationDate(LocalDate expirationDate);
}
