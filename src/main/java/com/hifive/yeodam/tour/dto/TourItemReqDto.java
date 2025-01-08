package com.hifive.yeodam.tour.dto;

import com.hifive.yeodam.global.validation.ValidFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.hifive.yeodam.global.validation.UploadAllowImageTypeDefine.*;

@Getter
public class TourItemReqDto {

    @NotBlank(message = "여행 상품명은 입력해주세요")
    @Size(max = 25)
    private final String tourName;

    @NotBlank(message = "여행 상품의 설명을 입력해주세요.")
    private final String tourDesc;

    @NotBlank(message = "기간을 입력해주세요.")
    @Size(max = 25)
    private final String tourPeriod;

    @NotBlank(message = "지역을 입력해주세요.")
    @Size(max = 25)
    private final String tourRegion;

    @NotBlank(message = "가격을 입력해주세요.")
    @Size(max = 25)
    private final String tourPrice;

    @NotBlank(message = "최대 인원을 입력해주세요.")
    @Size(max = 25)
    private final String maximum;

    @NotNull(message="테마를 1개 이상 선택해주세요.")
    private final String categoryIdList;

    @NotNull(message = "가이드 1명 이상을 선택해주세요.")
    private final String guideIdList;

    @ValidFile(allowImageTypeDefine = {JPG, PNG, JPEG})
    private final List<MultipartFile> tourImages;

    public TourItemReqDto(String tourName,
                          String tourDesc,
                          String tourPeriod,
                          String tourRegion,
                          String tourPrice,
                          String maximum,
                          String categoryIdList,
                          String guideIdList,
                          List<MultipartFile> tourImages) {
        this.tourName = tourName;
        this.tourDesc = tourDesc;
        this.tourPeriod = tourPeriod;
        this.tourRegion = tourRegion;
        this.tourPrice = tourPrice;
        this.maximum = maximum;
        this.categoryIdList = categoryIdList;
        this.guideIdList = guideIdList;
        this.tourImages = tourImages;
    }
}
