import React, { useContext, useState } from "react";
import { RadienContext } from "@/context/RadienContextProvider";
import useAssignableRoles from "@/hooks/useAssignableRoles";
import { useQueryClient } from "react-query";
import useAssignTenantRoles from "@/hooks/useAssignTenantRoles";
import useDeleteTicket from "@/hooks/useDeleteTicket";
import { OptionDefinition } from "@cloudscape-design/components/internal/components/option/interfaces";
import { Box, Button, Modal, Multiselect, SpaceBetween, Spinner } from "@cloudscape-design/components";
import { QueryKeys } from "@/consts";
import { TenantRequestsResult } from "@/pages/api/ticket/getTenantRequests";
import { AxiosError } from "axios";
import useNotifyUser from "@/hooks/useNotifyUser";
import useAssignedTenants from "@/hooks/useAssignedTenants";

interface RoleAssignmentProps {
    visible: boolean;
    setVisible: (value: boolean) => void;
    request?: TenantRequestsResult;
}

export default function RoleAssignment(props: RoleAssignmentProps) {
    const { request, visible, setVisible } = props;
    const { i18n, addSuccessMessage, addErrorMessage } = useContext(RadienContext);
    const { data } = useAssignableRoles(Number(request?.ticket.data));
    const queryClient = useQueryClient();
    const assignRole = useAssignTenantRoles();
    const { data: assignedTenants } = useAssignedTenants();
    const deleteTicket = useDeleteTicket();
    const notifyUser = useNotifyUser();

    const [targetRoles, setTargetRoles] = useState<OptionDefinition[]>([]);
    const [loading, setLoading] = useState<string[]>([]);

    if (loading.length != 0) {
        return <Spinner />;
    }

    const pushLoadingState = (val: string) => {
        setLoading((loading) => {
            let newLoading = [...loading];
            newLoading.push(val);
            return newLoading;
        });
    };

    const popLoadingState = () => {
        setLoading((loading) => {
            let newLoading = [...loading];
            newLoading.pop();
            return newLoading;
        });
    };

    const onError = (e: Error) => {
        popLoadingState();
        let message = `${i18n?.generic_message_error || "Error"}:`;
        if (e instanceof AxiosError) {
            message = `${message} ${e.response?.statusText}`;
        } else {
            message = `${message} ${e.message}`;
        }
        addErrorMessage(message);
    };

    const deleteRequestTicket = (role: OptionDefinition) => {
        deleteTicket.mutateAsync({ objectId: String(request?.ticket.id!), userId: String(request?.user.id!) }).then(
            () => {
                popLoadingState();
                const message = i18n?.user_management_tenant_request_role_assigned || "Role ${} successfully assigned";
                addSuccessMessage(message.replace("${}", role.label!));
                return queryClient.invalidateQueries(`${QueryKeys.TENANT_REQUESTS}_${request?.ticket?.data}`);
            },
            (e) => onError(e as Error)
        );
    };

    const notifyUserOfRoleAssignment = () => {
        const viewId: string = "email-9";
        console.log(assignedTenants);
        const args = {
            firstName: request?.user?.firstname,
            lastName: request?.user?.lastname,
            tenantName: assignedTenants?.results?.find((t) => t.id == request?.ticket.data)?.name || request?.ticket.data,
            roleList: targetRoles.map((t) => t.label).join(", "),
        };

        notifyUser.mutate({
            email: request?.user?.userEmail!,
            viewId,
            language: "en",
            params: args,
        });
    };

    const assignSelectedRoles = () => {
        if (targetRoles.length == 0) {
            addErrorMessage(i18n?.user_management_tenant_request_no_role_selected || "Please select at least one role to assign");
            return;
        }
        targetRoles.forEach((role) => {
            pushLoadingState(role.value!);
            assignRole
                .mutateAsync({
                    userId: request?.user?.id!,
                    tenantRoleId: Number(role.value),
                })
                .then(
                    () => {
                        deleteRequestTicket(role);
                    },
                    (e) => {
                        onError(e as Error);
                    }
                );
        });
        notifyUserOfRoleAssignment();
        setTargetRoles([]);
        setVisible(false);
    };

    return (
        <>
            <Modal
                onDismiss={() => setVisible(false)}
                visible={visible}
                closeAriaLabel="Close modal"
                footer={
                    <Box float="right">
                        <SpaceBetween direction="horizontal" size="xs">
                            <Button variant="link" onClick={() => setVisible(false)}>
                                {i18n?.button_cancel || "Cancel"}
                            </Button>
                            <Button variant="primary" onClick={() => assignSelectedRoles()} disabled={targetRoles.length == 0}>
                                {i18n?.button_ok || "Ok"}
                            </Button>
                        </SpaceBetween>
                    </Box>
                }
                header={i18n?.user_management_tenant_request_approve_action || "Approve"}>
                <Multiselect
                    selectedOptions={targetRoles}
                    onChange={({ detail }) => setTargetRoles(() => [...detail.selectedOptions])}
                    options={data?.map((t) => {
                        return { label: t.role.name, value: String(t.tenantRole.id) };
                    })}
                    placeholder={i18n?.user_management_tenant_request_choose_roles || "Choose Role(s)"}
                />
            </Modal>
        </>
    );
}
