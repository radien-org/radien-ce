/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.radien.api.kernel.messages;

import java.text.MessageFormat;

/**
 * Enum holding system related messages with codes and types for easy
 * distinction
 *
 * @author Marco Weiland
 */
public enum SystemMessages {

    /****************
     * TECHNICAL *
     ***************/
    KERNEL_UNKNOWN("OAF : 00010", Constants.SYS_CAT_SYS, "unknown_error", SystemMessageTypes.TECHNICAL),
	KERNEL_IO_ERROR("OAF : 00020", Constants.SYS_CAT_SYS, "io_problem", SystemMessageTypes.TECHNICAL),
	KERNEL_COMMAND_ERROR("OAF : 00030", Constants.SYS_CAT_SYS, "the_command_to_execute_is_not_valid", SystemMessageTypes.TECHNICAL),
	KERNEL_SESSION_NOT_VALID("OAF : 00040", Constants.SYS_CAT_SYS, "no_user_logged-in", SystemMessageTypes.TECHNICAL),
	KERNEL_ENVIRONMENT_ERROR("OAF : 00050", Constants.SYS_CAT_SYS, "sys.msg.env", SystemMessageTypes.TECHNICAL),
	KERNEL_PROPERTIES_ERROR("OAF : 00060", Constants.SYS_CAT_SYS, "error_reading_system_properties", SystemMessageTypes.TECHNICAL),
	KERNEL_MANIFEST_ERROR("OAF : 00070", Constants.SYS_CAT_SYS, "error_reading_manifest", SystemMessageTypes.TECHNICAL),
	PLUGIN_MANIFEST_ERROR("OAF : 00080", Constants.SYS_CAT_SYS, "error_reading_plugin_manifest", SystemMessageTypes.TECHNICAL),
	KERNEL_PROPERTY_UNAVAILABLE("OAF : 00090", Constants.SYS_CAT_SYS, "property_not_available", SystemMessageTypes.TECHNICAL),
	KERNEL_LOCALE_ERROR("OAF : 00100", Constants.SYS_CAT_SYS, "error_loading_locales", SystemMessageTypes.TECHNICAL),
    KERNEL_UNKNOWN_TEST("OAF : 00110", Constants.SYS_CAT_SYS, "unknown_error_test", SystemMessageTypes.TECHNICAL),

    /****************
     * PROCESS *
     ***************/
    PROC_ACCOUNT_NOT_EXISTING("OAF : 10010", Constants.SYS_CAT_PROC, "the_account_is_not_existing", SystemMessageTypes.BUSINESS),
	PROC_ACCOUNT_EXPIRED("OAF : 10020", Constants.SYS_CAT_PROC, "the_account_is_expired", SystemMessageTypes.BUSINESS),
	PROC_ACCOUNT_ALREADY_ASSOCIATED("OAF : 10030", Constants.SYS_CAT_PROC, "the_account_is_already_linked", SystemMessageTypes.BUSINESS),
	PROC_ACCOUNT_TARGET_CONTEXT_INVALID("OAF : 10040", Constants.SYS_CAT_PROC, "the_account_target_context_is_invalid", SystemMessageTypes.BUSINESS),

    /****************
     * SECURTIY *
     ***************/
    SEC_PASSWORD_POLICY_VIOLATION("OAF : 20010", Constants.SYS_CAT_SEC, "the_password_does_not_apply_to_the_password_policy", SystemMessageTypes.SECURITY),
	SEC_AUTHENTICATION_FAILED("OAF : 20020", Constants.SYS_CAT_SEC, "authentication_incorrect_credentials", SystemMessageTypes.SECURITY),
	SEC_AUTHENTICATION_RESPONSE_FAILED("OAF : 20030", Constants.SYS_CAT_SEC, "authentication_response_failed", SystemMessageTypes.SECURITY),
	SEC_AUTHENTICATION_RESOURCE_EXPIRED("OAF : 20040", Constants.SYS_CAT_SEC, "authentication_resource_expired", SystemMessageTypes.SECURITY),
    ;

    private String errorCode;
    private String catKey;
    private String msgKey;
    private SystemMessageTypes errorType;

    /**
     * System message constructor
     * @param errorCode code for the specific message
     * @param catKey category for the message
     * @param msgKey message unique key
     * @param errorType system message type
     */
    SystemMessages(String errorCode, String catKey, String msgKey, SystemMessageTypes errorType) {
        this.errorCode = errorCode;
        this.catKey = catKey;
        this.msgKey = msgKey;
        this.errorType = errorType;
    }

    /**
     * Format the requested system message
     * @param message to be returned
     * @return the requested system message
     */
    private static String formatMessage(String message) {
        MessageFormat mf = new MessageFormat(message);
        return mf.format(new Object[0]);
    }

