import React, { useMemo, useState } from "react";

import { useUserInSession } from "@/hooks/useUserInSession";
import { FlashbarProps } from "@cloudscape-design/components";
import { ActiveTenant, User } from "radien";
import useActiveTenant from "@/hooks/useActiveTenant";
import { UseQueryResult } from "react-query";
import useI18N from "@/hooks/useI18N";
import { handleErrorMessage, handleInfoMessage, handleSuccessMessage, handleWarningMessage } from "@/components/Flashbar/FlashbarComponent";

export type RadienContextDef = {
    values: FlashbarProps.MessageDefinition[];
    addSuccessMessage: (message: string, dismissedAfter?: number) => void;
    addInfoMessage: (message: string, dismissedAfter?: number) => void;
    addWarningMessage: (message: string) => void;
    addErrorMessage: (message: string) => void;
    isLoadingUserInSession: boolean;
    userInSession: User | undefined;
    activeTenant: UseQueryResult<ActiveTenant, Error>;
    i18n: any;
};

export const RadienContext = React.createContext<RadienContextDef>({
    values: [] as FlashbarProps.MessageDefinition[],
    addSuccessMessage: (message: string, dismissedAfter?: number) => null,
    addInfoMessage: (message: string, dismissedAfter?: number) => null,
    addWarningMessage: (message: string) => null,
    addErrorMessage: (message: string) => null,
    isLoadingUserInSession: true,
    userInSession: {} as User | undefined,
    activeTenant: {} as UseQueryResult<ActiveTenant, Error>,
    i18n: {} as any,
});

type RadienProviderProps = {
    children: JSX.Element[];
};

export default function RadienContextWrapper({ children }: RadienProviderProps) {
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

    const contextValue = useMemo(() => {
        return {
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
    }, [messages, isLoadingUserInSession, radienUser, activeTenant, i18nData]);

    return <RadienContext.Provider value={contextValue}>{children}</RadienContext.Provider>;
}
