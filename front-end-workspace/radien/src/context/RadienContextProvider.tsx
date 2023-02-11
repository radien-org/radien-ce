import React, { useState } from "react";
import { v4 as uuidv4 } from "uuid";

import { useUserInSession } from "@/hooks/useUserInSession";
import { FlashbarProps } from "@cloudscape-design/components";
import { ActiveTenant, Tenant, User } from "radien";
import useActiveTenant from "@/hooks/useActiveTenant";
import { UseQueryResult } from "react-query";
import useI18N from "@/hooks/useI18N";

export const RadienContext = React.createContext({
    values: [] as FlashbarProps.MessageDefinition[],
    addSuccessMessage: (message: string, dismissedAfter?: number) => {},
    addInfoMessage: (message: string, dismissedAfter?: number) => {},
    addWarningMessage: (message: string) => {},
    addErrorMessage: (message: string) => {},
    isLoadingUserInSession: true,
    userInSession: {} as User | undefined,
    activeTenant: {} as UseQueryResult<ActiveTenant, Error>,
    i18n: {} as any,
});

type RadienProviderProps = {
    children: JSX.Element[];
};

export default function RadienProvider({ children }: RadienProviderProps) {
    const [messages, setMessages] = useState<FlashbarProps.MessageDefinition[]>([]);
    const { userInSession: radienUser, isLoadingUserInSession } = useUserInSession();
    const activeTenant: UseQueryResult<ActiveTenant, Error> = useActiveTenant(radienUser?.data.id);
    const { data: i18nData } = useI18N();

    const deleteMessage = (id: string) => {
        setMessages((old: FlashbarProps.MessageDefinition[]) => {
            let newValues = [...old];
            newValues = newValues.filter((value) => value.id !== id);
            return newValues;
        });
    };
    const addMessage = (message: FlashbarProps.MessageDefinition) => {
        setMessages((old: FlashbarProps.MessageDefinition[]) => {
            let newValues = [...old];
            newValues.push(message);
            return newValues;
        });
    };
    const addSuccessMessage = (message: string) => {
        handleSuccessMessage(addMessage, deleteMessage, message);
    };
    const addInfoMessage = (message: string) => {
        handleInfoMessage(addMessage, deleteMessage, message);
    };
    const addWarningMessage = (message: string) => {
        handleWarningMessage(addMessage, deleteMessage, message);
    };
    const addErrorMessage = (message: string) => {
        handleErrorMessage(addMessage, deleteMessage, message);
    };

    const contextValue = {
        values: messages,
        addSuccessMessage,
        addInfoMessage,
        addWarningMessage,
        addErrorMessage,
        isLoadingUserInSession,
        userInSession: radienUser?.data,
        activeTenant,
        i18n: i18nData,
    };

    return <RadienContext.Provider value={contextValue}>{children}</RadienContext.Provider>;
}

/**
 * FLASHBAR RELATED METHODS
 */
const createMessage = (deleteMessage: Function, message: string, type: FlashbarProps.Type) => {
    const uuid = uuidv4();
    const firstSpaceIndex = message.indexOf(" ");
    const msgObj: FlashbarProps.MessageDefinition = {
        type: type,
        dismissible: type === "warning" || type === "error",
        dismissLabel: "Dismiss message",
        onDismiss: () => {
            deleteMessage(uuid);
        },
        content: (
            <>
                <span className={"font-bold"}>{message.substring(0, firstSpaceIndex)}</span>
                {message.substring(firstSpaceIndex)}{" "}
            </>
        ),
        id: uuid,
    };
    return { uuid, msgObj };
};
function handleSuccessMessage(addMessage: Function, deleteMessage: Function, message: string, dismissedAfter: number = 5000) {
    const { uuid, msgObj } = createMessage(deleteMessage, message, "success");
    setTimeout(() => {
        deleteMessage(uuid);
    }, dismissedAfter);
    addMessage(msgObj);
}
function handleInfoMessage(addMessage: Function, deleteMessage: Function, message: string, dismissedAfter: number = 5000) {
    const { uuid, msgObj } = createMessage(deleteMessage, message, "info");
    setTimeout(() => {
        deleteMessage(uuid);
    }, dismissedAfter);
    addMessage(msgObj);
}
function handleWarningMessage(addMessage: Function, deleteMessage: Function, message: string) {
    const { msgObj } = createMessage(deleteMessage, message, "warning");
    addMessage(msgObj);
}
function handleErrorMessage(addMessage: Function, deleteMessage: Function, message: string) {
    const { msgObj } = createMessage(deleteMessage, message, "error");
    addMessage(msgObj);
}
