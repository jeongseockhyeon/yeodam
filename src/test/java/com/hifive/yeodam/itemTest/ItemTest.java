package com.hifive.yeodam.itemTest;

import com.hifive.yeodam.item.dto.ItemReqDto;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.item.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemTest {

    @Autowired
    private ItemService itemService;

    @Test
    public void itemSaveTest() {
        //given

        Long sellerId = 1L; //임시 판매자 고유 번호
        String itemName = "test"; //임시 상품 이름
        ItemReqDto itemReqDto = new ItemReqDto();
        itemReqDto.setSellerId(sellerId);
        itemReqDto.setItemName(itemName);

        //when
        Item item = itemService.save(itemReqDto);

        //then
        assertEquals(sellerId, item.getSellerId());
        assertEquals(itemName, item.getItemName());
    }

}
