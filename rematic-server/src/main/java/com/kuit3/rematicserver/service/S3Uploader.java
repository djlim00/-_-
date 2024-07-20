package com.kuit3.rematicserver.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kuit3.rematicserver.common.exception.InternalServerErrorException;
import com.kuit3.rematicserver.common.exception.S3EmptyFileException;
import com.kuit3.rematicserver.common.exception.S3UploadingFailureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    public String uploadFile(MultipartFile file) {
        log.info("S3Uploader::uploadFile()");

        validateImageFile(file);

        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + originalFilename;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try(InputStream inputStream = file.getInputStream()){
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new InternalServerErrorException(SERVER_ERROR);
        }

        return getFileUrl(fileName);
    }

    private void validateImageFile(MultipartFile image) {
        if(image.isEmpty()){
            throw new S3EmptyFileException(EMPTY_IMAGE_FILE);
        }

//        String filename = image.getOriginalFilename();
//        int lastDotIndex = filename.lastIndexOf(".");
//        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
//        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
//        if(!allowedExtensions.contains(extension)){
//            throw new S3UploadingException(UNSUPPORTED_FILE_EXTENSION);
//        }
    }

    public String getFileUrl(String fileName){
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    public void deleteFile(String fileName){
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }
}
