import React, {useState} from "react";
import {v4 as uuidv4} from 'uuid';
import {FlashbarProps} from "@cloudscape-design/components";


export const FlashbarContext = React.createContext({
        values: [] as FlashbarProps.MessageDefinition[],
        addSuccessMessage: (message: string, dismissedAfter?: number) => {},
        addInfoMessage: (message: string, dismissedAfter?: number) => {},
        addWarningMessage: (message: string) => {},
        addErrorMessage: (message: string) => {},
    });

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
export function handleSuccessMessage(values: FlashbarProps.MessageDefinition[], valueSetter: Function, message: string, dismissedAfter: number = 5000) {
        const {uuid, msgObj} = createMessage(values, valueSetter, message, "success");
        setTimeout(() => {
                valueSetter(values.splice(values.findIndex(m => m.id === uuid), 1))
        }, dismissedAfter)
        valueSetter([...values, msgObj]);
}
export function handleInfoMessage(values: FlashbarProps.MessageDefinition[], valueSetter: Function, message: string, dismissedAfter: number = 5000) {
        const {uuid, msgObj} = createMessage(values, valueSetter, message, "info");
        setTimeout(() => {
                valueSetter(values.splice(values.findIndex(m => m.id === uuid), 1))
        }, dismissedAfter)
        valueSetter([...values, msgObj]);
}
export function handleWarningMessage(values: FlashbarProps.MessageDefinition[], valueSetter: Function, message: string) {
        const {uuid, msgObj} = createMessage(values, valueSetter, message, "warning");
        valueSetter([...values, msgObj]);
}
export function handleErrorMessage(values: FlashbarProps.MessageDefinition[], valueSetter: Function, message: string) {
        const {uuid, msgObj} = createMessage(values, valueSetter, message, "error");
        valueSetter([...values, msgObj]);
}