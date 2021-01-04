package io.radien.ms.usermanagement.client.exceptions;

public enum ErrorCodeMessage {

    /**
     * Business Error Code Messages
     */
    RESOURCE_NOT_FOUND(100, "error.resource.not.found","Resource not found."),
    DUPLICATED_FIELD(101, "error.duplicated.field", "There is more than one resource with the same value for the field: %s"),
    /**
     * System Error Code Messages
     */
    GENERIC_ERROR(500, "error.generic.error", "Generic Error.");

    private final int code;
    private final String key;
    private final String fallBackMessage;

    ErrorCodeMessage(int code, String key, String fallBackMessage) {
        this.code = code;
        this.key=key;
        this.fallBackMessage = fallBackMessage;
    }

    @Override
    public String toString() {
        return "{" +
                "\"code\":" + code +
                ", \"key\":\"" + key + "\"" +
                ", \"message\":\"" + fallBackMessage + "\"" +
                "}";
    }

    public String toString(String... args) {
        String message = String.format(fallBackMessage, args);
        return "{" +
                "\"code\":" + code +
                ", \"key\":\"" + key + "\"" +
                ", \"message\":\"" + message + "\"" +
                "}";
    }
}
