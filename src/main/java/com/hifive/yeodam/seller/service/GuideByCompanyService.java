package com.hifive.yeodam.seller.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.seller.dto.GuideResDto;
import com.hifive.yeodam.seller.entity.Guide;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.GuideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuideByCompanyService {
    private final GuideRepository guideRepository;
    private final SellerService sellerService;

    @Transactional(readOnly = true)
    public List<GuideResDto> getGuideByCompany(Auth auth) {
        Seller seller = sellerService.getSellerByAuth(auth);
        List<Guide> guides =  guideRepository.findBySellerCompanyId(seller.getCompanyId());
        return guides.stream()
                .map(GuideResDto::new)
                .toList();
    }
}
