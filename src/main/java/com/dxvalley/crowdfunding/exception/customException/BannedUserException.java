package com.dxvalley.crowdfunding.exception.customException;

import org.springframework.security.core.AuthenticationException;

public class BannedUserException extends AuthenticationException {

    public BannedUserException(String message) {
        super(message);
    }
}
