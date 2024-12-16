package com.hifive.yeodam.image.service;

import com.hifive.yeodam.image.common.GenerateImageName;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@RequiredArgsConstructor
@Service
public class ImageService {
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.cloudfront.domainName}")
    private String domainName;

    /*s3 이미지 파일 업로드*/
    public String upload(MultipartFile image) {

        String saveImageName = GenerateImageName.generateImageName(image);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .contentType(image.getContentType())
                    .contentLength(image.getSize())
                    .key(saveImageName)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(image.getBytes()));
        } catch (Exception e) {
            //커스텀 예외 처리 할 것
        }

        return "https://" + domainName + "/" + saveImageName;
    }
}
