package com.hifive.yeodam.item.repository;

import com.hifive.yeodam.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
