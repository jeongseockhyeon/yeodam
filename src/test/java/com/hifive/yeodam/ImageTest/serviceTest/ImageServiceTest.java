package com.hifive.yeodam.ImageTest.serviceTest;

import com.hifive.yeodam.image.service.ImageService;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


@Import(MockS3Config.class)
public class ImageServiceTest {

    @Mock
    private ImageService imageService;

    @Mock
    private S3Mock s3Mock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    public void tearDown() {
        s3Mock.stop();
    }

    @Test
    public void uploadTest(){
        //given
        String imageName = "test.png";
        String contentType = "image/png";
        String onlyName = "test";

        MockMultipartFile mockMultipartFile = new MockMultipartFile(onlyName,imageName, contentType,onlyName.getBytes());

        when(imageService.upload(mockMultipartFile)).thenReturn("http://localhost:8001/test.png"); //임시 이미지 url

        //when
        String url = imageService.upload(mockMultipartFile);

        //then
        assertThat(url).contains(onlyName);
        verify(imageService,times(1)).upload(mockMultipartFile);

    }

}
