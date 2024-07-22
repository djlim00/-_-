package com.kuit3.rematicserver.aws;

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

    /*
    이미지 파일을 업로드하는 메소드
     */
    public String uploadFile(MultipartFile file) {
        log.info("S3Uploader::uploadFile()");

        validateImageFile(file);

        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + originalFilename; // 유일한 이름을 만들기 위해서 uuid 사용함

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try(InputStream inputStream = file.getInputStream()){
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new InternalServerErrorException(SERVER_ERROR); // 업로드 중 예외는 서버 내부 에러로 처리
        }

        return getFileUrl(fileName); // s3 url 반환
    }

    /*
    파일을 검증하는 메소드
     */
    private void validateImageFile(MultipartFile image) {
        if(image.isEmpty()){
            throw new S3EmptyFileException(EMPTY_IMAGE_FILE);
        }

        // 추후에 파일 확장자 검증 기능 구현할 수도 있음
//        String filename = image.getOriginalFilename();
//        int lastDotIndex = filename.lastIndexOf(".");
//        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
//        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
//        if(!allowedExtensions.contains(extension)){
//            throw new S3UploadingException(UNSUPPORTED_FILE_EXTENSION);
//        }
    }

    /*
    파일에 해당하는 s3 Url을 반환하는 메소드
     */
    public String getFileUrl(String fileName){
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    /*
    해당하는 파일을 s3에서 삭제하는 메소드
     */
    public void deleteFile(String fileName){
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }
}
