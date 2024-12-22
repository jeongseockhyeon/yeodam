package com.hifive.yeodam.auth.repository;

import com.hifive.yeodam.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
