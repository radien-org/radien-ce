import React, { useContext } from "react";
import { Role, TenantRole } from "radien";
import PaginatedTable from "@/components/PaginatedTable/PaginatedTable";
import { Box, TableProps } from "@cloudscape-design/components";
import { QueryKeys } from "@/consts";
import { RadienContext } from "@/context/RadienContextProvider";
import {getTenantRolesPage} from "@/hooks/usePaginatedTenantRoles";

export default function TenantRoleManagement() {
    const { i18n, userInSession: radienUser, addWarningMessage } = useContext(RadienContext);

    const colDefinition: TableProps.ColumnDefinition<TenantRole>[] = [
        {
            id: "id",
            header: i18n?.tenant_role_management_column_tenant_id || "ID",
            cell: (item: TenantRole) => item?.id || "-",
            sortingField: "id",
        },
        {
            id: "name",
            header: i18n?.tenant_role_management_column_tenant_name || "Tenant",
            cell: (item: TenantRole) => item?.tenant?.name || "-",
            sortingField: "name",
        },
        {
            id: "description",
            header: i18n?.tenant_role_management_column_role_name || "Role",
            cell: (item: TenantRole) => item?.role?.name || "-",
            sortingField: "description",
        },
    ];

    return (
        <Box padding={"xl"}>
            <PaginatedTable
                tableHeader={i18n?.tenant_role_management_header || "Tenant Role Management"}
                queryKey={QueryKeys.TENANT_ROLE_MANAGEMENT}
                manipulationDisableCondition={radienUser?.processingLocked}
                columnDefinitions={colDefinition}
                getPaginated={(pageNumber, pageSize) => getTenantRolesPage(pageNumber, pageSize)}
                viewActionProps={{}}
                createActionProps={{
                    createAction: () => addWarningMessage("Not yet implemented"),
                }}
                deleteActionProps={{
                    deleteLabel: i18n?.tenant_role_management_delete_label || "Delete Tenant Role",
                    deleteConfirmationText: (selectedRole) =>
                        `${i18n?.tenant_role_management_delete_confirmation || "Are you sure you would like to delete ${}"}`.replace(
                            "${}",
                            selectedRole?.role?.name!
                        ),
                    deleteAction: () => addWarningMessage("Not yet implemented"),
                }}
                emptyProps={{
                    emptyMessage: i18n?.tenant_role_management_empty_label || "No Tenant Roles available",
                    emptyActionLabel: i18n?.tenant_role_management_empty_action || "Create Tenant Role",
                }}
            />
        </Box>
    );
}
