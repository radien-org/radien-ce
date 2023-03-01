import React, { useState } from "react";
import FlashbarComponent from "./FlashbarComponent";
import { RadienContext } from "@/context/RadienContextProvider";
import { FlashbarProps } from "@cloudscape-design/components";
import { UseQueryResult } from "react-query";
import { ActiveTenant, User } from "radien";
import { v4 as uuidv4 } from "uuid";

type RadienProviderProps = {
    children: JSX.Element[];
};
type TestTypeProps = {
    messageType: "success" | "warning" | "info" | "error";
};

const testMessageContent = "test message";
const messageDefaultTimeoutMs = 5000;

const RadienContextProviderMock = ({ messageType }: TestTypeProps) => {
    const [messages, setMessages] = useState<FlashbarProps.MessageDefinition[]>([]);

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
        isLoadingUserInSession: false,
        userInSession: {} as User,
        activeTenant: {} as UseQueryResult<ActiveTenant, Error>,
        i18n: {},
    };

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
    const ReactContextProvider = ({ children }: RadienProviderProps) => {
        return <RadienContext.Provider value={contextValue}>{children}</RadienContext.Provider>;
    };

    const addMessageFromProps = () => {
        switch (messageType) {
            case "success":
                addSuccessMessage(testMessageContent);
                break;
            case "warning":
                addWarningMessage(testMessageContent);
                break;
            case "error":
                addErrorMessage(testMessageContent);
                break;
            case "info":
                addInfoMessage(testMessageContent);
                break;
        }
    };

    return (
        <ReactContextProvider>
            <></>
            <FlashbarComponent />
            <button onClick={() => addMessageFromProps()}>Click me</button>
        </ReactContextProvider>
    );
};
describe("<FlashbarComponent />", () => {
    it("renders", () => {
        cy.mount(<FlashbarComponent />);
    });
    it("displaysSuccess", () => {
        cy.clock();
        cy.mount(<RadienContextProviderMock messageType={"success"}></RadienContextProviderMock>);
        cy.get("ul").should("not.contain.text", "test");
        cy.get("button").click();
        cy.get("ul").should("contain.text", "test");
        cy.tick(messageDefaultTimeoutMs);
        cy.get("ul").should("not.contain.text", "test");
    });
    it("displaysError", () => {
        cy.clock();
        cy.mount(<RadienContextProviderMock messageType={"error"}></RadienContextProviderMock>);
        cy.get("ul").should("not.contain.text", "test");
        cy.get("button").click();
        cy.get("ul").should("contain.text", "test");
        cy.tick(messageDefaultTimeoutMs);
        cy.get("ul").should("contain.text", "test");
        cy.get('button[class^="awsui_dismiss-button"]').as("removeButton").click();
        cy.get("ul").should("not.contain.text", "test");
    });
    it("displaysWarning", () => {
        cy.clock();
        cy.mount(<RadienContextProviderMock messageType={"warning"}></RadienContextProviderMock>);
        cy.get("ul").should("not.contain.text", "test");
        cy.get("button").click();
        cy.get("ul").should("contain.text", "test");
        cy.tick(messageDefaultTimeoutMs);
        cy.get("ul").should("contain.text", "test");
        cy.get('button[class^="awsui_dismiss-button"]').as("removeButton").click();
        cy.get("ul").should("not.contain.text", "test");
    });
    it("displaysInfo", () => {
        cy.clock();
        cy.mount(<RadienContextProviderMock messageType={"info"}></RadienContextProviderMock>);
        cy.get("ul").should("not.contain.text", "test");
        cy.get("button").click();
        cy.get("ul").should("contain.text", "test");
        cy.tick(messageDefaultTimeoutMs);
        cy.get("ul").should("not.contain.text", "test");
    });
});
