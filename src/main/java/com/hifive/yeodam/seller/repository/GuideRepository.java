package com.hifive.yeodam.seller.repository;

import com.hifive.yeodam.seller.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuideRepository extends JpaRepository<Guide, Long> {
    // 회사 아이디로 가이드 조회
    List<Guide> findBySellerCompanyId(Long companyId);
}