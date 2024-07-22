package com.kuit3.rematicserver.common.exception_handler;

import com.kuit3.rematicserver.common.exception.S3EmptyFileException;
import com.kuit3.rematicserver.common.exception.S3UploadingFailureException;
import com.kuit3.rematicserver.common.response.BaseErrorResponse;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.*;


@Slf4j
@Priority(0)
@RestControllerAdvice
public class S3UploadingExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(S3UploadingFailureException.class)
    public BaseErrorResponse handle_S3UploadingException(Exception e) {
        log.error("[handle_S3UploadingException]", e);
        return new BaseErrorResponse(S3_UPLOADING_FAILED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(S3EmptyFileException.class)
    public BaseErrorResponse handle_S3EmptyFileException(Exception e) {
        log.error("[handle_S3EmptyFileException]", e);
        return new BaseErrorResponse(EMPTY_IMAGE_FILE);
    }
}