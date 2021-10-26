/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.rd.microservice.api.messages;

import java.text.MessageFormat;

/**
 * Enum holding system related messages with codes and types for easy
 * distinction
 *
 * @author Rajesh Gavvala
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
    KERNEL_UNKNOWN_TEST("OAF : 00110", Constants.SYS_CAT_SYS, "unknown_error_test", SystemMessageTypes.TECHNICAL);


    private String errorCode;
    private String catKey;
    private String msgKey;
    private SystemMessageTypes errorType;

    SystemMessages(String errorCode, String catKey, String msgKey, SystemMessageTypes errorType) {
        this.errorCode = errorCode;
        this.catKey = catKey;
        this.msgKey = msgKey;
        this.errorType = errorType;
    }

    private static String formatMessage(String message) {
        MessageFormat mf = new MessageFormat(message);
        return mf.format(new Object[0]);
    }

    private static String formatMessage(String message, Object arg0) {
        MessageFormat mf = new MessageFormat(message);
        Object[] args = new Object[1];
        args[0] = arg0;
        return mf.format(args);
    }

    private static String formatMessage(String message, Object arg0, Object arg1) {
        MessageFormat mf = new MessageFormat(message);
        Object[] args = new Object[2];
        args[0] = arg0;
        args[1] = arg1;
        return mf.format(args);
    }

    private static String formatMessage(String message, Object arg0, Object arg1, Object arg2) {
        MessageFormat mf = new MessageFormat(message);
        Object[] args = new Object[3];
        args[0] = arg0;
        args[1] = arg1;
        args[2] = arg2;
        return mf.format(args);
    }

    public String catKey() {
        return catKey;
    }

    public String msgKey() {
        return msgKey;
    }

    public String message() {
        return message(null, null, null);
    }

    public String message(Object arg0) {
        return message(arg0, null, null);
    }

    public String message(Object arg0, Object arg1) {
        return message(arg0, arg1, null);
    }

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

    private String getMessageString(String messageKey) {
        return errorType.resourceBundle(errorType).getString(messageKey);
    }

    public String getMessageByErrorCode(String errorCode) {
        for (SystemMessages message : values()) {
            if (errorCode.matches(message.errorCode)) {
                return message.message();
            }
        }
        return KERNEL_UNKNOWN.message();
    }

    private static class Constants {
        static final String SYS_CAT_SYS = "sys.cat.sys";
    }
}
