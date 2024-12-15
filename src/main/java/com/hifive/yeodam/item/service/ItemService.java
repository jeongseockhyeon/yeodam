package com.hifive.yeodam.item.service;

import com.hifive.yeodam.item.dto.ItemTourReqDto;
import com.hifive.yeodam.item.dto.ItemUpdateReqDto;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.hifive.yeodam.tour.entity.Tour;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ItemService {


    private final ItemRepository itemRepository;
    //private final SellerRepository sellerRepository

    /*상품 등록*/
/*    public Item save(ItemReqDto itemReqDto) {
        //Seller findSeller = sellerRepository.findById(itemReqDto.getSellerId())
                                        // .orElseThrow(() -> new RuntimeException("해당 판매자가 없습니다."));

        Item item = Item.builder()
                .sellerId(itemReqDto.getSellerId())
                //.seller(findSeller)
                .itemName(itemReqDto.getItemName())
                .build();

        return itemRepository.save(item);
    }*/

    /*상품 전체 조회*/
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    /*상품 상세 조회*/
    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 상품이 없습니다"));
    }

    /*상품 업데이트*/
    public Item update(ItemUpdateReqDto itemUpdateReqDto) {
        Item targetItem = itemRepository.findById(itemUpdateReqDto.getItemId())
                .orElseThrow(() -> new RuntimeException("해당 상품이 없습니다"));

        targetItem.updateItem(itemUpdateReqDto.getUpdateItemName());
        return itemRepository.save(targetItem);
    }

    /*상품 삭제*/
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    /*상품_여행 등록*/
    public Tour saveItemTour(ItemTourReqDto itemTourReqDto) {
        Tour itemTour = Tour.builder()
                .sellerId(itemTourReqDto.getSellerId())
                .itemName(itemTourReqDto.getTourName())
                .region(itemTourReqDto.getTourRegion())
                .period(itemTourReqDto.getTourPeriod())
                .description(itemTourReqDto.getTourDesc())
                .price(itemTourReqDto.getTourPrice())
                .build();

        return itemRepository.save(itemTour);

    }

}
