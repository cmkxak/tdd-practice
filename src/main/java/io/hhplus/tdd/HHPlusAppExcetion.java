package io.hhplus.tdd;

import lombok.Getter;

@Getter
public class HHPlusAppExcetion extends RuntimeException {

    private final ErrorResponse errorResponse;

    public HHPlusAppExcetion(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public HHPlusAppExcetion(String message, ErrorResponse errorResponse) {
        super(message);
        this.errorResponse = errorResponse;
    }

    public HHPlusAppExcetion(String message, Throwable cause, ErrorResponse errorResponse) {
        super(message, cause);
        this.errorResponse = errorResponse;
    }

    public HHPlusAppExcetion(Throwable cause, ErrorResponse errorResponse) {
        super(cause);
        this.errorResponse = errorResponse;
    }

    public HHPlusAppExcetion(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorResponse errorResponse) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorResponse = errorResponse;
    }
}
