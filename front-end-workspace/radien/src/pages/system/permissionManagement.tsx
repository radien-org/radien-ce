import React, {useState} from "react";
import {Permission} from "radien";
import axios from "axios";
import {PaginatedTableProps} from "@/components/PaginatedTable/PaginatedTable";
import {Box, TableProps} from "@cloudscape-design/components";
import dynamic from "next/dynamic";
import {QueryKeys} from "@/consts";

export default function PermissionManagement() {
    const PaginatedTable = dynamic(
        () => import("@/components/PaginatedTable/PaginatedTable"),
        { ssr: false}
    ) as React.ComponentType<PaginatedTableProps<Permission>>

    const [ selectedPermission, setSelectedPermission ] = useState<Permission>();

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

    return (
        <Box padding={"xl"}>
            <PaginatedTable
                tableHeader={"User Management"}
                queryKey={QueryKeys.PERMISSION_MANAGEMENT}
                columnDefinitions={colDefinition}
                getPaginated={getPermissionPage}
                selectedItemDetails={
                    {
                        selectedItem: selectedPermission,
                        setSelectedItem: setSelectedPermission
                    }
                }
                viewActionProps={{}}
                createActionProps={{}}
                deleteActionProps={
                    {
                        deleteLabel: "Delete Permission",
                        deleteConfirmationText: `Are you sure you would like to delete ${selectedPermission?.name}`
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