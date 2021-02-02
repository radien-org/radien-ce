package io.radien.ms.permissionmanagement.model;

public class AssociationStatus {
    private final boolean successful;
    private String message;

    public AssociationStatus() {
        this(true, "");
    }

    public AssociationStatus(boolean success, String message) {
        this.successful = success;
        this.message = message;
    }

    public AssociationStatus(boolean status) {
        this.successful = status;
    }

    public boolean isOK() {
        return successful;
    }

    public String getMessage() {
        return message;
    }
}
