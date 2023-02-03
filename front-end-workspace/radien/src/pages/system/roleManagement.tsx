import React, {useContext} from "react";
import {Role} from "radien";
import axios from "axios";
import PaginatedTable from "@/components/PaginatedTable/PaginatedTable";
import {Box, TableProps} from "@cloudscape-design/components";
import {QueryKeys} from "@/consts";
import {RadienContext} from "@/context/RadienContextProvider";

export default function RoleManagement() {
    const { i18n } = useContext(RadienContext);

    const colDefinition: TableProps.ColumnDefinition<Role>[] = [
        {
            id: "name",
            header: i18n?.role_management_column_name || "Name",
            cell: (item: Role) => item?.name || "-",
            sortingField: "name"
        },{
            id: "description",
            header: i18n?.role_management_column_description || "Description",
            cell: (item: Role) => item?.description || "-",
            sortingField: "description"
        },
        {
            id: "terminationDate",
            header: i18n?.role_management_column_termination_date || "Termination Date",
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
                tableHeader={i18n?.role_management_header || "Role Management"}
                queryKey={QueryKeys.ROLE_MANAGEMENT}
                columnDefinitions={colDefinition}
                getPaginated={getRolePage}
                viewActionProps={{}}
                createActionProps={{}}
                deleteActionProps={
                    {
                        deleteLabel: i18n?.role_management_delete_label || "Delete Role",
                        deleteConfirmationText: (selectedRole) => `${i18n?.role_management_delete_confirmation ||  "Are you sure you would like to delete ${}"}`.replace("${}", selectedRole?.name)
                    }
                }
                emptyProps={
                    {
                        emptyMessage: i18n?.role_management_empty_label || "No roles available",
                        emptyActionLabel: i18n?.role_management_empty_action || "Create Role"
                    }
                }
            />
        </Box>
    )
}