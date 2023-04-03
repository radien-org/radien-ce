import React from "react";
import UserDetailsView from "./UserDetailsView";
import { QueryClient, QueryClientProvider } from "react-query";
import { User } from "radien";

const queryClient = new QueryClient();

const user: User = {
    id: 1,
    firstname: "John",
    lastname: "Doe",
    userEmail: "",
    logon: "jdoe",
    sub: "1234567890",
    mobileNumber: "1234567890",
    delegatedCreation: false,
    enabled: true,
    createDate: new Date(),
    lastUpdate: new Date(),
    terminationDate: new Date(),
};

describe("<UserDetailsView />", () => {
    it("renders", () => {
        // see: https://on.cypress.io/mounting-react
        cy.mount(
            <QueryClientProvider client={queryClient}>
                <UserDetailsView data={user} />
            </QueryClientProvider>
        );
    });
});
