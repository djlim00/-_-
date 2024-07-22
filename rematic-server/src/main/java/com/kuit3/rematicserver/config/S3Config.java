package com.kuit3.rematicserver.config;

import com.amazonaws.auth.AWS3Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    @Value("${cloud.aws.s3.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.s3.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.s3.region.static}")
    private String region;

    @Bean
    public AmazonS3 amazonS3Client(){
        AWSCredentials credetials = new BasicAWSCredentials(accessKey, secretKey);

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new StaticCredentialsProvider(credetials))
                .withRegion(region)
                .build();
    }
}
