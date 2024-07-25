package com.kuit3.rematicserver.common.exception;

import com.kuit3.rematicserver.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class S3FileNumberLimitExceededException extends RuntimeException {

    private final ResponseStatus exceptionStatus;

    public S3FileNumberLimitExceededException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}