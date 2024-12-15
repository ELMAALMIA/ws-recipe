package org.dev.recipe.exception;


import org.springframework.http.HttpStatus;

public class GeminiAuthenticationException extends GeminiAIException {

    public GeminiAuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "GEMINI_AUTH_ERROR");
    }
}