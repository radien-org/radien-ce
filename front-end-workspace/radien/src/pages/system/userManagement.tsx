import React from "react";
import {User} from "radien";
import axios from "axios";
import {PaginatedTableProps} from "@/components/PaginatedTable/PaginatedTable";
import dynamic from "next/dynamic";

export default function UserManagement() {
    const PaginatedTable = dynamic(
        () => import("@/components/PaginatedTable/PaginatedTable"),
        { ssr: false}
    ) as React.ComponentType<PaginatedTableProps<User>>


    const colDefinition = [
        {
            id: "logon",
            header: "Username",
            cell: (item: User) => item?.logon || "-",
            sortingField: "logon"
        },{
            id: "firstname",
            header: "First Name",
            cell: (item: User) => item?.firstname || "-",
            sortingField: "firstname"
        },
        {
            id: "lastname",
            header: "Last Name",
            cell: (item: User) => item?.lastname || "-",
            sortingField: "lastname"
        },
        {
            id: "userEmail",
            header: "User Email",
            cell: (item: User) => item?.userEmail || "-",
            sortingField: "userEmail"
        }
    ]

    const getUserPage = async (pageNumber: number = 1, pageSize: number = 10) => {
        return await axios.get("/api/user/getAll", {
            params: {
                page: pageNumber,
                pageSize: pageSize
            }
        });
    }

    return (
        <>
            <PaginatedTable
                queryKey={"usersManagement"}
                getPaginated={getUserPage}
                columnDefinitions={colDefinition}
                deleteConfirmationText={"Are you sure you would like to delete the selected user?"}
                tableHeader={"User Management"} />
        </>
    )
}