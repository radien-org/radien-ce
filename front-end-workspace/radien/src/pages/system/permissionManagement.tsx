import React, {useContext, useState} from "react";
import {Permission} from "radien";
import axios from "axios";
import {DeleteParams, PaginatedTableProps} from "@/components/PaginatedTable/PaginatedTable";
import {Box, TableProps} from "@cloudscape-design/components";
import dynamic from "next/dynamic";
import {QueryKeys} from "@/consts";
import {RadienContext} from "@/context/RadienContextProvider";
import {useRouter} from "next/router";

export default function PermissionManagement() {
    const PaginatedTable = dynamic(
        () => import("@/components/PaginatedTable/PaginatedTable"),
        { ssr: false}
    ) as React.ComponentType<PaginatedTableProps<Permission>>

    const pageSize = 10;
    const router = useRouter();

    const {addSuccessMessage, addErrorMessage} = useContext(RadienContext);

    const colDefinition: TableProps.ColumnDefinition<Permission>[] = [
        {
            id: "name",
            header: "Name",
            cell: (item: Permission) => item?.name || "-",
            sortingField: "name"
        },{
            id: "actionId",
            header: "Action",
            cell: (item: Permission) => item?.actionId.toString() || "-",
            sortingField: "actionId"
        },
        {
            id: "resourceId",
            header: "Resource",
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
            console.log('deletePermission', data);
            await axios.delete(`/api/permission/permission/delete/${data.tenantId}`);
            addSuccessMessage("Permission deleted successfully");
        } catch (e) {
            addErrorMessage("Failed to delete permission");
        }
    }

    return (
        <Box padding={"xl"}>
            <PaginatedTable
                tableHeader={"User Management"}
                queryKey={QueryKeys.PERMISSION_MANAGEMENT}
                columnDefinitions={colDefinition}
                getPaginated={getPermissionPage}
                viewActionProps={{}}
                createActionProps={{
                    createLabel: "Create permission",
                    createAction: () => {
                        router.push('/permission/createPermission');
                    }
                }}
                deleteActionProps={
                    {
                        deleteLabel: "Delete Permission",
                        deleteConfirmationText: (selectedPermission) => `Are you sure you would like to delete ${selectedPermission?.name}`,
                        deleteAction: deletePermission
                    }
                }
                emptyProps={
                    {
                        emptyMessage: "No permissions available",
                        emptyActionLabel: "Create Permission"
                    }
                }
            />
        </Box>
    )
}