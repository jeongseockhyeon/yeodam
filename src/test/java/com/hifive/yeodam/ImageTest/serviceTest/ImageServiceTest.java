package com.hifive.yeodam.ImageTest.serviceTest;

import com.hifive.yeodam.ImageTest.config.S3MockConfig;
import com.hifive.yeodam.image.service.ImageService;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@Import(S3MockConfig.class)
@SpringBootTest
public class ImageServiceTest {

    @Autowired
    private S3Mock s3Mock;

    @Autowired
    private ImageService imageService;

    @Test
    public void uploadTest(){
        //given
        String imageName = "test.png";
        String contentType = "image/png";
        String onlyName = "test";

        MockMultipartFile mockMultipartFile = new MockMultipartFile(onlyName,imageName, contentType,onlyName.getBytes());

        //when
        String url = imageService.upload(mockMultipartFile);

        //then
        assertThat(url).contains(onlyName);
    }


}
