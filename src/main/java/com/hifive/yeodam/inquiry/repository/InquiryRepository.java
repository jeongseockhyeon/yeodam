package com.hifive.yeodam.inquiry.repository;

import com.hifive.yeodam.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}
