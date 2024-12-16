package com.hifive.yeodam.image.common;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/*S3 내 이미지 파일 이름 중복 저장을 막기 위해 UUID 값을 삽입하여 이미지 파일 이름 재지정*/
public class GenerateImageName {
    public static final String FILE_EXTENSION_SEPARATOR = ".";

    public static String generateImageName(MultipartFile image){
        String imageName = image.getOriginalFilename();
        int imageExtensionIndex = imageName.lastIndexOf(FILE_EXTENSION_SEPARATOR);

        /*파일 확장자 반환*/
        String fileExtension = imageName.substring(imageExtensionIndex);

        /*확장자를 제외한 파일이름 반환*/
        String fileName = imageName.substring(0,imageExtensionIndex);

        /*파일업로드 시간 중간에 삽입하여 파일이름 재지정*/
        return "yeodam/" + fileName + "_" + UUID.randomUUID() + fileExtension;

    }


}