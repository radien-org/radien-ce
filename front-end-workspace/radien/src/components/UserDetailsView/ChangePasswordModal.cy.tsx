import React from "react";
import ChangePasswordModal from "./ChangePasswordModal";
import { QueryClient, QueryClientProvider, useQuery } from "react-query";

const queryClient = new QueryClient();

let modalVisible = true;
const setModalVisible = (value: boolean) => {
    return (modalVisible = value);
};

describe("<ChangePasswordModal />", () => {
    it("password miss requirements", () => {
        cy.mount(
            <QueryClientProvider client={queryClient}>
                <ChangePasswordModal modalVisible={true} setModalVisible={setModalVisible} />
            </QueryClientProvider>
        );
        cy.get('input[name="newPasswordInput"]').type("test");
        cy.get('input[name="confirmNewPasswordInput"]').type("test");
        cy.get("#submitBtn").click();
        cy.get(".awsui_error__message_14mhv_veozk_236");
        cy.get('input[name="newPasswordInput"]').type("Password123#");
        cy.get('input[name="confirmNewPasswordInput"]').type("Password123#");
        cy.get(".awsui_error__message_14mhv_veozk_236").should("not.exist");
    });
    it("passwords dont match", () => {
        cy.mount(
            <QueryClientProvider client={queryClient}>
                <ChangePasswordModal modalVisible={true} setModalVisible={setModalVisible} />
            </QueryClientProvider>
        );
        cy.get('input[name="newPasswordInput"]').type("Password123#");
        cy.get('input[name="confirmNewPasswordInput"]').type("123#Password");
        cy.get("#submitBtn").click();
        cy.get(".awsui_error__message_14mhv_veozk_236").should("have.text", "Passwords don't match");
    });
    it("wrong old password", () => {
        cy.mount(
            <QueryClientProvider client={queryClient}>
                <ChangePasswordModal modalVisible={true} setModalVisible={setModalVisible} />
             </QueryClientProvider>
        );
        cy.get('input[name="newPasswordInput"]').type("Password123#");
        cy.get('input[name="confirmNewPasswordInput"]').type("Password123#");
        cy.get('input[name="oldPasswordInput"]').type("123");
        cy.get("#submitBtn").click();
        cy.get(".awsui_error__message_14mhv_veozk_236").should("have.text", "You got the old password wrong");
    });
});
