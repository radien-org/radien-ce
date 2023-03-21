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
import usePaginatedUsersForTenant from "@/hooks/usePaginatedUsersForTenant";
import { getColDefinitionUser } from "@/utils/tablesColDefinitions";

export default function UserManagement() {
    const {
        i18n,
        userInSession: radienUser,
        addSuccessMessage,
        activeTenant: { data: tenantId, isLoading: isLoadingActiveTenant },
    } = useContext(RadienContext);
    const deleteUser = useDeleteUser();
    const router = useRouter();

    const colDefinition: TableProps.ColumnDefinition<User>[] = getColDefinitionUser(i18n);
 
    return (
        <>
            <TenantRequestsTable />
            <Box padding={"xl"}>
                <PaginatedTable
                    tableHeader={i18n?.user_management_header || "User Management"}
                    queryKey={QueryKeys.USER_MANAGEMENT}
                    manipulationEnableCondition={!radienUser?.processingLocked}
                    columnDefinitions={colDefinition}
                    getPaginated={(pageNumber, pageSize) => usePaginatedUsersForTenant({ tenantId: tenantId?.tenantId!, pageNo: pageNumber, pageSize })}
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
                        onDeleteSuccess: () => addSuccessMessage(i18n?.user_management_delete_success || "User deleted successfully."),
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
