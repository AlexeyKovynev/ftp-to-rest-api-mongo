package com.kovynev.payout.infrastructure.exceptions;

import org.springframework.http.HttpStatus;

public class InternalException extends ApiException {
    public final String messageCode;

    public InternalException(String messageCode) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), messageCode);
        this.messageCode = messageCode;
    }
}
