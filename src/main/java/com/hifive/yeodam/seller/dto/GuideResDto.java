package com.hifive.yeodam.seller.dto;

import com.hifive.yeodam.seller.entity.Guide;
import lombok.Getter;

@Getter
public class GuideResDto {
    private final Long id;
    private final String name;
    private final String bio;
    private final String gender;

    public GuideResDto(Guide guide){
        this.id = guide.getGuideId();
        this.name = guide.getName();
        this.bio = guide.getBio();
        this.gender = guide.getGender();
    }
}
