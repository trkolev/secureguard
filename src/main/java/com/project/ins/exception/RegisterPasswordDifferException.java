package com.project.ins.exception;

import com.project.ins.web.dto.RegisterRequest;

public class RegisterPasswordDifferException extends RuntimeException {
    private RegisterRequest registerRequest;

    public RegisterPasswordDifferException(String message,  RegisterRequest registerRequest) {
        super(message);
        this.registerRequest = registerRequest;
    }

    public RegisterRequest getRegisterRequest() {
        return registerRequest;
    }
}
