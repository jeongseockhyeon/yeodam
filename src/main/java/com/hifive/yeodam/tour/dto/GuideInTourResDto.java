package com.hifive.yeodam.tour.dto;

import com.hifive.yeodam.seller.entity.Guide;
import lombok.Getter;

@Getter
public class GuideInTourResDto {
    private Long id;

    private String name;
    private String gender;
    private String bio;

    public GuideInTourResDto(Guide guide) {
        this.id = guide.getGuideId();
        this.name = guide.getName();
        this.gender = guide.getGender();
        this.bio = guide.getBio();
    }
}
