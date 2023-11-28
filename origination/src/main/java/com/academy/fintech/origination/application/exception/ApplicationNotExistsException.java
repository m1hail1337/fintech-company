package com.academy.fintech.origination.application.exception;

import lombok.Getter;

@Getter
public class ApplicationNotExistsException extends ApplicationException {

    private static final String message = "Application with such id not exist, then it can't be canceled.";

    private final String notExistedApplicationId;

    public ApplicationNotExistsException(String applicationId) {
        super(message);
        this.notExistedApplicationId = applicationId;
    }
}
