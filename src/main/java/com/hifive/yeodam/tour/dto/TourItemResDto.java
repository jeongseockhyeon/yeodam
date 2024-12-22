package com.hifive.yeodam.tour.dto;

import com.hifive.yeodam.category.dto.CategoryResDto;
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

    private final List<CategoryResDto> categoryResDtoList = new ArrayList<>();
    private final List<GuideInTourResDto> guideInTourResDtos = new ArrayList<>();
    private final List<String> images = new ArrayList<>();

    public TourItemResDto(Tour tour) {
        this.id = tour.getId();
        this.tourName = tour.getItemName();
        this.tourDesc = tour.getDescription();
        this.tourPeriod = tour.getPeriod();
        this.tourRegion = tour.getRegion();
        this.tourPrice = tour.getPrice();
        this.maximum = tour.getMaximum();

        for(TourCategory tourCategory : tour.getTourCategories()){
            CategoryResDto categoryResDto = new CategoryResDto(tourCategory.getCategory());
            this.categoryResDtoList.add(categoryResDto);
        }

        for(TourGuide tourGuide : tour.getTourGuides()){
            GuideInTourResDto guideInTourResDto = new GuideInTourResDto(tourGuide.getGuide());
            this.guideInTourResDtos.add(guideInTourResDto);
        }

        for(ItemImage itemImage : tour.getItemImages()){
            String image = itemImage.getStorePath();
            this.images.add(image);
        }
    }
}
