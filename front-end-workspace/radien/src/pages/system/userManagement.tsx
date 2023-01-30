import React, {useState} from "react";
import {User} from "radien";
import axios from "axios";
import {PaginatedTableProps} from "@/components/PaginatedTable/PaginatedTable";
import dynamic from "next/dynamic";
import {QueryKeys} from "@/consts";
import {Box} from "@cloudscape-design/components";

export default function UserManagement() {
    const PaginatedTable = dynamic(
        () => import("@/components/PaginatedTable/PaginatedTable"),
        { ssr: false}
    ) as React.ComponentType<PaginatedTableProps<User>>

    const [ selectedUser, setSelectedUser ] = useState<User>()


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
        <Box padding={"xl"}>
            <PaginatedTable
                tableHeader={"User Management"}
                queryKey={QueryKeys.USER_MANAGEMENT}
                columnDefinitions={colDefinition}
                getPaginated={getUserPage}
                selectedItemDetails={
                    {
                        selectedItem: selectedUser,
                        setSelectedItem: setSelectedUser
                    }
                }
                viewActionProps={{}}
                createActionProps={{}}
                deleteActionProps={
                    {
                        deleteLabel: "Delete User",
                        deleteConfirmationText: `Are you sure you would like to delete ${selectedUser?.firstname} ${selectedUser?.lastname}`
                    }
                }
                emptyProps={
                    {
                        emptyMessage: "No users available",
                        emptyActionLabel: "Create User"
                    }
                }
            />
        </Box>
    )
}