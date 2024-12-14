package com.hifive.yeodam.item.service;

import com.hifive.yeodam.item.dto.ItemReqDto;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    //private final SellerRepository sellerRepository

    public Item save(ItemReqDto itemReqDto) {
        //Seller findSeller = sellerRepository.findById(itemReqDto.getSellerId()).orElseThrow(() -> new RuntimeException("해당 판매자가 없습니다."));

        Item item = Item.builder()
                .sellerId(itemReqDto.getSellerId())
                //.seller(findSeller)
                .itemName(itemReqDto.getItemName())
                .build();

        return itemRepository.save(item);
    }
}
