package com.academy.fintech.origination.application.exception;

import lombok.Getter;

@Getter
public class ApplicationAlreadyExistsException extends ApplicationException {

    private final static String message = "This application already exists. You can check its id in trailers.";
    private final String existedApplicationId;

    public ApplicationAlreadyExistsException(String existedApplicationId) {
        super(message);
        this.existedApplicationId = existedApplicationId;
    }
}
