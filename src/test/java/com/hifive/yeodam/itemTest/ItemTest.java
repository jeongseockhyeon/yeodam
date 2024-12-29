package com.hifive.yeodam.itemTest;

import com.hifive.yeodam.item.dto.ActiveUpdateDto;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.item.service.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemTest {

    @InjectMocks
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;

/*
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
        verify(itemRepository, times(1)).findAll();
    }
*/

/*    @Test
    public void itemFindTest() {
        //given
        Long itemId = 1L;
        String itemName = "test"; //임시 상품 이름

        Item item = mock(Item.class);
        when(item.getId()).thenReturn(itemId);
        when(item.getItemName()).thenReturn(itemName);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        //when
        Item testItem = itemService.findById(itemId);

        //then
        assertEquals(itemId, testItem.getId());
        assertEquals(itemName, testItem.getItemName());
        verify(itemRepository, times(1)).findById(itemId);
    }*/

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
        verify(itemRepository, times(1)).findAllItemType();
    }

    @Test
    @DisplayName("상품 활성 상태 변경")
    public void activeUpdateTest(){
        //given
        Long itemId = 1L;
        ActiveUpdateDto activeUpdateDto = mock(ActiveUpdateDto.class);
        when(activeUpdateDto.isActive()).thenReturn(true);

        Item item = mock(Item.class);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        //when
        itemService.updateActive(itemId,activeUpdateDto);

        //then
        verify(itemRepository, times(1)).findById(itemId);
        verify(item, times(1)).updateActive(activeUpdateDto.isActive());
    }

}
