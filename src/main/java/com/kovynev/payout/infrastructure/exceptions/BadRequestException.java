package com.kovynev.payout.infrastructure.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {
    public final String messageCode;

    public BadRequestException(String messageCode) {
        super(HttpStatus.BAD_REQUEST.value(), messageCode);
        this.messageCode = messageCode;
    }
}
