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
import {AxiosError} from "axios";
import {Loader} from "@/components/Loader/Loader";

interface TenantRequestProps {
    modalVisible: boolean,
    setModalVisible: (value: boolean) => void;
}

const TARGET_ROLES = ["Tenant Administrator", "System Administrator"];

export default function TenantRequest(props: TenantRequestProps) {
    const { userInSession: radienUser, i18n, addSuccessMessage, addErrorMessage } = useContext(RadienContext);
    const { modalVisible, setModalVisible } = props;
    const createTicket = useCreateTicket();
    const notifyTenantRoles = useNotifyTenantRoles();
    const { data } = useAvailableTenants();

    const [ selectedOptions, setSelectedOptions ] = useState<OptionDefinition[]>([]);
    const [ loading, setLoading ] = useState<string[]>([]);

    const popLoadingState = () => {
        setLoading((loading) => {
            let newLoading = [...loading];
            newLoading.pop();
            return newLoading;
        });
    }

    const notifyAdmins = (option: OptionDefinition, referenceUrl: string) => {
        const args = {
            tenantName: option.label,
            firstName: radienUser?.firstname,
            lastName: radienUser?.lastname,
            userId: String(radienUser?.id),
            targetUrl: referenceUrl
        }

        notifyTenantRoles.mutateAsync(
            {
                params: args,
                viewId: "email-8",
                language: "en",
                tenantId: Number(option.value!),
                roles: TARGET_ROLES
            }).then((r) => {
                const message = `${i18n?.generic_success_message || "Success"}: ${i18n.tenant_request_admins_notified || "Administrators of ${} have been notified"}`
                    .replace("${}", option.label!);
                addSuccessMessage(message);
                popLoadingState();
            }, (e) => {
                if(e instanceof AxiosError && e.response?.status == 404) {
                    const message = `${i18n?.generic_error_message || "Error"}: ${i18n.tenant_request_no_admins_found || "No users with the necessary roles were found in ${}"}`
                        .replace("${}", option.label!)
                    addErrorMessage(message);
                    popLoadingState();
                }
            }
        )
    }

    const onClickAction = () => {
        setModalVisible(false);
        const referenceUrl: string = `${process.env.NEXTAUTH_URL}/system/userManagement")`;
        selectedOptions.forEach(option => {
            const uuid = uuidv4();
            loading.push(option.value!);
            const ticket:  Ticket = {
                userId: Number(radienUser?.id),
                token: uuid,
                ticketType: TicketType.TENANT_REQUEST,
                data: `${option.value!}`,
                createUser: Number(radienUser?.id),
                expireDate: moment().add(7, "days").format("yyyy-MM-DDTHH:mm:ss")
            }

            createTicket.mutateAsync(ticket)
                .then((r) => {
                    notifyAdmins(option, referenceUrl);
                }, (e) => {
                    const message = `${i18n?.generic_error_message || "Error"}: ${i18n.tenant_request_create_ticket_error || "Could not create Tenant Request. If the error persists please contact an administrator."}`
                    addErrorMessage(message);
                    popLoadingState();
                });
        })
    }

    if(loading.length !== 0) {
        return <Loader />
    }

    return (
        <Modal
            onDismiss={() => setModalVisible(false)}
            visible={modalVisible}
            closeAriaLabel="Close modal"
            footer={
                <Box float="right">
                    <SpaceBetween direction="horizontal" size="xs">
                        <Button variant="primary" onClick={() => onClickAction()}>{i18n?.request_tenant_button_primary || "Ok"}</Button>
                        <Button variant="link" onClick={() => setModalVisible(false)}>{i18n?.request_tenant_button_cancel || "Cancel"}</Button>
                    </SpaceBetween>
                </Box>
            }
            header={i18n?.request_tenant_header || "Request tenant"}>
            <Multiselect
                selectedOptions={selectedOptions}
                onChange={({ detail }) =>
                    setSelectedOptions(() => [...detail.selectedOptions])
                }
                options={ data?.map(t => { return {label: t.name, value: String(t.id)} }) }
                placeholder={i18n?.request_tenant_options_placeholder || "Choose tenant(s)"}
            />
        </Modal>
    )
}