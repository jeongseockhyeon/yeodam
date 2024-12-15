package com.hifive.yeodam.seller.service;

import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    // 판매자 등록
    @Transactional
    public Seller createSeller(Seller seller) {
        return sellerRepository.save(seller);
    }

    // 판매자 정보 수정
    @Transactional
    public Seller updateSeller(Long id, Seller updatedSeller) {
        Seller existingSeller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("판매자를 찾을 수 없습니다."));

        existingSeller.setOwner(updatedSeller.getOwner());
        existingSeller.setCompanyName(updatedSeller.getCompanyName());
        existingSeller.setBio(updatedSeller.getBio());

        return existingSeller;
    }

    // 판매자 삭제
    @Transactional
    public void deleteSeller(Long id) {
        sellerRepository.deleteById(id);
    }

    // 판매자 전체 조회
    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    // 판매자 단일 조회
    public Seller getSellerById(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("판매자를 찾을 수 없습니다."));
    }
}