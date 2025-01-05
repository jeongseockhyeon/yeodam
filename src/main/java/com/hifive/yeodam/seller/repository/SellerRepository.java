package com.hifive.yeodam.seller.repository;

import com.hifive.yeodam.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByAuthId(Long authId);

    @Query("select s from Seller s join s.auth a where a.email = :email")
    Optional<Seller> findByEmail(String email);
}

