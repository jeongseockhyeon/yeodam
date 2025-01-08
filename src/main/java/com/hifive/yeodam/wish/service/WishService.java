package com.hifive.yeodam.wish.service;

import com.hifive.yeodam.auth.entity.Auth;
import com.hifive.yeodam.auth.repository.AuthRepository;
import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.repository.ItemRepository;
import com.hifive.yeodam.user.entity.User;
import com.hifive.yeodam.user.repository.UserRepository;
import com.hifive.yeodam.wish.dto.WishDto;
import com.hifive.yeodam.wish.entity.Wish;
import com.hifive.yeodam.wish.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WishService {
    private final WishRepository wishRepository;
    private final AuthRepository authRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public void addWish(Long authId, Long itemId) {
        User user = getUserByAuthId(authId);
        Item item = getItemById(itemId);

        if (wishRepository.existsByUserAndItem(user, item)) {
            throw new CustomException(CustomErrorCode.WISH_ITEM_DUPLICATE);
        }

        Wish wish = new Wish(user, item);
        wishRepository.save(wish);
    }
    //찜 목록 조회
    @Transactional(readOnly = true)
    public List<WishDto> getWish(Long authId) {
        User user = getUserByAuthId(authId);
        List<Wish> wishItems = wishRepository.findByUserOrderByCreatedAtDesc(user);

        return wishItems.stream()
                .map(WishDto::from)
                .collect(Collectors.toList());
    }

    //찜 상태 확인
    @Transactional(readOnly = true)
    public boolean isWished(Long authId, Long itemId) {
        User user = getUserByAuthId(authId);
        Item item = getItemById(itemId);

        return wishRepository.existsByUserAndItem(user, item);
    }

    //찜 제거
    public void removeWish(Long authId, Long itemId) {
        User user = getUserByAuthId(authId);
        Item item = getItemById(itemId);

        if (!wishRepository.existsByUserAndItem(user, item)) {
            throw new CustomException(CustomErrorCode.WISH_ITEM_NOT_FOUND);
        }
        try {
            wishRepository.deleteByUserAndItem(user, item);
        } catch (Exception e) {
            throw new CustomException(CustomErrorCode.WISH_ITEM_DELETE_FAILED);
        }
    }

    //User 조회 private 메소드
    private User getUserByAuthId(Long authId) {
        //Auth 검증
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.AUTH_NOT_FOUND));
        //User 검증
        return userRepository.findByAuthId(authId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    //Item 조회 Private 메소드
    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.ITEM_NOT_FOUND));
    }
}
