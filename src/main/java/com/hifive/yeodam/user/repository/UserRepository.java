package com.hifive.yeodam.user.repository;

import com.hifive.yeodam.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickname);

    @Query("select u from User u join fetch u.auth a where u.auth.email = :email")
    Optional<User> findByEmail(String email);

    Optional<User> findByAuthId(Long id);
}
