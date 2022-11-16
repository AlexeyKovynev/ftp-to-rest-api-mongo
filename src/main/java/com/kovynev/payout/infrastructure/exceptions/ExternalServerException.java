package com.kovynev.payout.infrastructure.exceptions;

import org.springframework.http.HttpStatus;

public class ExternalServerException extends ApiException {
    public final String messageCode;

    public ExternalServerException(String messageCode) {
        super(HttpStatus.BAD_GATEWAY.value(), messageCode);
        this.messageCode = messageCode;
    }
}
