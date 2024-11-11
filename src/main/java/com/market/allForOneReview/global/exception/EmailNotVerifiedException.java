package com.market.allForOneReview.global.exception;

import org.springframework.security.core.AuthenticationException;

public class EmailNotVerifiedException extends AuthenticationException {
    private final String email;

    public EmailNotVerifiedException(String msg, String email) {
        super(msg);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}