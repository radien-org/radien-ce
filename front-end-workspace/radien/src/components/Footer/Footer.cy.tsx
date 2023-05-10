import React from "react";
import Footer from "./Footer";
import MockRouter from "../../../cypress/support/router";

describe("<Footer />", () => {
    it("renders", () => {
        cy.mount(
            <MockRouter>
                <Footer />
            </MockRouter>
        );
        cy.get("ul>li").eq(0).should("have.text", "About");
        cy.get("ul>li").eq(1).should("have.text", "Privacy Policy");
        cy.get("ul>li").eq(2).should("have.text", "Licensing");
        cy.get("ul>li").eq(3).should("have.text", "Contact");
    });
    it("should have About href", () => {
        cy.mount(
            <MockRouter locale={"de"}>
                <Footer />
            </MockRouter>
        );
        cy.get("ul>li").eq(0).should("have.text", "About").get("a").invoke("attr", "href").should("equal", "/de");
    });
    it("should have Privacy Policy href", () => {
        cy.mount(
            <MockRouter locale={"de"}>
                <Footer />
            </MockRouter>
        );
        cy.get("ul>li").eq(1).should("have.text", "Privacy Policy").get("a").invoke("attr", "href").should("equal", "/de");
    });
    it("should have Licensing href", () => {
        cy.mount(
            <MockRouter locale={"de"}>
                <Footer />
            </MockRouter>
        );
        cy.get("ul>li").eq(2).should("have.text", "Licensing").get("a").invoke("attr", "href").should("equal", "/de");
    });
    it("should have Contact href", () => {
        cy.mount(
            <MockRouter locale={"de"}>
                <Footer />
            </MockRouter>
        );
        cy.get("ul>li").eq(3).should("have.text", "Contact").get("a").invoke("attr", "href").should("equal", "/de");
    });
});
