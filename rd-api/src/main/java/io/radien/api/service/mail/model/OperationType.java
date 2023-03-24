package io.radien.api.service.mail.model;

public enum OperationType {
    MODIFICATION("rectify", "rectified"),
    DELETION("erase", "erased"),
    RESTRICTION("restrict", "restricted");

    private final String operation, operation_past_simple;

    OperationType(String operation, String operation_past_simple){
        this.operation = operation;
        this.operation_past_simple = operation_past_simple;
    }

    public String getOperation(){
        return operation;
    }

    public String getOperationPasteSimple(){
        return operation_past_simple;
    }
}
