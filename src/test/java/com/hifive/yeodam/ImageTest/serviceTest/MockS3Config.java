package com.hifive.yeodam.ImageTest.serviceTest;

import com.hifive.yeodam.global.config.S3Config;
import io.findify.s3mock.S3Mock;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentials;

@Configuration
public class MockS3Config extends S3Config {
    @Bean
    public S3Mock s3Mock() {
        return new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
    }

    @Bean
    public AwsCredentials awsCredential(){
        return Mockito.mock(AwsCredentials.class);
    }

}
