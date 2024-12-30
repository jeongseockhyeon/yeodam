package com.hifive.yeodam.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class CustomFileValidator implements ConstraintValidator<ValidFile, List<MultipartFile>> {

    @Override
    public boolean isValid(List<MultipartFile> multipartFiles, ConstraintValidatorContext context) {
        // null 또는 빈 리스트인지 확인
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("사진 한장 이상을 업로드해주세요.").addConstraintViolation();
            return false;
        }

        // 각 파일이 빈 파일인지 확인
        for (MultipartFile file : multipartFiles) {
            if (file == null || file.isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("업로드 사진이 존재하지 않습니다.").addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
