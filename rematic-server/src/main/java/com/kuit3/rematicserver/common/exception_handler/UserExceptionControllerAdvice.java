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
public class UserExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotFoundException.class)
    public BaseErrorResponse handle_UserNotFoundException(Exception e) {
        log.error("[handle_UserNotFoundException]", e);
        return new BaseErrorResponse(USER_NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserDuplicateNicknameException.class)
    public BaseErrorResponse handle_UserDuplicateNicknameException(Exception e) {
        log.error("[handle_UserDuplicateNicknameException]", e);
        return new BaseErrorResponse(DUPLICATE_NICKNAME);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserDuplicateEmailException.class)
    public BaseErrorResponse handle_UserDuplicateEmailException(Exception e) {
        log.error("[handle_UserDuplicateEmailException]", e);
        return new BaseErrorResponse(DUPLICATE_EMAIL);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserDormantException.class)
    public BaseErrorResponse handle_UserStatusDormatException(Exception e) {
        log.error("[handle_UserStatusDormantException]", e);
        return new BaseErrorResponse(USER_DORMANT_STATUS);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserKeywordException.class)
    public BaseErrorResponse handle_UserKeywordException(Exception e) {
        log.error("[handle_UserKeywordException]", e);
        return new BaseErrorResponse(USER_KEYWORD_NOT_FOUND);
    }
}