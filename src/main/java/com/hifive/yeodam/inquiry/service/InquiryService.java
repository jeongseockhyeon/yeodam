package com.hifive.yeodam.inquiry.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.inquiry.dto.AddInquiryRequest;
import com.hifive.yeodam.inquiry.dto.InquiryResponse;
import com.hifive.yeodam.inquiry.entity.Inquiry;
import com.hifive.yeodam.inquiry.repository.InquiryRepository;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public Inquiry createInquiry(AddInquiryRequest request, Auth auth) {
        Inquiry parentInquiry;
        if(request.getParentId() != null) {
            parentInquiry = inquiryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("해당 문의를 찾을 수 없습니다."));
            parentInquiry.update();
        } else {
            parentInquiry = null;
        }
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("해당 상품을 찾을 수 없습니다."));
        Inquiry inquiry = Inquiry.builder()
                .auth(auth)
                .item(item)
                .title(request.getTitle())
                .content(request.getContent())
                .isAnswered("N")
                .parentInquiry(parentInquiry)
                .build();

        return inquiryRepository.save(inquiry);
    }

    @Transactional
    public List<InquiryResponse> getInquiryByAuth(Auth auth) {
        List<Inquiry> inquiries = inquiryRepository.findByAuthId(auth.getId());
        return inquiries.stream()
                .map(InquiryResponse::new)
                .toList();
    }

    @Transactional
    public void deleteInquiry(Long id, Auth auth) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 문의를 찾을 수 없습니다."));

        if (!inquiry.getAuth().getId().equals(auth.getId())) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        inquiryRepository.delete(inquiry);
    }
}
