package com.kuit3.rematicserver.common.exception_handler;

import com.kuit3.rematicserver.common.exception.*;
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
public class PostExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PostNotFoundException.class)
    public BaseErrorResponse handle_PostNotFoundException(Exception e) {
        log.error("[handle_PostNotFoundException]", e);
        return new BaseErrorResponse(POST_NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidReporterUserIdException.class)
    public BaseErrorResponse handle_InvalidReporterUserIdException(Exception e) {
        log.error("[handle_InvalidReporterUserIdException]", e);
        return new BaseErrorResponse(INVALID_REPORTER_USER_ID);
    }
}