import {Box, Button, Modal, Multiselect, SpaceBetween} from "@cloudscape-design/components";
import React, {useContext, useState} from "react";
import {v4 as uuidv4} from "uuid";
import {Ticket} from "radien";
import {TicketType} from "@/consts";
import moment from "moment/moment";
import {OptionDefinition} from "@cloudscape-design/components/internal/components/option/interfaces";
import {RadienContext} from "@/context/RadienContextProvider";
import useCreateTicket from "@/hooks/useCreateTicket";
import useNotifyTenantRoles from "@/hooks/useNotifyTenantRoles";
import useAvailableTenants from "@/hooks/useAvailableTenants";

interface TenantRequestProps {
    modalVisible: boolean,
    setModalVisible: (value: boolean) => void;
}

const TARGET_ROLES = ["Tenant Administrator", "System Administrator"];
export default function TenantRequest(props: TenantRequestProps) {
    const { userInSession: radienUser, addSuccessMessage, addErrorMessage } = useContext(RadienContext);
    const createTicket = useCreateTicket();
    const notifyTenantRoles = useNotifyTenantRoles();
    const { data } = useAvailableTenants();

    const [ selectedOptions, setSelectedOptions ] = useState<OptionDefinition[]>([]);
    const { modalVisible, setModalVisible } = props;

    const onClickAction = () => {
        setModalVisible(false);
        selectedOptions.forEach(option => {
            const uuid = uuidv4();
            const ticket:  Ticket = {
                userId: Number(radienUser?.id),
                token: uuid,
                ticketType: TicketType.TENANT_REQUEST,
                data: `${option.value!}`,
                createUser: Number(radienUser?.id),
                expireDate: moment().add(7, "days").format("yyyy-MM-DDTHH:mm:ss")
            }
            createTicket.mutate(ticket);
            const args = {
                tenantName: option.label,
                firstName: radienUser?.firstname,
                lastName: radienUser?.lastname,
                userId: String(radienUser?.id),
                targetUrl: "banana"
            }
            notifyTenantRoles.mutate(
                {
                    params: args,
                    viewId: "email-8",
                    language: "en",
                    tenantId: Number(option.value!),
                    roles: TARGET_ROLES
                },
                {
                    onSuccess: () => addSuccessMessage(`Administrators of ${option.label} have been notified`)
                }
            )
        })
    }

    return (
        <Modal
            onDismiss={() => setModalVisible(false)}
            visible={modalVisible}
            closeAriaLabel="Close modal"
            footer={
                <Box float="right">
                    <SpaceBetween direction="horizontal" size="xs">
                        <Button variant="primary" onClick={() => onClickAction()}>Ok</Button>
                        <Button variant="link" onClick={() => setModalVisible(false)}>Cancel</Button>
                    </SpaceBetween>
                </Box>
            }
            header="Request tenant">
            <Multiselect
                selectedOptions={selectedOptions}
                onChange={({ detail }) =>
                    setSelectedOptions(() => [...detail.selectedOptions])
                }
                deselectAriaLabel={e => `Remove ${e.label}`}
                options={ data?.map(t => { return {label: t.name, value: String(t.id)} }) }
                placeholder="Choose options"
                selectedAriaLabel="Selected"
            />
        </Modal>
    )
}