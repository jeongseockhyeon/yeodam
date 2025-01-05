package com.hifive.yeodam.item.service;

import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.dto.ActiveUpdateDto;
import com.hifive.yeodam.item.dto.ItemWithOrderResponse;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.orderdetail.domain.OrderDetail;
import com.hifive.yeodam.orderdetail.domain.OrderDetailStatus;
import com.hifive.yeodam.seller.entity.Seller;
import com.hifive.yeodam.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

import static com.hifive.yeodam.global.exception.CustomErrorCode.ITEM_NOT_FOUND;
import static com.hifive.yeodam.global.exception.CustomErrorCode.SELLER_NOT_FOUND;
import static com.hifive.yeodam.orderdetail.domain.OrderDetailStatus.*;

@RequiredArgsConstructor
@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final SellerRepository sellerRepository;

    /*상품 타입 조회*/
    public List<String> findAllItemType() {
        return itemRepository.findAllItemType();
    }

    /*상품 활성화 상태 변경*/
    @Transactional
    public boolean updateActive(Long id, ActiveUpdateDto activeUpdateDto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new CustomException(ITEM_NOT_FOUND));
        item.updateActive(activeUpdateDto.isActive());
        return item.isActive();
    }

    @Transactional(readOnly = true)
    public Page<ItemWithOrderResponse> findItemsWithOrderCountBySeller(Principal principal, int offset, int limit) {
        Seller seller = sellerRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(SELLER_NOT_FOUND));

        Page<Item> items = itemRepository.findBySellerCompanyId(seller.getCompanyId(), PageRequest.of(offset, limit));

        return items.map(i -> {
            int reservationCnt = (int) i.getOrderDetails().stream()
                    .filter(orderDetail -> orderDetail.getStatus().equals(PENDING))
                    .count();

            int cancelCnt = (int) i.getOrderDetails().stream()
                    .filter(orderDetail -> orderDetail.getStatus().equals(CANCELED))
                    .count();

            return new ItemWithOrderResponse(i.getId(), i.getItemName(), reservationCnt, cancelCnt);
        });
    }

    public List<Long> getItemsBySeller(Long companyId) {
        List<Item> items = itemRepository.findBySellerCompanyId(companyId);
        return items.stream()
                .map(Item::getId)
                .toList();
    }

    public void changeCompany(Seller seller) {
        Seller delete = sellerRepository.findById(1L).orElseThrow(() -> new RuntimeException("판매자를 찾을 수 없습니다."));
        List<Item> items = itemRepository.findBySellerCompanyId(seller.getCompanyId());
        for (Item item : items) {
            item.updateActive(false);
            item.changeSeller(delete);
        }
    }
}
