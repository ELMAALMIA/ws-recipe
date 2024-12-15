package org.dev.recipe.exception;

import org.springframework.http.HttpStatus;

public class GeminiRequestException extends GeminiAIException {

    public GeminiRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "GEMINI_REQUEST_ERROR");
    }
}