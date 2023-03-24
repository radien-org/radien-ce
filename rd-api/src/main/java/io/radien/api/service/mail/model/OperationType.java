package io.radien.api.service.mail.model;

public enum OperationType {
    MODIFICATION("rectify"),
    DELETION("erase"),
    RESTRICTION("restrict");

    private String operation;

    OperationType(final String operation){
        this.operation = operation;
    }

    public String getOperation(){
        return operation;
    }

}
