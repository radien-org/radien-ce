import React, { useContext, useState } from "react";
import { SplitEmptyColumnDefinition, Tenant } from "radien";
import { AppLayout, Box, Button, ColumnLayout, Header, Modal, Pagination, SpaceBetween, SplitPanel, Table, TableProps } from "@cloudscape-design/components";
import { QueryKeys } from "@/consts";
import { RadienContext } from "@/context/RadienContextProvider";
import useDeleteTenant from "@/hooks/useDeleteTenant";
import {getTenantPage} from "@/hooks/usePaginatedTenants";
import { useRouter } from "next/router";
import useTenantChildren from "@/hooks/useTenantChildren";
import PaginatedTable from "@/components/PaginatedTable/PaginatedTable";

export default function TenantManagement() {
    const router = useRouter();
    const { i18n, userInSession: radienUser } = useContext(RadienContext);
    const deleteTenant = useDeleteTenant();
    const { activeTenant } = useContext(RadienContext);
    const [selectedItem, setSelectedItem] = useState<Tenant>();
    const { isLoading: isLoadingChildren, data: items } = useTenantChildren(selectedItem?.id!);

    const emptySplitColumns: SplitEmptyColumnDefinition = {
        header: i18n?.split_view_nothing_selected || "Nothing selected",
        body: i18n?.split_view_nothing_selected_description || "Select a tenant to see its children.",
    };

    const colDefinition: TableProps.ColumnDefinition<Tenant>[] = [
        {
            id: "name",
            header: i18n?.tenant_management_column_name || "Name",
            cell: (item: Tenant) => item?.name || "-",
            sortingField: "name",
        },
        {
            id: "tenantKey",
            header: i18n?.tenant_management_column_key || "Key",
            cell: (item: Tenant) => item?.tenantKey || "-",
            sortingField: "tenantKey",
        },
        {
            id: "tenantType",
            header: i18n?.tenant_management_column_type || "Type",
            cell: (item: Tenant) => item?.tenantType || "-",
            sortingField: "tenantType",
        },
        {
            id: "tenantStart",
            header: i18n?.tenant_management_column_start_date || "Start Date",
            cell: (item: Tenant) => item?.tenantStart.toString() || "-",
            sortingField: "tenantStart",
        },
        {
            id: "tenantEnd",
            header: i18n?.tenant_management_column_end_date || "End Date",
            cell: (item: Tenant) => item?.tenantEnd.toString() || "-",
            sortingField: "tenantEnd",
        },
        {
            id: "clientAddress",
            header: i18n?.tenant_management_column_address || "Address",
            cell: (item: Tenant) => item?.clientAddress || "-",
            sortingField: "clientAddress",
        },
        {
            id: "clientZipCode",
            header: i18n?.tenant_management_column_zip_code || "Zip Code",
            cell: (item: Tenant) => item?.clientZipCode || "-",
            sortingField: "clientZipCode",
        },
        {
            id: "clientCity",
            header: i18n?.tenant_management_column_city || "City",
            cell: (item: Tenant) => item?.clientCity || "-",
            sortingField: "clientCity",
        },
        {
            id: "clientCountry",
            header: i18n?.tenant_management_column_country || "Country",
            cell: (item: Tenant) => item?.clientCountry || "-",
            sortingField: "clientCountry",
        },
        {
            id: "clientPhoneNumber",
            header: i18n?.tenant_management_column_phone_number || "Phone Number",
            cell: (item: Tenant) => item?.clientPhoneNumber || "-",
            sortingField: "clientPhoneNumber",
        },
        {
            id: "clientEmail",
            header: i18n?.tenant_management_column_email || "Email",
            cell: (item: Tenant) => item?.clientEmail || "-",
            sortingField: "clientEmail",
        },
        {
            id: "parentId",
            header: i18n?.tenant_management_column_parent_tenant || "Parent Tenant",
            cell: (item: Tenant) => item?.parentData?.name || "-",
            sortingField: "parentId",
        },
        {
            id: "clientId",
            header: i18n?.tenant_management_column_client_tenant || "Client Tenant",
            cell: (item: Tenant) => item?.clientData?.name || "-",
            sortingField: "clientId",
        },
    ];

    const getPanelContent = (item: Tenant | undefined) => {
        if (!item) {
            return emptySplitColumns;
        }

        return {
            header: item.name,
            body: (
                <>
                    <ColumnLayout columns={3} variant="text-grid">
                        <div>
                            <Box variant="awsui-key-label">{i18n?.tenant_management_column_name || "Name"}</Box>
                            <h3 className="text-3xl font-semibold text-[#00509c] ml-1">{item.name}</h3>
                        </div>
                        <div>
                            <Box variant="awsui-key-label">{i18n?.tenant_management_column_key || "Key"}</Box>
                            <h3 className="text-3xl font-semibold text-[#00509c] ml-1">{item.tenantKey}</h3>
                        </div>
                        <div>
                            <Box variant="awsui-key-label">{i18n?.tenant_management_column_type || "Type"}</Box>
                            <h3 className="text-3xl font-semibold text-[#00509c] ml-1">{item.tenantType}</h3>
                        </div>
                    </ColumnLayout>
                    <hr className="h-[1px] bg-gray-200 mt-10 mb-7" />
                    <div>
                        <Header variant="h2">{i18n?.tenant_management_children || "Children"}</Header>
                        {items?.results?.length != 0 ? (
                            <>
                                <Box padding={{ bottom: "l" }}>
                                    <Table loading={isLoadingChildren} columnDefinitions={colDefinition} items={items?.results!} variant="embedded"></Table>
                                </Box>
                            </>
                        ) : (
                            <>
                                <div>{i18n?.tenant_management_children_none_message || "This tenant does not have children"}</div>
                            </>
                        )}
                    </div>
                </>
            ),
        };
    };

    const { header: panelHeader, body: panelBody } = getPanelContent(selectedItem);

    return (
        <Box padding={"xl"}>
            <AppLayout
                navigationHide={true}
                contentType={"table"}
                maxContentWidth={Number.MAX_VALUE}
                toolsHide={true}
                splitPanel={
                    <SplitPanel
                    className="mb-[20px]"
                        header={panelHeader}
                        i18nStrings={{
                            preferencesTitle: i18n?.split_view_preferences || "Split panel preferences",
                            preferencesPositionLabel: i18n?.split_view_position || "Split panel position",
                            preferencesPositionDescription: i18n?.split_view_position_description || "Choose the default split panel position for the service.",
                            preferencesPositionSide: i18n?.split_view_position_side || "Side",
                            preferencesPositionBottom: i18n?.split_view_position_bottom || "Bottom",
                            preferencesConfirm: i18n?.button_ok || "Ok",
                            preferencesCancel: i18n?.button_cancel || "Cancel",
                            closeButtonAriaLabel: i18n?.split_view_button_close || "Close panel",
                            openButtonAriaLabel: i18n?.split_view_button_open || "Open panel",
                            resizeHandleAriaLabel: i18n?.split_view_button_open || "Resize split panel",
                        }}>
                        {panelBody}
                    </SplitPanel>
                }
                content={
                    <PaginatedTable
                        tableHeader={i18n?.tenant_management_header || "Tenant Management"}
                        queryKey={QueryKeys.TENANT_MANAGEMENT}
                        manipulationDisableCondition={radienUser?.processingLocked}
                        onSelectAction={(item) => setSelectedItem(item)}
                        columnDefinitions={colDefinition}
                        getPaginated={(pageNumber, pageSize) => getTenantPage(pageNumber, pageSize)}
                        viewActionProps={{}}
                        createActionProps={{
                            createLabel: i18n?.tenant_management_create_label || "Create Tenant",
                            createAction: () => {
                                router.push("/tenant/createTenant");
                            },
                        }}
                        deleteActionProps={{
                            deleteLabel: i18n?.tenant_management_delete_label || "Delete Tenant",
                            deleteConfirmationText: (selectedTenant) =>
                                `${i18n?.tenant_management_delete_confirmation || "Are you sure you would like to delete ${}"}`.replace(
                                    "${}",
                                    selectedTenant?.name!
                                ),
                            deleteAction: deleteTenant.mutate,
                        }}
                        emptyProps={{
                            emptyMessage: i18n?.tenant_management_empty_label || "No tenants available",
                            emptyActionLabel: i18n?.tenant_management_empty_action || "Create Tenant",
                        }}
                    />
                }
            />
        </Box>
    );
}
