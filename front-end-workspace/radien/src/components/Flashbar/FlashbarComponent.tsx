import React, { useContext } from "react";
import { Flashbar, FlashbarProps } from "@cloudscape-design/components";
import { RadienContext } from "@/context/RadienContextProvider";
import { v4 as uuidv4 } from "uuid";

interface RadFlashbarProps {
    values: FlashbarProps.MessageDefinition[];
}

export default function FlashbarComponent() {
    const context = useContext(RadienContext);
    return (
        <div className={"flash-bar--container"}>
            <Flashbar items={context.values} />
        </div>
    );
}

export const createMessage = (deleteMessage: Function, message: string, type: FlashbarProps.Type) => {
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
export function handleSuccessMessage(addMessage: Function, deleteMessage: Function, message: string, dismissedAfter: number = 5000) {
    const { uuid, msgObj } = createMessage(deleteMessage, message, "success");
    setTimeout(() => {
        deleteMessage(uuid);
    }, dismissedAfter);
    addMessage(msgObj);
}
export function handleInfoMessage(addMessage: Function, deleteMessage: Function, message: string, dismissedAfter: number = 5000) {
    const { uuid, msgObj } = createMessage(deleteMessage, message, "info");
    setTimeout(() => {
        deleteMessage(uuid);
    }, dismissedAfter);
    addMessage(msgObj);
}
export function handleWarningMessage(addMessage: Function, deleteMessage: Function, message: string) {
    const { msgObj } = createMessage(deleteMessage, message, "warning");
    addMessage(msgObj);
}
export function handleErrorMessage(addMessage: Function, deleteMessage: Function, message: string) {
    const { msgObj } = createMessage(deleteMessage, message, "error");
    addMessage(msgObj);
}
