package com.maksimkaxxl.usermanagementapi.exceptions;

public class UserUnderAgeException extends RuntimeException {
    public UserUnderAgeException(String message) {
        super(message);
    }
}