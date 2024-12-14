package com.hifive.yeodam.item.service;

import com.hifive.yeodam.item.dto.ItemReqDto;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemService {

    /*상품 등록*/
    private final ItemRepository itemRepository;
    //private final SellerRepository sellerRepository

    public Item save(ItemReqDto itemReqDto) {
        //Seller findSeller = sellerRepository.findById(itemReqDto.getSellerId())
                                        // .orElseThrow(() -> new RuntimeException("해당 판매자가 없습니다."));

        Item item = Item.builder()
                .sellerId(itemReqDto.getSellerId())
                //.seller(findSeller)
                .itemName(itemReqDto.getItemName())
                .build();

        return itemRepository.save(item);
    }

    /*상품 전체 조회*/
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    /*상품 상세 조회*/
    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 상품이 없습니다"));
    }
}
