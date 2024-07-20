package com.kuit3.rematicserver.common.exception_handler;

import com.kuit3.rematicserver.common.exception.UnauthorizedUserRequestException;
import com.kuit3.rematicserver.common.exception.UserDuplicateEmailException;
import com.kuit3.rematicserver.common.exception.UserDuplicateNicknameException;
import com.kuit3.rematicserver.common.exception.UserNotFoundException;
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
    @ExceptionHandler(UnauthorizedUserRequestException.class)
    public BaseErrorResponse handle_UnauthorizedUserRequestException(Exception e) {
        log.error("[handle_UnauthorizedUserRequestException]", e);
        return new BaseErrorResponse(UNAUTHORIZED_USER_REQUEST);
    }
}