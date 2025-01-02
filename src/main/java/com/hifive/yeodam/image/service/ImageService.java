package com.hifive.yeodam.image.service;

import com.hifive.yeodam.global.exception.CustomErrorCode;
import com.hifive.yeodam.global.exception.CustomException;
import com.hifive.yeodam.image.common.GenerateImageName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RequiredArgsConstructor
@Service
@Slf4j
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
            throw new CustomException(CustomErrorCode.IMG_UPLOAD_EXCEPTION);
        }

        return "https://" + domainName + "/" + saveImageName;
    }

    public void delete(String storePath) {
        String imageKey = storePath.substring(storePath.lastIndexOf("/") + 1);
        log.info("Delete image: " + imageKey);
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(imageKey).build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new CustomException(CustomErrorCode.IMG_DELETE_EXCEPTION);
        }
    }

}
