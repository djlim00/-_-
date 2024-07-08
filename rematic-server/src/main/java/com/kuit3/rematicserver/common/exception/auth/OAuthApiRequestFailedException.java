package com.kuit3.rematicserver.common.exception.auth;

import com.kuit3.rematicserver.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class OAuthApiRequestFailedException extends RuntimeException {

    private final ResponseStatus exceptionStatus;

    public OAuthApiRequestFailedException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}