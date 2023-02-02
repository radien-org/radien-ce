import React, {useState} from "react";
import {Role} from "radien";
import axios from "axios";
import {PaginatedTableProps} from "@/components/PaginatedTable/PaginatedTable";
import {Box, TableProps} from "@cloudscape-design/components";
import dynamic from "next/dynamic";
import {QueryKeys} from "@/consts";

export default function RoleManagement() {
    const PaginatedTable = dynamic(
        () => import("@/components/PaginatedTable/PaginatedTable"),
        { ssr: false}
    ) as React.ComponentType<PaginatedTableProps<Role>>

    const colDefinition: TableProps.ColumnDefinition<Role>[] = [
        {
            id: "name",
            header: "Name",
            cell: (item: Role) => item?.name || "-",
            sortingField: "name"
        },{
            id: "description",
            header: "Description",
            cell: (item: Role) => item?.description || "-",
            sortingField: "description"
        },
        {
            id: "terminationDate",
            header: "Termination Date",
            cell: (item: Role) => item?.terminationDate.toString().substring(0, item?.terminationDate.toString().indexOf('T')) || "-",
            sortingField: "terminationDate"
        }
    ]

    const getRolePage = async (pageNumber: number = 1, pageSize: number = 10) => {
        return await axios.get("/api/role/role/getAll", {
            params: {
                page: pageNumber,
                pageSize: pageSize
            }
        });
    }

    return (
        <Box padding={"xl"}>
            <PaginatedTable
                tableHeader={"Role Management"}
                queryKey={QueryKeys.ROLE_MANAGEMENT}
                columnDefinitions={colDefinition}
                getPaginated={getRolePage}
                viewActionProps={{}}
                createActionProps={{}}
                deleteActionProps={
                    {
                        deleteLabel: "Delete Role",
                        deleteConfirmationText: (selectedRole) => `Are you sure you would like to delete ${selectedRole?.name}`
                    }
                }
                emptyProps={
                    {
                        emptyMessage: "No roles available",
                        emptyActionLabel: "Create Role"
                    }
                }
            />
        </Box>
    )
}