package com.hifive.yeodam.tour.dto;

import com.hifive.yeodam.category.dto.CategoryResDto;
import com.hifive.yeodam.tour.entity.Tour;
import com.hifive.yeodam.tour.entity.TourCategory;
import com.hifive.yeodam.tour.entity.TourGuide;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TourItemResDto {
    private String tourName;

    private String tourDesc;

    private String tourPeriod;

    private String tourRegion;

    private int tourPrice;

    private int maximum;

    private List<CategoryResDto> categoryResDtoList = new ArrayList<>();
    private List<GuideInTourResDto> guideInTourResDtos = new ArrayList<>();

    public TourItemResDto(Tour tour) {
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
    }
}
