package io.radien.ms.usermanagement.service;

/**
 * Enum containing all operations that can result in a user notification.
 * Each operation has contains four fields:
 * the deverbal noun of the operation, the subject of the notification when this operation is called,
 * the present form of the operation and the past simple version of the operation.
 * The last three fields are keys in the 'language.properties' file.
 */
enum OperationType {
    MODIFICATION("modification", "data_manipulation_modification_subject", "data_manipulation_modification_present", "data_manipulation_modification_past_simple"),
    DELETION("deletion", "data_manipulation_deletion_subject", "data_manipulation_deletion_present", "data_manipulation_deletion_past_simple"),
    RESTRICTION("restriction", "data_manipulation_restriction_subject", "data_manipulation_restriction_present", "data_manipulation_restriction_past_simple");

    private final String deverbal;
    private final String subject;
    private final String present;
    private final String pastSimple;

    OperationType(String deverbal, String subject, String operation, String pastSimple){
        this.deverbal = deverbal;
        this.subject = subject;
        this.present = operation;
        this.pastSimple = pastSimple;
    }

    public String getDeverbal() {
        return deverbal;
    }

    public String getSubject(){
        return subject;
    }

    public String getPresent(){
        return present;
    }

    public String getPastSimple(){
        return pastSimple;
    }
}