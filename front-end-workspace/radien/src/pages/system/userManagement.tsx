import React from "react";
import {User} from "radien";
import axios from "axios";
import PaginatedTable from "@/components/PaginatedTable/PaginatedTable";
import {QueryKeys} from "@/consts";
import {Box, TableProps} from "@cloudscape-design/components";
import {useRouter} from "next/router";
import UserDetailsView from "@/components/UserDetailsView/UserDetailsView";

export default function UserManagement() {

    const router = useRouter();


    const colDefinition: TableProps.ColumnDefinition<User>[] = [
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

    //@ts-ignore
    const deleteAction = async ({tenantId, userId}) => {
        try{
            console.log(tenantId)
            await  axios.post(`/api/user/deleteUser`,{id:tenantId})
        }
        catch (e){
            console.log(e)
        }
    }


    return (
        <Box padding={"xl"}>
            <PaginatedTable
                tableHeader={"User Management"}
                queryKey={QueryKeys.USER_MANAGEMENT}
                columnDefinitions={colDefinition}
                getPaginated={getUserPage}
                viewActionProps={{
                    ViewComponent: UserDetailsView,
                    viewTitle: "User details"
                }}
                createActionProps={{
                    createLabel: "Create user",
                    createAction: () => {
                        router.push('/user/createUser');
                    }
                }}
                deleteActionProps={
                    {
                        deleteLabel: "Delete User",
                        deleteConfirmationText: (selectedUser) => `Are you sure you would like to delete ${selectedUser?.firstname} ${selectedUser?.lastname}`,
                        deleteAction: deleteAction
                    }
                }
                emptyProps={
                    {
                        emptyMessage: "No users available",
                        emptyActionLabel: "Create User",
                    }
                }
            />
        </Box>
    )
}