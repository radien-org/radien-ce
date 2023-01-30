import React from "react";
import {Role} from "radien";
import axios from "axios";
import {PaginatedTableProps} from "@/components/PaginatedTable/PaginatedTable";
import {TableProps} from "@cloudscape-design/components";
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
            cell: (item: Role) => item?.terminationDate.toString() || "-",
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
        <>
            <PaginatedTable
                queryKey={QueryKeys.ROLE_MANAGEMENT}
                getPaginated={getRolePage}
                columnDefinitions={colDefinition}
                deleteConfirmationText={"Are you sure you would like to delete the selected role?"}
                tableHeader={"Role Management"}
                emptyMessage={"No roles available"}
                emptyAction={"Create role"}
            />
        </>
    )
}