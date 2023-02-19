import React, { useContext } from "react";
import { Permission } from "radien";
import PaginatedTable from "@/components/PaginatedTable/PaginatedTable";
import { Box, TableProps } from "@cloudscape-design/components";
import { QueryKeys } from "@/consts";
import { RadienContext } from "@/context/RadienContextProvider";
import { useRouter } from "next/router";
import useDeletePermission from "@/hooks/useDeletePermission";
import usePaginatedPermissions from "@/hooks/usePaginatedPermissions";

export default function PermissionManagement() {
    const pageSize = 10;
    const router = useRouter();

    const { i18n } = useContext(RadienContext);
    const deletePermission = useDeletePermission();

    const colDefinition: TableProps.ColumnDefinition<Permission>[] = [
        {
            id: "name",
            header: i18n?.permission_management_column_name || "Name",
            cell: (item: Permission) => item?.name || "-",
            sortingField: "name",
        },
        {
            id: "actionId",
            header: i18n?.permission_management_column_action || "Action",
            cell: (item: Permission) => item?.action?.name.toString() || "-",
            sortingField: "actionId",
        },
        {
            id: "resourceId",
            header: i18n?.permission_management_column_resource || "Resource",
            cell: (item: Permission) => item?.resource?.name.toString() || "-",
            sortingField: "resourceId",
        },
    ];

    return (
        <Box padding={"xl"}>
            <PaginatedTable
                tableHeader={i18n?.permission_management_header || "Permission Management"}
                queryKey={QueryKeys.PERMISSION_MANAGEMENT}
                columnDefinitions={colDefinition}
                getPaginated={(pageNumber, pageSize) => usePaginatedPermissions({ pageNo: pageNumber, pageSize })}
                viewActionProps={{}}
                createActionProps={{
                    createLabel: i18n?.permission_management_create_label || "Create permission",
                    createAction: () => {
                        router.push("/permission/createPermission");
                    },
                }}
                deleteActionProps={{
                    deleteLabel: i18n?.permission_management_delete_label || "Delete Permission",
                    deleteConfirmationText: (selectedPermission) =>
                        `${i18n?.permission_management_delete_confirmation || "Are you sure you would like to delete ${}"}`.replace(
                            "${}",
                            selectedPermission?.name!
                        ),
                    deleteAction: deletePermission.mutate,
                }}
                emptyProps={{
                    emptyMessage: i18n?.permission_management_empty_label || "No permissions available",
                    emptyActionLabel: i18n?.permission_management_empty_action || "Create Permission",
                }}
            />
        </Box>
    );
}
