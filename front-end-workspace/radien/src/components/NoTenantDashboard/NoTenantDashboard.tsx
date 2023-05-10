import React, { useContext, useState } from "react";
import Button from "@cloudscape-design/components/button";
import { Box, Container } from "@cloudscape-design/components";
import TenantRequestModal from "@/components/TenantRequest/TenantRequestModal";
import { RadienContext } from "@/context/RadienContextProvider";

export default function NoTenantDashboard() {
    const { i18n } = useContext(RadienContext);
    const [requestTenant, setRequestTenant] = useState(false);

    return (
        <>
            <TenantRequestModal modalVisible={requestTenant} setModalVisible={setRequestTenant} />

            <Box padding="m">
                <Container>
                    <div className="flex justify-center mb-4">
                        <h2>{i18n?.tenant_request_no_tenant_assigned || "No tenant is assigned to you, please request access to one."}</h2>
                    </div>
                    <div className="flex justify-center">
                        <Button onClick={() => setRequestTenant(true)} variant="primary">
                            {i18n?.tenant_request_button || "Request Tenant"}
                        </Button>
                    </div>
                </Container>
            </Box>
        </>
    );
}
