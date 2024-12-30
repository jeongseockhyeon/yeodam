package com.hifive.yeodam.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.lang3.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
public class CustomFileValidator implements ConstraintValidator<ValidFile, List<MultipartFile>> {
    private ValidFile validFile;

    // 허용되지 않는 특수문자 정의
    private static final Pattern INVALID_CHARACTERS_PATTERN = Pattern.compile("[%$#@!^&*()+=\\[\\]{};:'\"|<>?,]");

    @Override
    public void initialize(ValidFile validFile) {
        this.validFile = validFile;
    }

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
            //사진 용량 검증
            try {
                int targetByte = file.getBytes().length;
                if (targetByte == 0) {
                    context.buildConstraintViolationWithTemplate("사진의 용량이 0 byte입니다.").addConstraintViolation();
                    return false;
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                context.buildConstraintViolationWithTemplate("사진의 용량 확인 중 에러가 발생했습니다.").addConstraintViolation();
                return false;
            }

            //사진 이름 검증
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("사진 이름이 유효하지 않습니다.").addConstraintViolation();
                return false;
            }

            // 특수문자 검증
            if (INVALID_CHARACTERS_PATTERN.matcher(originalFileName).find()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "사진 이름에 허용되지 않는 특수문자가 포함되어 있습니다: " + originalFileName
                ).addConstraintViolation();
                return false;
            }


            final UploadAllowImageTypeDefine[] uploadAllowImageTypeDefines = validFile.allowImageTypeDefine();
            //파일 확장자명
            final String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            //파일 mimeType
            final String detectedMimeType = this.getMimeTypeByTika(file);
            //허용 확장자명 배열
            String[] allowExtLowerCaseArray = Arrays.stream(uploadAllowImageTypeDefines)
                    .map(UploadAllowImageTypeDefine::getFileExtensionLowerCase)
                    .toArray(String[]::new);

            //허용 MimeType
            String[] allowMimeTypes = Arrays.stream(uploadAllowImageTypeDefines)
                    .map(UploadAllowImageTypeDefine::getFileMimeType)
                    .toArray(String[]::new);

            if(!ArrayUtils.contains(allowExtLowerCaseArray, Objects.requireNonNull(fileExtension).toLowerCase())){
                String contextString = "혀용되지 않는 확장자입니다. 다음 확장자만 지원합니다." +
                        ": " +
                        ArrayUtils.toString(allowExtLowerCaseArray);
                context.buildConstraintViolationWithTemplate(contextString).addConstraintViolation();
                return false;
            }
            if(!ArrayUtils.contains(allowMimeTypes, detectedMimeType)){
                String contextString = "확장자 변조 파일은 허용되지 않습니다." +
                        ": " +
                        ArrayUtils.toString(allowMimeTypes);
                context.buildConstraintViolationWithTemplate(contextString).addConstraintViolation();
                return false;
            }
        }
        return true;
    }

    private String getMimeTypeByTika(MultipartFile file) {

        try {

            Tika tika = new Tika();
            String mimeType = tika.detect(file.getInputStream());
            log.debug("업로드 요청된 파일 {}의 mimeType:{}", file.getOriginalFilename(), mimeType);

            return mimeType;

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
