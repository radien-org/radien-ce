import React, { useContext } from "react";
import { Tenant } from "radien";
import PaginatedTable from "@/components/PaginatedTable/PaginatedTable";
import { Box, TableProps } from "@cloudscape-design/components";
import { QueryKeys } from "@/consts";
import { RadienContext } from "@/context/RadienContextProvider";
import useDeleteTenant from "@/hooks/useDeleteTenant";
import usePaginatedTenants from "@/hooks/usePaginatedTenants";
import { useRouter } from "next/router";
import useActiveTenant from "@/hooks/useActiveTenant";
import useAvailableTenants from "@/hooks/useAvailableTenants";



export default function TenantManagement() {
    const router = useRouter();
    const { i18n, userInSession: radienUser } = useContext(RadienContext);
    const deleteTenant = useDeleteTenant();
    const { activeTenant } = useContext(RadienContext)


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

    return (
        <Box padding={"xl"}>
            <PaginatedTable
                tableHeader={i18n?.tenant_management_header || "Tenant Management"}
                queryKey={QueryKeys.TENANT_MANAGEMENT}
                manipulationDisableCondition={radienUser?.processingLocked}
                columnDefinitions={colDefinition}
                getPaginated={(pageNumber, pageSize) => usePaginatedTenants({ pageNo: pageNumber, pageSize })}
                viewActionProps={{}}
                createActionProps={{
                    createLabel: i18n?.tenant_management_create_label || "Create Tenant",
                    createAction: () => {
                        router.push("/tenant/createTenant");
                    },}}
                deleteActionProps={{
                    deleteLabel: i18n?.tenant_management_delete_label || "Delete Tenant",
                    deleteConfirmationText: (selectedTenant) =>
                        `${i18n?.tenant_management_delete_confirmation || "Are you sure you would like to delete ${}"}`.replace("${}", selectedTenant?.name!),
                    deleteAction: deleteTenant.mutate,
                }}
                emptyProps={{
                    emptyMessage: i18n?.tenant_management_empty_label || "No tenants available",
                    emptyActionLabel: i18n?.tenant_management_empty_action || "Create Tenant",
                }}
            />
        </Box>
    );
}
