package com.kuit3.rematicserver.common.exception;

import com.kuit3.rematicserver.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class UserDormantException extends RuntimeException{

    private final ResponseStatus exceptionStatus;

    public UserDormantException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
