package com.hifive.yeodam.item.repository;

import com.hifive.yeodam.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT DISTINCT item_type FROM item", nativeQuery = true)
    List<String> findAllItemType();
}
