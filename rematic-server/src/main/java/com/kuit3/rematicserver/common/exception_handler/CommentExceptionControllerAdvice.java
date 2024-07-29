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
import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.PARENT_COMMENT_NOT_EXISTS;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class CommentExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserCommentException.class)
    public BaseErrorResponse handle_UserCommentException(Exception e) {
        log.error("[handle_UserCommentException]", e);
        return new BaseErrorResponse(COMMENT_NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CommentNotFoundException.class)
    public BaseErrorResponse handle_CommentNotFoundException(Exception e) {
        log.error("[handle_CommentNotFoundException]", e);
        return new BaseErrorResponse(PARENT_COMMENT_NOT_EXISTS);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CommentImageDuplicateException.class)
    public BaseErrorResponse handle_commentImageDuplicateException(Exception e) {
        log.error("[handle_PostNotFoundException]", e);
        return new BaseErrorResponse(IMAGE_ALREADY_EXISTS);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserMisMatchCommentException.class)
    public BaseErrorResponse handle_UserMisMatchCommentException(Exception e) {
        log.error("[handle_UserMisMatchCommentException]", e);
        return new BaseErrorResponse(USER_COMMENT_MISMATCH);
    }
}
