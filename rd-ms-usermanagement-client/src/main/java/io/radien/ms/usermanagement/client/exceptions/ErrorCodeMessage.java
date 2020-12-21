package io.radien.ms.usermanagement.client.exceptions;

public enum ErrorCodeMessage {

    // Bussiness Logic
    RESOURCE_NOT_FOUND(100, "Resource not found."),
    DUPLICATED_EMAIL(101, "There is more than one user with the same Email."),

    // System Error
    GENERIC_ERROR(500, "Generic Error.");

    private final int code;
    private final String message;

    ErrorCodeMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "{" +
                "\"code\":" + code +
                ", \"message\":\"" + message + "\"" +
                "}";
    }
}
