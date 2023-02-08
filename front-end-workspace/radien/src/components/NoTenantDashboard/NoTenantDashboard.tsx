import React, {useState} from 'react';
import Button from "@cloudscape-design/components/button";
import {Box, Container} from "@cloudscape-design/components";
import TenantRequest from "@/components/TenantRequest/TenantRequest";

//TODO: ADD I18N
export default function NoTenantDashboard() {

    const [ requestTenant, setRequestTenant ] = useState(false);

    return (
        <>
            <TenantRequest modalVisible={requestTenant} setModalVisible={setRequestTenant} />

            <Box padding="m">
                <Container>
                    <div className="flex justify-center mb-4">
                        <h2>No tenant is assigned to you, please request access to one.</h2>
                    </div>
                    <div className="flex justify-center">
                        <Button onClick={() => setRequestTenant(true)} variant="primary">Request Tenant</Button>
                    </div>
                </Container>
            </Box>
        </>
);
}