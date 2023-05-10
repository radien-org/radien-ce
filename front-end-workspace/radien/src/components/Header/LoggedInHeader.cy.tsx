import React, { useState } from "react";
import LoggedInHeader from "./LoggedInHeader";
import { TopNavigationProps } from "@cloudscape-design/components/top-navigation";
import MockRouter from "../../../cypress/support/router";
import { QueryClient, QueryClientProvider, UseQueryResult } from "react-query";
import { FlashbarProps } from "@cloudscape-design/components";
import { ActiveTenant, User } from "radien";
import { v4 as uuidv4 } from "uuid";
import { RadienContext } from "@/context/RadienContextProvider";
import userInSession from "../../../cypress/fixtures/userInSession.json";
type RadienProviderProps = {
    children: JSX.Element[];
};

const i18nStrings = {
    searchIconAriaLabel: "Search",
    searchDismissIconAriaLabel: "Close search",
    overflowMenuTriggerText: "More",
    overflowMenuTitleText: "All",
    overflowMenuBackIconAriaLabel: "Back",
    overflowMenuDismissIconAriaLabel: "Close menu",
};

const topNavigationProps: TopNavigationProps = {
    identity: {
        href: `/`,
        logo: { src: "/top-navigation/trademark.svg", alt: "ra'di'en" },
    },
    i18nStrings,
};

const RadienContextProviderMock = () => {
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

    const activeTenant = cy.stub({
        data: {
            userId: 1,
            tenantId: 1,
        },
    });

    const contextValue = {
        values: messages,
        addSuccessMessage,
        addInfoMessage,
        addWarningMessage,
        addErrorMessage,
        isLoadingUserInSession: false,
        userInSession: userInSession as User,
        activeTenant: activeTenant as unknown as UseQueryResult<ActiveTenant, Error>,
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

    const localeClicked = cy.spy().as("localeClicked");
    const queryClient = new QueryClient();

    return (
        <ReactContextProvider>
            <></>
            <QueryClientProvider client={queryClient}>
                <MockRouter>
                    <LoggedInHeader topNavigationProps={topNavigationProps} i18nStrings={i18nStrings} localeClicked={localeClicked} />
                </MockRouter>
            </QueryClientProvider>
        </ReactContextProvider>
    );
};

describe("<LoggedInHeader />", () => {
    beforeEach(() => {
        cy.intercept("/api/role/tenantroleuser/getTenants?userId=1", { fixture: "assignedTenants.json", statusCode: 200 }).as("assignedTenants");
        cy.intercept("/api/role/tenantrolepermission/hasPermission*", {
            statusCode: 200,
        }).as("allPermissions");
        cy.viewport(1920, 1080);
    });
    it("renders", () => {
        cy.viewport(1920, 1080);
        cy.mount(<RadienContextProviderMock></RadienContextProviderMock>);
    });
    it("has user management if permission", () => {
        cy.intercept("/api/role/tenantrolepermission/hasPermission?userId=1&resource=User&action=Read&tenantId=1", {
            body: { data: true },
            statusCode: 200,
        }).as("userManagementPermission");
        cy.mount(<RadienContextProviderMock></RadienContextProviderMock>);
        cy.get("div[class^='awsui_stretch-trigger-height']>button").eq(0).click();
        cy.get("div[class^='awsui_dropdown-content']>ul").as("availableMenus");
        cy.get("@availableMenus").get("li").eq(0).should("have.text", " User Management ");
        cy.get("@availableMenus").get("li").eq(1).should("not.exist");
    });
    it("has permission management if permission", () => {
        cy.intercept("/api/role/tenantrolepermission/hasPermission?userId=1&resource=Permission&action=Read&tenantId=1", {
            body: { data: true },
            statusCode: 200,
        }).as("permissionManagementPermission");
        cy.mount(<RadienContextProviderMock></RadienContextProviderMock>);
        cy.get("div[class^='awsui_stretch-trigger-height']>button").eq(0).click();
        cy.get("div[class^='awsui_dropdown-content']>ul").as("availableMenus");
        cy.get("@availableMenus").get("li").eq(0).should("have.text", " Permission Management ");
        cy.get("@availableMenus").get("li").eq(1).should("not.exist");
    });
    it("has tenant management if permission", () => {
        cy.intercept("/api/role/tenantrolepermission/hasPermission?userId=1&resource=Tenant&action=Read&tenantId=1", {
            body: { data: true },
            statusCode: 200,
        }).as("tenantManagementPermission");
        cy.mount(<RadienContextProviderMock></RadienContextProviderMock>);
        cy.get("div[class^='awsui_stretch-trigger-height']>button").eq(0).click();
        cy.get("div[class^='awsui_dropdown-content']>ul").as("availableMenus");
        cy.get("@availableMenus").get("li").eq(0).should("have.text", " Tenant Management ");
        cy.get("@availableMenus").get("li").eq(1).should("not.exist");
    });
    it("has role management if permission", () => {
        cy.intercept("/api/role/tenantrolepermission/hasPermission?userId=1&resource=Roles&action=Read&tenantId=1", {
            body: { data: true },
            statusCode: 200,
        }).as("roleManagementPermission");
        cy.mount(<RadienContextProviderMock></RadienContextProviderMock>);
        cy.get("div[class^='awsui_stretch-trigger-height']>button").eq(0).click();
        cy.get("div[class^='awsui_dropdown-content']>ul").as("availableMenus");
        cy.get("@availableMenus").get("li").get("p").eq(0).should("have.text", "Role Management");
        cy.get("li[class^='awsui_category']>ul").should("have.text", " Role Management ");
    });
    it("has tenant role management if permission", () => {
        cy.intercept("/api/role/tenantrolepermission/hasPermission?userId=1&resource=Tenant%20Role&action=Read&tenantId=1", {
            body: { data: true },
            statusCode: 200,
        }).as("tenantManagementPermission");
        cy.mount(<RadienContextProviderMock></RadienContextProviderMock>);
        cy.get("div[class^='awsui_stretch-trigger-height']>button").eq(0).click();
        cy.get("div[class^='awsui_dropdown-content']>ul").as("availableMenus");
        cy.get("@availableMenus").get("li").get("p").eq(0).should("have.text", "Role Management");
        cy.get("li[class^='awsui_category']>ul").should("have.text", " Tenant Role Management ");
    });
    it("shows active tenant", () => {
        cy.mount(<RadienContextProviderMock></RadienContextProviderMock>);
        cy.get("div[class^='awsui_stretch-trigger-height']").eq(1).should("have.text", "TEST");
    });
    it("shows available tenants", () => {
        cy.mount(<RadienContextProviderMock></RadienContextProviderMock>);
        cy.get("div[class^='awsui_stretch-trigger-height']").eq(1).get("button").eq(1).click();
        cy.get("div[class^='awsui_dropdown-content']>ul").as("availableTenants");
        cy.get("@availableTenants").get("li").eq(0).should("have.text", " TEST ");
        cy.get("@availableTenants").get("li").eq(1).should("have.text", " TEST 1 ");
    });
    it("shows user full name", () => {
        cy.mount(<RadienContextProviderMock></RadienContextProviderMock>);
        cy.get("div[class^='awsui_stretch-trigger-height']").eq(2).should("have.text", "Test User");
    });
});
