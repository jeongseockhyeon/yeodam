package com.hifive.yeodam.tour.dto;

import com.hifive.yeodam.category.dto.CategoryResDto;
import com.hifive.yeodam.item.dto.ItemImgResDto;
import com.hifive.yeodam.item.entity.ItemImage;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.tour.entity.TourCategory;
import com.hifive.yeodam.tour.entity.TourGuide;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class TourItemResDto {
    private final Long id;

    private final String tourName;

    private final String tourDesc;

    private final String tourPeriod;

    private final String tourRegion;

    private final int tourPrice;

    private final int maximum;

    private final boolean active;

    private final double rate;

    private final int reviewCount;

    private final List<CategoryResDto> categoryResDtoList = new ArrayList<>();
    private final List<GuideInTourResDto> guideInTourResDtos = new ArrayList<>();
    private final List<ItemImgResDto> itemImgResDtoList = new ArrayList<>();

    public TourItemResDto(Tour tour) {
        this.id = tour.getId();
        this.tourName = tour.getItemName();
        this.tourDesc = tour.getDescription();
        this.tourPeriod = tour.getPeriod();
        this.tourRegion = tour.getRegion();
        this.tourPrice = tour.getPrice();
        this.maximum = tour.getMaximum();
        this.active = tour.isActive();
        this.rate = tour.getRate();

        for(TourCategory tourCategory : tour.getTourCategories()){
            CategoryResDto categoryResDto = new CategoryResDto(tourCategory.getCategory());
            this.categoryResDtoList.add(categoryResDto);
        }

        for(TourGuide tourGuide : tour.getTourGuides()){
            GuideInTourResDto guideInTourResDto = new GuideInTourResDto(tourGuide.getGuide());
            this.guideInTourResDtos.add(guideInTourResDto);
        }

        for(ItemImage itemImage : tour.getItemImages()){
            this.itemImgResDtoList.add(new ItemImgResDto(itemImage));
        }

        this.reviewCount = tour.getReviews().size();

    }
}
