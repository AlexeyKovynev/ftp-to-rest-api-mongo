package com.kovynev.payout.infrastructure.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public abstract class ApiException extends RuntimeException {
    private final Integer code;
    private final String messageCode;
}
