import React, {useState} from "react";
import {v4 as uuidv4} from 'uuid';

import {useUserInSession} from "@/hooks/useUserInSession";
import {FlashbarProps} from "@cloudscape-design/components";
import {ActiveTenant, Tenant, User} from "radien";
import useAvailableTenants from "@/hooks/useAvailableTenants";
import useActiveTenant from "@/hooks/useActiveTenant";
import {UseQueryResult} from "react-query";

export const RadienContext = React.createContext({
    values: [] as FlashbarProps.MessageDefinition[], addSuccessMessage: (message: string, dismissedAfter?: number) => {},
    addInfoMessage: (message: string, dismissedAfter?: number) => {},
    addWarningMessage: (message: string) => {},
    addErrorMessage: (message: string) => {},
    isLoadingUserInSession: true,
    userInSession: {} as User | undefined,
    activeTenant: {} as UseQueryResult<ActiveTenant, Error>,
})

type RadienProviderProps = {
    children: JSX.Element[]
}

export default function RadienProvider({ children }: RadienProviderProps) {
    const [messages, setMessages] = useState<FlashbarProps.MessageDefinition[]>([]);
    const { userInSession: radienUser, isLoadingUserInSession } = useUserInSession();
    const activeTenant: UseQueryResult<ActiveTenant, Error> = useActiveTenant(radienUser?.data.id);
    const addSuccessMessage = (message: string) => {
        handleSuccessMessage(messages, setMessages, message);
    }
    const addInfoMessage = (message: string) => {
        handleInfoMessage(messages, setMessages, message);
    }
    const addWarningMessage = (message: string) => {
        handleWarningMessage(messages, setMessages, message);
    }
    const addErrorMessage = (message: string) => {
        handleErrorMessage(messages, setMessages, message);
    }

    const contextValue = {
        values: messages,
        addSuccessMessage,
        addInfoMessage,
        addWarningMessage,
        addErrorMessage,
        isLoadingUserInSession,
        userInSession: radienUser?.data,
        activeTenant,
    }

    return (
        <RadienContext.Provider value={contextValue} >
            {children}
        </RadienContext.Provider>
    )
}

/**
 * FLASHBAR RELATED METHODS
 */
const createMessage = (values: FlashbarProps.MessageDefinition[], valueSetter: Function, message: string, type: FlashbarProps.Type) => {
    const uuid = uuidv4();
    const firstSpaceIndex = message.indexOf(" ");
    const msgObj: FlashbarProps.MessageDefinition = {
        type: type,
        dismissible: type === 'warning' || type === 'error',
        dismissLabel: 'Dismiss message',
        onDismiss: () => {valueSetter(values.splice(values.findIndex(m => m.id === uuid), 1))},
        content: (<><span className={"font-bold"}>{message.substring(0, firstSpaceIndex)}</span>{message.substring(firstSpaceIndex)} </>),
        id: uuid,
    };
    return {uuid, msgObj};
}
function handleSuccessMessage(values: FlashbarProps.MessageDefinition[], valueSetter: Function, message: string, dismissedAfter: number = 5000) {
    const {uuid, msgObj} = createMessage(values, valueSetter, message, "success");
    setTimeout(() => {
        valueSetter(values.splice(values.findIndex(m => m.id === uuid), 1))
    }, dismissedAfter)
    valueSetter([...values, msgObj]);
}
function handleInfoMessage(values: FlashbarProps.MessageDefinition[], valueSetter: Function, message: string, dismissedAfter: number = 5000) {
    const {uuid, msgObj} = createMessage(values, valueSetter, message, "info");
    setTimeout(() => {
        valueSetter(values.splice(values.findIndex(m => m.id === uuid), 1))
    }, dismissedAfter)
    valueSetter([...values, msgObj]);
}
function handleWarningMessage(values: FlashbarProps.MessageDefinition[], valueSetter: Function, message: string) {
    const {uuid, msgObj} = createMessage(values, valueSetter, message, "warning");
    valueSetter([...values, msgObj]);
}
function handleErrorMessage(values: FlashbarProps.MessageDefinition[], valueSetter: Function, message: string) {
    const {uuid, msgObj} = createMessage(values, valueSetter, message, "error");
    valueSetter([...values, msgObj]);
}