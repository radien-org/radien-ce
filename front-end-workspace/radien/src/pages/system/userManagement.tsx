import React, { useContext } from "react";
import { User } from "radien";
import PaginatedTable from "@/components/PaginatedTable/PaginatedTable";
import { QueryKeys } from "@/consts";
import { Box, TableProps } from "@cloudscape-design/components";
import { useRouter } from "next/router";
import UserDetailsView from "@/components/UserDetailsView/UserDetailsView";
import { RadienContext } from "@/context/RadienContextProvider";
import useDeleteUser from "@/hooks/useDeleteUser";
import TenantRequestsTable from "@/components/TenantRequest/TenantRequestsTable";
import usePaginatedUsers from "@/hooks/usePaginatedUsers";

export default function UserManagement() {
    const { i18n } = useContext(RadienContext);
    const deleteUser = useDeleteUser();
    const router = useRouter();

    const colDefinition: TableProps.ColumnDefinition<User>[] = [
        {
            id: "logon",
            header: i18n?.user_management_column_username || "Username",
            cell: (item: User) => item?.logon || "-",
            sortingField: "logon",
        },
        {
            id: "firstname",
            header: i18n?.user_management_column_firstname || "First Name",
            cell: (item: User) => item?.firstname || "-",
            sortingField: "firstname",
        },
        {
            id: "lastname",
            header: i18n?.user_management_column_lastname || "Last Name",
            cell: (item: User) => item?.lastname || "-",
            sortingField: "lastname",
        },
        {
            id: "userEmail",
            header: i18n?.user_management_column_email || "User Email",
            cell: (item: User) => item?.userEmail || "-",
            sortingField: "userEmail",
        },
    ];

    return (
        <>
            <TenantRequestsTable />
            <Box padding={"xl"}>
                <PaginatedTable
                    tableHeader={i18n?.user_management_header || "User Management"}
                    queryKey={QueryKeys.USER_MANAGEMENT}
                    columnDefinitions={colDefinition}
                    getPaginated={(pageNumber, pageSize) => usePaginatedUsers({ pageNo: pageNumber, pageSize })}
                    viewActionProps={{
                        ViewComponent: UserDetailsView,
                        viewTitle: i18n?.user_management_view_label || "User details",
                    }}
                    createActionProps={{
                        createLabel: i18n?.user_management_create_label || "Create User",
                        createAction: () => {
                            router.push("/user/createUser");
                        },
                    }}
                    deleteActionProps={{
                        deleteLabel: i18n?.user_management_delete_label || "Delete User",
                        deleteConfirmationText: (selectedUser) =>
                            `${i18n?.user_management_delete_confirmation || "Are you sure you would like to delete ${}"}`.replace(
                                "${}",
                                `${selectedUser?.firstname} ${selectedUser?.lastname}`
                            ),
                        deleteAction: deleteUser.mutate,
                    }}
                    emptyProps={{
                        emptyMessage: i18n?.user_management_empty_label || "No users available",
                        emptyActionLabel: i18n?.user_management_empty_action || "Create User",
                    }}
                />
            </Box>
        </>
    );
}
