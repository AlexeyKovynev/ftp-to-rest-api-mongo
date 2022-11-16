package com.kovynev.payout.infrastructure.exceptions;

public class IntrumApiFailureException extends RuntimeException {

    public IntrumApiFailureException(String message) {
        super(message);
    }
}
