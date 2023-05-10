import React from "react";
import { RoleDetailsView } from "./index";
import { QueryClient, QueryClientProvider } from "react-query";
import { Role } from "radien";

const queryClient = new QueryClient();
const role: Role = {
    id: 1,
    name: "test",
    description: "test",
};

describe("<RoleDetailsView />", () => {
    it("renders", () => {
        // see: https://on.cypress.io/mounting-react
        cy.mount(
            <QueryClientProvider client={queryClient}>
                <RoleDetailsView data={role} />
            </QueryClientProvider>
        );
    });
});
