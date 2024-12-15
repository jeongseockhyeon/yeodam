package com.hifive.yeodam.itemTest;

import com.hifive.yeodam.item.dto.ItemUpdateReqDto;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class ItemTest {

    @Autowired
    private ItemService itemService;

/*    @Test
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
    }*/

    @Test
    public void allItemFindTest() {
        //given
        int testCount = 1;

        //when
        int listCount = itemService.findAll().size();

        //then
        assertEquals(testCount, listCount);
    }

    @Test
    public void itemFindTest() {
        //given
        Long testItemId = 1L;
        Long sellerId = 1L; //임시 판매자 고유 번호
        String itemName = "test"; //임시 상품 이름

        //when
        Item item = itemService.findById(testItemId);

        //then
        assertEquals(sellerId, item.getSellerId());
        assertEquals(itemName, item.getItemName());
    }

    @Test
    public void itemUpdateTest() {
        //given
        Long itemId = 2L;
        String itemName = "updateTest";
        ItemUpdateReqDto itemUpdateReqDto = new ItemUpdateReqDto();
        itemUpdateReqDto.setUpdateItemName(itemName);

        //when
        Item item = itemService.update(itemId, itemUpdateReqDto);

        //then
        assertEquals(itemId, item.getId());
        assertEquals(itemName, item.getItemName());
    }

    @Test
    public void itemDeleteTest() {
        //given
        Long itemId = 1L;

        //when
        itemService.deleteItem(itemId);

        //then
        assertNull(itemService.findById(itemId)); //예외 발생으로 테스트 실패
    }

}
