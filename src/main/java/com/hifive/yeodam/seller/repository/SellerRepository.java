package com.hifive.yeodam.seller.repository;

import com.hifive.yeodam.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByAuthId(Long authId);
}

