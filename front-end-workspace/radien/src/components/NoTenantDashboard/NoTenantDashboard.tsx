import React, {useContext, useState} from 'react';
import Card from "@/components/Card/Card";
import useCheckPermissions from "@/hooks/useCheckPermissions";
import {RadienContext} from "@/context/RadienContextProvider";
import Button from "@cloudscape-design/components/button";
import {Box, Container, Modal, Multiselect, SpaceBetween} from "@cloudscape-design/components";
import useAllTenantListed from "@/hooks/useAllTenantListed";

export default function NoTenantDashboard() {

    const [ requestTenant, setRequestTenantModalVisible ] = useState(false);

    const [ selectedOptions, setSelectedOptions ] = React.useState();

    const { data } = useAllTenantListed();

    const sendEmailToAdministrator = () => {
        console.log("Carreguei no ok");
    }

    return (
        <>
            <Modal
                onDismiss={() => setRequestTenantModalVisible(false)}
                visible={requestTenant}
                closeAriaLabel="Close modal"
                footer={
                    <Box float="right">
                        <SpaceBetween direction="horizontal" size="xs">
                            <Button variant="primary" onClick={() => sendEmailToAdministrator()}>Ok</Button>
                            <Button variant="link" onClick={() => setRequestTenantModalVisible(false)}>Cancel</Button>
                        </SpaceBetween>
                    </Box>
                }
                header="Request tenant">
                <Multiselect
                    selectedOptions={selectedOptions}
                    onChange={({ detail }) =>
                        setSelectedOptions(detail.selectedOptions)
                    }
                    deselectAriaLabel={e => `Remove ${e.label}`}
                    options={ data?.map(t => { return {label: t.name, value: String(t.id)} }) }
                    placeholder="Choose options"
                    selectedAriaLabel="Selected"
                />
            </Modal>

            <Box padding="m">
                <Container>
                    <div className="flex justify-center mb-4">
                        <h2>No tenant is assigned to you, please request access to one.</h2>
                    </div>
                    <div className="flex justify-center">
                        <Button onClick={() => setRequestTenantModalVisible(true)} variant="primary">Request Tenant</Button>
                    </div>
                </Container>
            </Box>
        </>
);
}