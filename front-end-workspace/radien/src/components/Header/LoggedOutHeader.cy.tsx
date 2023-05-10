import React from "react";
import LoggedOutHeader from "./LoggedOutHeader";
import MockRouter from "../../../cypress/support/router";
import { TopNavigationProps } from "@cloudscape-design/components/top-navigation";
import { NextRouter } from "next/router";
import { ButtonDropdownProps } from "@cloudscape-design/components";

const i18nStrings = {
    searchIconAriaLabel: "Search",
    searchDismissIconAriaLabel: "Close search",
    overflowMenuTriggerText: "More",
    overflowMenuTitleText: "All",
    overflowMenuBackIconAriaLabel: "Back",
    overflowMenuDismissIconAriaLabel: "Close menu",
};

describe("<LoggedOutHeader />", () => {
    it("renders", () => {
        const topNavigationProps: TopNavigationProps = {
            identity: {
                href: `/`,
                logo: { src: "/top-navigation/trademark.svg", alt: "ra'di'en" },
            },
            i18nStrings,
        };
        const localeClicked = async (router: NextRouter, event: CustomEvent<ButtonDropdownProps.ItemClickDetails>) => {
            console.log("locale clicked ", event.detail.id);
        };

        cy.viewport(800, 600);
        cy.mount(
            <MockRouter locales={["en", "de"]}>
                <LoggedOutHeader topNavigationProps={topNavigationProps} localeClicked={localeClicked} i18nStrings={i18nStrings} />
            </MockRouter>
        );
    });
    it("changes locale", () => {
        const topNavigationProps: TopNavigationProps = {
            identity: {
                href: `/`,
                logo: { src: "/top-navigation/trademark.svg", alt: "ra'di'en" },
            },
            i18nStrings,
        };

        const spy = cy.spy().as("localeClicked");
        cy.viewport(800, 600);
        cy.mount(
            <MockRouter locales={["en", "de"]}>
                <LoggedOutHeader topNavigationProps={topNavigationProps} localeClicked={spy} i18nStrings={i18nStrings} />
            </MockRouter>
        );
        cy.get("div[class^='awsui_stretch-trigger-height']>button").eq(0).as("changeLocale");
        cy.get("@changeLocale").invoke("attr", "aria-expanded").should("equal", "false");
        cy.get("@changeLocale").click();
        cy.get("div[class^='awsui_dropdown-content']>ul").as("localeSelection");
        cy.get("@localeSelection").get("li").eq(1).as("enSelector").should("have.text", " de ");
        cy.get("@enSelector").click();
        expect(spy.calledOnce);
    });
    it("toggles theme", () => {
        const topNavigationProps: TopNavigationProps = {
            identity: {
                href: `/`,
                logo: { src: "/top-navigation/trademark.svg", alt: "ra'di'en" },
            },
            i18nStrings,
        };

        const spy = cy.spy().as("localeClicked");
        cy.viewport(800, 600);
        cy.mount(
            <MockRouter locales={["en", "de"]}>
                <LoggedOutHeader topNavigationProps={topNavigationProps} localeClicked={spy} i18nStrings={i18nStrings} />
            </MockRouter>
        );
        cy.get("span[class^='awsui_contro']>input").eq(0).as("toggleTheme");
        cy.get("@toggleTheme").click();
        cy.get("html").invoke("attr", "class").should("equal", "awsui-dark-mode");
        cy.get("@toggleTheme").click();
        cy.get("html").invoke("attr", "class").should("equal", "light");
    });
    it("triggers login", () => {
        const topNavigationProps: TopNavigationProps = {
            identity: {
                href: `/`,
                logo: { src: "/top-navigation/trademark.svg", alt: "ra'di'en" },
            },
            i18nStrings,
        };

        const spy = cy.spy().as("localeClicked");
        cy.viewport(800, 600);
        cy.mount(
            <MockRouter locales={["en", "de"]}>
                <LoggedOutHeader topNavigationProps={topNavigationProps} localeClicked={spy} i18nStrings={i18nStrings} />
            </MockRouter>
        );
        Cypress.on("uncaught:exception", (err, runnable) => {
            return false;
        });
        cy.intercept("GET", "/api/auth/providers", { fixture: "providers.json" }).as("providersCall");
        cy.intercept("GET", "/api/auth/csrf", { fixture: "csrf.json" }).as("csrfCall");
        cy.intercept("POST", "/api/auth/signin/keycloak?", { statusCode: 200 }).as("loginCall");
        cy.get("div[class^='awsui_utilities']>div[class^='awsui_utility-wrapper']").eq(0).get("a[aria-label='Log In']").eq(0).as("login");
        cy.get("@login").click();
    });
});
