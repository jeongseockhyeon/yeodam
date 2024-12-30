package com.hifive.yeodam.inquiry.repository;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByAuthId(Long authId);
    List<Inquiry> findByItemIdIn(List<Long> itemIds);
    Inquiry findByParentInquiryId(Long id);
}
