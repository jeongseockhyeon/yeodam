package com.hifive.yeodam.item.repository;

import com.hifive.yeodam.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT DISTINCT item_type FROM item", nativeQuery = true)
    List<String> findAllItemType();

    List<Item> findBySellerCompanyId(Long companyId);

    Page<Item> findBySellerCompanyId(Long companyId, Pageable pageable);
}