    /**
     * Format the requested system message with one argument
     * @param message to be edited
     * @param arg0 to be changed or added
     * @return the system message formatted
     */
    private static String formatMessage(String message, Object arg0) {
        MessageFormat mf = new MessageFormat(message);
        Object[] args = new Object[1];
        args[0] = arg0;
        return mf.format(args);
    }

    /**
     * Format the requested system message with two argument
     * @param message to be edited
     * @param arg0 to be changed or added
     * @param arg1 to be changed or added
     * @return the system message formatted
     */
    private static String formatMessage(String message, Object arg0, Object arg1) {
        MessageFormat mf = new MessageFormat(message);
        Object[] args = new Object[2];
        args[0] = arg0;
        args[1] = arg1;
        return mf.format(args);
    }

    /**
     * Format the requested system message with three argument
     * @param message to be edited
     * @param arg0 to be changed or added
     * @param arg1 to be changed or added
     * @param arg2 to be changed or added
     * @return the system message formatted
     */
    private static String formatMessage(String message, Object arg0, Object arg1, Object arg2) {
        MessageFormat mf = new MessageFormat(message);
        Object[] args = new Object[3];
        args[0] = arg0;
        args[1] = arg1;
        args[2] = arg2;
        return mf.format(args);
    }

    /**
     * Getter for category key
     * @return a string value of the category key value
     */
    public String catKey() {
        return catKey;
    }

    /**
     * Getter for the message key
     * @return a string value of the message key value
     */
    public String msgKey() {
        return msgKey;
    }

    /**
     * Creates a new system message with null values
     * @return a string value of the system message
     */
    public String message() {
        return message(null, null, null);
    }

    /**
     * Creates a system message with one parameter
     * @param arg0 to be added in the system message
     * @return a string value of the system message
     */
    public String message(Object arg0) {
        return message(arg0, null, null);
    }

    /**
     * Creates a system message with two parameters
     * @param arg0 to be added in the system message
     * @param arg1 to be added in the system message
     * @return a string value of the system message
     */
    public String message(Object arg0, Object arg1) {
        return message(arg0, arg1, null);
    }

    /**
     * Creates a system message understanding how many parameters might be needed
     * @param arg0 to be added in the system message
     * @param arg1 to be added in the system message
     * @param arg2 to be added in the system message
     * @return a string value of the system message
     */
    public String message(Object arg0, Object arg1, Object arg2) {
        String prefix = "[" + errorCode + "] : <" + getMessageString(errorType.typeKey()) + "> : ";
        String localizedMessage;
        int replaceCount = 0;
        String message = getMessageString(msgKey);
        replaceCount = calculateReplaceCount(replaceCount, message);

        String missingParameter = "***MISSING***";
        if (arg0 == null) {
            arg0 = missingParameter;
        }
        if (arg1 == null) {
            arg1 = missingParameter;
        }
        if (arg2 == null) {
            arg2 = missingParameter;
        }
        switch (replaceCount) {
            case 1:
                localizedMessage = formatMessage(message, arg0);
                break;
            case 2:
                localizedMessage = formatMessage(message, arg0, arg1);
                break;
            case 3:
                localizedMessage = formatMessage(message, arg0, arg1, arg2);
                break;
            default:
                localizedMessage = formatMessage(message);
                break;
        }
        return prefix + localizedMessage;
    }

    /**
     * Method to count how many parameters are in the system message
     * @param replaceCount counter to understand how many parameters does the system message has
     * @param message to be counted how many parameters has
     * @return the count of the parameters
     */
    public int calculateReplaceCount(int replaceCount, String message) {
        if (message.contains("{0}")) {
            replaceCount++;
            if (message.contains("{1}")) {
                replaceCount++;
                if (message.contains("{2}")) {
                    replaceCount++;
                }
            }
        }
        return replaceCount;
    }

    /**
     * Get system message value message
     * @param messageKey to be found
     * @return the string value of the system message
     */
    private String getMessageString(String messageKey) {
        return errorType.resourceBundle(errorType).getString(messageKey);
    }

    /**
     * Get the system message by the error code value
     * @param errorCode to be found
     * @return the string value of the system message
     */
    public String getMessageByErrorCode(String errorCode) {
        for (SystemMessages message : values()) {
            if (errorCode.matches(message.errorCode)) {
                return message.message();
            }
        }
        return KERNEL_UNKNOWN.message();
    }

    /**
     * System messages category key constants
     */
    private static class Constants {
        static final String SYS_CAT_SYS = "sys.cat.sys";
        static final String SYS_CAT_SEC = "sys.cat.sec";
        static final String SYS_CAT_PROC = "sys.cat.proc";
    }
}
