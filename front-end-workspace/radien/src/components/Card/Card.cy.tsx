import React from "react";
import Card from "./Card";

const testTitle = "Title";
const testDescription = "Description";
const testHref = "/a/href";
const testLocale = "en";
describe("<Card />", () => {
    it("renders", () => {
        cy.mount(<Card title={testTitle} description={testDescription} href={testHref} />);
        cy.get("a").invoke("attr", "href").should("equal", testHref);
        cy.get("header").should("have.text", testTitle);
        cy.get("footer").should("have.text", testDescription);
    });
    it("includesLocale", () => {
        cy.mount(<Card title={testTitle} description={testDescription} href={testHref} locale={testLocale} />);
        cy.get("a").invoke("attr", "href").should("equal", `/${testLocale}${testHref}`);
        cy.get("header").should("have.text", testTitle);
        cy.get("footer").should("have.text", testDescription);
    });
});
