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
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("해당 상품을 찾을 수 없습니다."));
        Inquiry inquiry = Inquiry.builder()
                .auth(auth)
                .item(item)
                .title(request.getTitle())
                .content(request.getContent())
                .isAnswered("N")
                .parentInquiry(null)
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

        if(inquiry.getIsAnswered().equals("Y")) {
            Inquiry answer = inquiryRepository.findByParentInquiryId(id);
            inquiryRepository.deleteById(answer.getId());
        }

        inquiryRepository.delete(inquiry);
    }

    @Transactional
    public List<InquiryResponse> getInquiriesByItemIds(List<Long> itemIds) {
        List<Inquiry> inquiries = inquiryRepository.findByItemIdIn(itemIds);
        return inquiries.stream()
                .map(InquiryResponse::new)
                .toList();
    }

    @Transactional
    public void answerInquiry(Long id, AddInquiryRequest request, Auth auth) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 문의를 찾을 수 없습니다."));

        if (inquiry.getIsAnswered().equals("Y")) {
            throw new RuntimeException("이미 답변이 완료된 문의입니다.");
        }

        inquiry.update();

        Inquiry answer = Inquiry.builder()
                .auth(auth)
                .item(inquiry.getItem())
                .title(request.getTitle())
                .content(request.getContent())
                .isAnswered("Y")
                .parentInquiry(inquiry)
                .build();

        inquiryRepository.save(answer);
    }
}
