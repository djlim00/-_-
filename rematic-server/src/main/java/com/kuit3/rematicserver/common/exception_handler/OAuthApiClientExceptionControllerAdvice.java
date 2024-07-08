package com.kuit3.rematicserver.common.exception_handler;

import com.kuit3.rematicserver.common.exception.auth.OAuthApiRequestFailedException;
import com.kuit3.rematicserver.common.exception.jwt.bad_request.JwtBadRequestException;
import com.kuit3.rematicserver.common.exception.jwt.unauthorized.JwtUnauthorizedTokenException;
import com.kuit3.rematicserver.common.response.BaseErrorResponse;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class OAuthApiClientExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(OAuthApiRequestFailedException.class)
    public BaseErrorResponse handle_OAuthApiRequestFailedException(OAuthApiRequestFailedException e) {
        log.error("[handle_OAuthApiRequestFailedException]", e);
        return new BaseErrorResponse(e.getExceptionStatus());
    }

}