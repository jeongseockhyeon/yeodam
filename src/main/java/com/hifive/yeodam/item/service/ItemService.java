package com.hifive.yeodam.item.service;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    /*상품 전체 조회*/
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    /*상품 상세 조회*/
    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 상품이 없습니다"));
    }

    /*상품 타입 조회*/
    public List<String> findAllItemType(){
        return itemRepository.findAllItemType();
    }
}
