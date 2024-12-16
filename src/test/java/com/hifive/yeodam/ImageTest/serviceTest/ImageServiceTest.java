package com.hifive.yeodam.ImageTest.serviceTest;

import com.hifive.yeodam.image.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
public class ImageServiceTest {

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
