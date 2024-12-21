package com.hifive.yeodam.itemTest;

import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@WebMvcTest(ItemService.class)
public class ItemTest {

    @MockitoBean
    private ItemService itemService;

    @Test
    public void allItemFindTest() {
        //given
        int testCount = 1;
        Item item = mock(Item.class);
        List<Item> itemsList = new ArrayList<Item>();

        itemsList.add(item);


        when(itemService.findAll()).thenReturn(itemsList);

        //when
        int count = itemService.findAll().size();

        //then
        assertEquals(testCount, count);
        verify(itemService, times(1)).findAll();
    }

    @Test
    public void itemFindTest() {
        //given
        Long itemId = 1L;
        String itemName = "test"; //임시 상품 이름

        Item item = mock(Item.class);
        when(item.getId()).thenReturn(itemId);
        when(item.getItemName()).thenReturn(itemName);

        when(itemService.findById(itemId)).thenReturn(item);

        //when
        Item testItem = itemService.findById(itemId);

        //then
        assertEquals(itemId, testItem.getId());
        assertEquals(itemName, testItem.getItemName());
        verify(itemService, times(1)).findById(itemId);
    }

    @Test
    public void findAllItemTypeTest(){
        //given
        List<String> testTypes = new ArrayList<>();
        String testType = "Tour";
        testTypes.add(testType);

        when(itemService.findAllItemType()).thenReturn(testTypes);

        //when
        List<String> itemTypes = itemService.findAllItemType();

        //then
        assertEquals(testTypes.getFirst(), itemTypes.getFirst());
        assertEquals(testTypes.size(), itemTypes.size());
        verify(itemService, times(1)).findAllItemType();
    }

}
