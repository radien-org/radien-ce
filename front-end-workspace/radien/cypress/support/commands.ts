/// <reference types="cypress" />
// ***********************************************
// This example commands.ts shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
Cypress.Commands.add("login", () => {
    cy.intercept("/api/auth/session", { fixture: "session.json" }).as("session");
    cy.setCookie("next-auth.session-token", "a valid cookie from your browser session");
});
declare global {
    namespace Cypress {
        interface Chainable {
            login(email: string, password: string): Chainable<void>;
        }
    }
}
export {};
