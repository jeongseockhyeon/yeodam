package com.hifive.yeodam.item.service;

import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.dto.ActiveUpdateDto;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final SellerRepository sellerRepository;

    /*상품 타입 조회*/
    public List<String> findAllItemType(){
        return itemRepository.findAllItemType();
    }

    /*상품 활성화 상태 변경*/
    @Transactional
    public boolean updateActive(Long id, ActiveUpdateDto activeUpdateDto){
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.ITEM_NOT_FOUND));
        item.updateActive(activeUpdateDto.isActive());
        return item.isActive();
    }

    public List<Long> getItemsBySeller(Long companyId) {
        List<Item> items = itemRepository.findBySellerCompanyId(companyId);
        return items.stream()
                .map(Item::getId)
                .toList();
    }

    public void changeCompany(Seller seller) {
        Seller delete = sellerRepository.findById(1L).orElseThrow(() -> new RuntimeException("판매자를 찾을 수 없습니다."));
        List<Item> items = itemRepository.findBySellerCompanyId(seller.getCompanyId());
        for (Item item : items) {
            item.changeSeller(delete);
        }
    }
}
