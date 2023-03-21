import React, { useContext } from "react";
import { Role } from "radien";
import PaginatedTable from "@/components/PaginatedTable/PaginatedTable";
import { Box, TableProps } from "@cloudscape-design/components";
import { QueryKeys } from "@/consts";
import { RadienContext } from "@/context/RadienContextProvider";
import { useRouter } from "next/router";
import usePaginatedRoles from "@/hooks/usePaginatedRoles";

export default function RoleManagement() {
    const { i18n, userInSession: radienUser } = useContext(RadienContext);
    const router = useRouter();

    const colDefinition: TableProps.ColumnDefinition<Role>[] = [
        {
            id: "name",
            header: i18n?.role_management_column_name || "Name",
            cell: (item: Role) => item?.name || "-",
            sortingField: "name",
        },
        {
            id: "description",
            header: i18n?.role_management_column_description || "Description",
            cell: (item: Role) => item?.description || "-",
            sortingField: "description",
        },
    ];

    return (
        <Box padding={"xl"}>
            <PaginatedTable
                tableHeader={i18n?.role_management_header || "Role Management"}
                queryKey={QueryKeys.ROLE_MANAGEMENT}
                manipulationEnableCondition={!radienUser?.processingLocked}
                columnDefinitions={colDefinition}
                getPaginated={(pageNumber, pageSize) => usePaginatedRoles({ pageNo: pageNumber, pageSize })}
                viewActionProps={{}}
                createActionProps={{
                    createLabel: i18n?.permission_management_create_label || "Create Role",
                    createAction: () => {
                        router.push("/role/createRole");
                    },
                }}
                deleteActionProps={{
                    deleteLabel: i18n?.role_management_delete_label || "Delete Role",
                    deleteConfirmationText: (selectedRole) =>
                        `${i18n?.role_management_delete_confirmation || "Are you sure you would like to delete ${}"}`.replace("${}", selectedRole?.name!),
                }}
                emptyProps={{
                    emptyMessage: i18n?.role_management_empty_label || "No roles available",
                    emptyActionLabel: i18n?.role_management_empty_action || "Create Role",
                }}
            />
        </Box>
    );
}
