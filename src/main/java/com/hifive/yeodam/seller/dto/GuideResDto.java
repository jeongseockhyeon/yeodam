package com.hifive.yeodam.seller.dto;

import com.hifive.yeodam.seller.entity.Guide;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class GuideResDto {
    private final Long id;
    private final String name;
    private final LocalDate birth;
    private final String gender;
    private final String phone;
    private final String bio;

    public GuideResDto(Guide guide){
        this.id = guide.getGuideId();
        this.name = guide.getName();
        this.birth = guide.getBirth();
        this.gender = guide.getGender();
        this.phone = guide.getPhone();
        this.bio = guide.getBio();
    }
}
