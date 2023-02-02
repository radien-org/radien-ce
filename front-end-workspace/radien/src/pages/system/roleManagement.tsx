import React from "react";
import {Role} from "radien";
import axios from "axios";
import PaginatedTable from "@/components/PaginatedTable/PaginatedTable";
import {Box, TableProps} from "@cloudscape-design/components";
import {QueryKeys} from "@/consts";

export default function RoleManagement() {
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