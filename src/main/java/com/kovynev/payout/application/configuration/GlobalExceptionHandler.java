package com.kovynev.payout.application.configuration;

import com.kovynev.payout.application.domain.dto.DtoApiError;
import com.kovynev.payout.infrastructure.exceptions.BadRequestException;
import com.kovynev.payout.infrastructure.exceptions.InternalException;
import com.kovynev.payout.infrastructure.exceptions.IntrumApiFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    public DtoApiError handleBadRequestException(BadRequestException badRequestException) {
        return DtoApiError.builder()
                .message(badRequestException.messageCode)
                .status(BAD_REQUEST)
                .timestamp(now())
                .build();
    }

    @ExceptionHandler(InternalException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public DtoApiError handleInternalException(InternalException internalException) {
        return DtoApiError.builder()
                .message(internalException.getMessage())
                .status(INTERNAL_SERVER_ERROR)
                .timestamp(now())
                .build();
    }

    @ExceptionHandler(IntrumApiFailureException.class)
    @ResponseStatus(BAD_GATEWAY)
    public DtoApiError handleExternalServerException(IntrumApiFailureException externalException) {
        return DtoApiError.builder()
                .message(externalException.getMessage())
                .status(BAD_GATEWAY)
                .timestamp(now())
                .build();
    }
}
