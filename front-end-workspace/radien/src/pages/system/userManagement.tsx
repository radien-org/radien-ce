import React, {useContext} from "react";
import {User} from "radien";
import axios, {AxiosResponse} from "axios";
import PaginatedTable, {PaginatedTableProps} from "@/components/PaginatedTable/PaginatedTable";
import dynamic from "next/dynamic";
import {QueryKeys} from "@/consts";
import {Box, TableProps} from "@cloudscape-design/components";
import {useRouter} from "next/router";
import UserDetailsView from "@/components/UserDetailsView/UserDetailsView";
import {RadienContext} from "@/context/RadienContextProvider";

export default function UserManagement() {

    const { i18n } = useContext(RadienContext);
    const router = useRouter();


    const colDefinition: TableProps.ColumnDefinition<User>[] = [
        {
            id: "logon",
            header: i18n?.user_management_column_username || "Username",
            cell: (item: User) => item?.logon || "-",
            sortingField: "logon"
        },{
            id: "firstname",
            header: i18n?.user_management_column_firstname || "First Name",
            cell: (item: User) => item?.firstname || "-",
            sortingField: "firstname"
        },
        {
            id: "lastname",
            header: i18n?.user_management_column_lastname || "Last Name",
            cell: (item: User) => item?.lastname || "-",
            sortingField: "lastname"
        },
        {
            id: "userEmail",
            header: i18n?.user_management_column_email || "User Email",
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
                tableHeader={i18n?.user_management_header || "User Management"}
                queryKey={QueryKeys.USER_MANAGEMENT}
                columnDefinitions={colDefinition}
                getPaginated={getUserPage}
                viewActionProps={{
                    ViewComponent: UserDetailsView,
                    viewTitle: i18n?.user_management_view_label || "User details"
                }}
                createActionProps={{
                    createLabel: i18n?.user_management_create_label || "Create User",
                    createAction: () => {
                        router.push('/user/createUser');
                    }
                }}
                deleteActionProps={
                    {
                        deleteLabel: i18n?.user_management_delete_label || "Delete User",
                        deleteConfirmationText: (selectedUser) => `${i18n?.user_management_delete_confirmation || "Are you sure you would like to delete ${}"}`.replace("${}", `${selectedUser?.firstname} ${selectedUser?.lastname}`),
                        deleteAction: deleteAction
                    }
                }
                emptyProps={
                    {
                        emptyMessage: i18n?.user_management_empty_label || "No users available",
                        emptyActionLabel: i18n?.user_management_empty_action || "Create User",
                    }
                }
            />
        </Box>
    )
}