package com.kuit3.rematicserver.common.exception.jwt.unauthorized;

import com.kuit3.rematicserver.common.response.status.ResponseStatus;

import lombok.Getter;

@Getter
public class JwtMalformedTokenException extends JwtUnauthorizedTokenException {

    private final ResponseStatus exceptionStatus;

    public JwtMalformedTokenException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
        this.exceptionStatus = exceptionStatus;
    }
}