package org.dev.recipe.exception;


import org.springframework.http.HttpStatus;

public class GeminiTimeoutException extends GeminiAIException {

    public GeminiTimeoutException(String message) {
        super(message, HttpStatus.GATEWAY_TIMEOUT, "GEMINI_TIMEOUT_ERROR");
    }
}