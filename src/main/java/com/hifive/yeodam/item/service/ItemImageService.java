package com.hifive.yeodam.item.service;

import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.image.service.ImageService;
import com.hifive.yeodam.item.entity.Item;
import com.hifive.yeodam.item.entity.ItemImage;
import com.hifive.yeodam.item.repository.ItemImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ItemImageService {
    private final ItemImageRepository itemImageRepository;
    private final ImageService imageService;

    @Transactional
    public void save(MultipartFile imageFile, Item targetItem) {
        String originalName = imageFile.getOriginalFilename();
        String storePath = imageService.upload(imageFile);

        ItemImage itemImage = ItemImage.builder()
                .item(targetItem)
                .originalName(originalName)
                .storePath(storePath)
                .build();

        itemImageRepository.save(itemImage);
    }

    @Transactional
    public void delete(Long imageId) {
        ItemImage itemImage = itemImageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.IMG_NOT_FOUND));
        imageService.delete(itemImage.getStorePath());
        itemImageRepository.delete(itemImage);
    }
}
