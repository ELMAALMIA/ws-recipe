package org.dev.recipe.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GeminiAIException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public GeminiAIException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errorCode = "GEMINI_ERROR";
    }

    public GeminiAIException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}