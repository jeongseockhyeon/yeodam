package com.hifive.yeodam.auth.repository;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByAuth(Auth auth);
}