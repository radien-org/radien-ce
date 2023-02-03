import React, {useContext} from "react";
import {Permission} from "radien";
import axios from "axios";
import PaginatedTable, {DeleteParams} from "@/components/PaginatedTable/PaginatedTable";
import {Box, TableProps} from "@cloudscape-design/components";
import dynamic from "next/dynamic";
import {QueryKeys} from "@/consts";
import {RadienContext} from "@/context/RadienContextProvider";
import {useRouter} from "next/router";

export default function PermissionManagement() {
    const pageSize = 10;
    const router = useRouter();

    const {addSuccessMessage, addErrorMessage, i18n} = useContext(RadienContext);

    const colDefinition: TableProps.ColumnDefinition<Permission>[] = [
        {
            id: "name",
            header: i18n?.permission_management_column_name || "Name",
            cell: (item: Permission) => item?.name || "-",
            sortingField: "name"
        },{
            id: "actionId",
            header: i18n?.permission_management_column_action || "Action",
            cell: (item: Permission) => item?.actionId.toString() || "-",
            sortingField: "actionId"
        },
        {
            id: "resourceId",
            header: i18n?.permission_management_column_resource || "Resource",
            cell: (item: Permission) => item?.resourceId.toString() || "-",
            sortingField: "resourceId"
        }
    ]

    const getPermissionPage = async (pageNumber: number = 1, pageSize: number = 10) => {
        return await axios.get("/api/permission/permission/getAll", {
            params: {
                page: pageNumber,
                pageSize: pageSize
            }
        });
    }

    const deletePermission = async (data: DeleteParams) => {
        try {
            await axios.delete(`/api/permission/permission/delete/${data.tenantId}`);
            let message = `${i18n?.generic_message_success || "Success"}: ${i18n?.permission_management_delete_success || "Permission deleted successfully"}`
            addSuccessMessage(message);
        } catch (e) {
            let message = `${i18n?.generic_message_error || "Error"}: ${i18n?.permission_management_delete_error || "Failed to delete permission"}`
            addErrorMessage(message);
        }
    }

    return (
        <Box padding={"xl"}>
            <PaginatedTable
                tableHeader={i18n?.permission_management_header || "Permission Management"}
                queryKey={QueryKeys.PERMISSION_MANAGEMENT}
                columnDefinitions={colDefinition}
                getPaginated={getPermissionPage}
                viewActionProps={{}}
                createActionProps={{
                    createLabel: i18n?.permission_management_create_label || "Create permission",
                    createAction: () => {
                        router.push('/permission/createPermission');
                    }
                }}
                deleteActionProps={
                    {
                        deleteLabel: i18n?.permission_management_delete_label || "Delete Permission",
                        deleteConfirmationText: (selectedPermission) => `${i18n?.permission_management_delete_confirmation ||  "Are you sure you would like to delete ${}"}`.replace("${}", selectedPermission?.name),
                        deleteAction: deletePermission
                    }
                }
                emptyProps={
                    {
                        emptyMessage: i18n?.permission_management_empty_label || "No permissions available",
                        emptyActionLabel: i18n?.permission_management_empty_action || "Create Permission"
                    }
                }
            />
        </Box>
    )
}