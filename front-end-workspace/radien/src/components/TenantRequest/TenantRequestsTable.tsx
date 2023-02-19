import { Box, TableProps } from "@cloudscape-design/components";
import React, { useContext, useState } from "react";
import { QueryKeys, TicketType } from "@/consts";
import { RadienContext } from "@/context/RadienContextProvider";
import PaginatedTable from "@/components/PaginatedTable/PaginatedTable";
import dynamic from "next/dynamic";
import { TenantRequestsResult } from "@/pages/api/ticket/getTenantRequests";
import axios from "axios";
import useDeleteTicket from "@/hooks/useDeleteTicket";
import { useQueryClient } from "react-query";
import RoleAssignment from "@/components/TenantRequest/RoleAssignment";
import usePaginatedTenantRequests from "@/hooks/usePaginatedTenantRequests";

const ExpandableSection = dynamic(() => import("@cloudscape-design/components/expandable-section"), { ssr: false });

export default function TenantRequestsTable() {
    const {
        i18n,
        activeTenant: { data: activeTenantData },
    } = useContext(RadienContext);
    const [requestCount, setRequestCount] = useState(0);
    const [modalVisible, setModalVisible] = useState(false);
    const [selectedRow, setSelectedRow] = useState<TenantRequestsResult | undefined>();

    const queryClient = useQueryClient();
    const deleteTicket = useDeleteTicket();

    const columnDefinition: TableProps.ColumnDefinition<TenantRequestsResult>[] = [
        {
            id: "userId",
            header: i18n?.user_management_tenant_request_user_id || "User ID",
            cell: (item: TenantRequestsResult) => item?.ticket?.userId || "-",
            sortingField: "userId",
        },
        {
            id: "username",
            header: i18n?.user_management_tenant_request_username || "Username",
            cell: (item: TenantRequestsResult) => item?.user?.logon || "-",
            sortingField: "username",
        },
        {
            id: "firstname",
            header: i18n?.user_management_tenant_request_firstname || "First Name",
            cell: (item: TenantRequestsResult) => item?.user?.firstname || "-",
            sortingField: "firstname",
        },
        {
            id: "lastname",
            header: i18n?.user_management_tenant_request_lastname || "Last Name",
            cell: (item: TenantRequestsResult) => item?.user?.lastname || "-",
            sortingField: "lastname",
        },
    ];

    const invalidateRequestQuery = () => {
        queryClient.invalidateQueries(`${QueryKeys.TENANT_REQUESTS}_${selectedRow?.ticket.data})`);
    };

    return (
        <Box padding={"xl"}>
            <RoleAssignment visible={modalVisible} setVisible={setModalVisible} request={selectedRow} />
            <ExpandableSection
                headerText={i18n?.user_management_tenant_request_section_header || "Tenant Requests"}
                headerCounter={requestCount > 0 ? String(requestCount) : undefined}
                variant="container">
                <PaginatedTable
                    tableHeader={""}
                    tableVariant="embedded"
                    queryKey={`${QueryKeys.TENANT_REQUESTS}_${activeTenantData?.tenantId}`}
                    columnDefinitions={columnDefinition}
                    getPaginated={(pageNumber, pageSize) =>
                        usePaginatedTenantRequests({ pageNo: pageNumber, pageSize, tenantId: activeTenantData?.tenantId!, setRequestCount })
                    }
                    viewActionProps={{}}
                    createActionProps={{
                        createButtonType: "primary",
                        createLabel: i18n?.user_management_tenant_request_approve_action || "Approve",
                        createAction: (selectedValue?: TenantRequestsResult) => {
                            setSelectedRow(selectedValue);
                            setModalVisible(true);
                        },
                    }}
                    deleteActionProps={{
                        deleteLabel: i18n?.user_management_tenant_request_reject_action || "Reject",
                        deleteConfirmationText: (selectedUser) =>
                            `${i18n?.user_management_tenant_request_reject_action_confirmation || "Are you sure you would like to reject ${}"}`.replace(
                                "${}",
                                `${selectedUser?.user.firstname} ${selectedUser?.user.lastname}`
                            ),
                        deleteNestedObj: "ticket",
                        deleteAction: deleteTicket.mutate,
                        onDeleteSuccess: invalidateRequestQuery,
                    }}
                    emptyProps={{
                        emptyMessage: i18n?.user_management_tenant_request_empty_label || "No tenant Requests",
                    }}
                />
            </ExpandableSection>
        </Box>
    );
}
