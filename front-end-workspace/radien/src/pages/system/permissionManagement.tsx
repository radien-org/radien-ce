import React, {useContext} from "react";
import {Permission} from "radien";
import axios from "axios";
import PaginatedTable, {DeleteParams} from "@/components/PaginatedTable/PaginatedTable";
import {Box, TableProps} from "@cloudscape-design/components";
import dynamic from "next/dynamic";
import {QueryKeys} from "@/consts";
import {RadienContext} from "@/context/RadienContextProvider";
import {useRouter} from "next/router";
import useDeletePermission from "@/hooks/useDeletePermission";
import {useQuery} from "react-query";

export default function PermissionManagement() {
    const pageSize = 10;
    const router = useRouter();

    const {i18n} = useContext(RadienContext);
    const deletePermission = useDeletePermission()


    const loadActions = async () => {
        return await axios.get("/api/action/getAll");
    }

    const loadResources = async () => {
        return await axios.get("/api/resource/getAll");
    }

    const aggregateActions = (actions: any, data: Permission[]) => {
        return data.map((permission: any) => ({
            ...permission,
            action: actions.data.find((action: any) => action.id === permission.actionId),
        }));
    }
    const aggregateResources = (resources: any, data: Permission[]) => {
        return data.map((permission: any) => ({
            ...permission,
            resource: resources.data.find((action: any) => action.id === permission.actionId),
        }));
    }

    const colDefinition: TableProps.ColumnDefinition<Permission>[] =  [
            {
                id: "name",
                header: i18n?.permission_management_column_name || "Name",
                cell: (item: Permission) => item?.name || "-",
                sortingField: "name"
            },{
                id: "actionId",
                header: i18n?.permission_management_column_action || "Action",
                cell: (item: Permission) => item?.action?.name.toString() || "-",
                sortingField: "actionId"
            },
            {
                id: "resourceId",
                header: i18n?.permission_management_column_resource || "Resource",
                cell: (item: Permission) => item?.resource?.name.toString() || "-",
                sortingField: "resourceId"
            }
        ]



    const getPermissionPage = async (pageNumber: number = 1, pageSize: number = 10) => {
        return await axios.get("/api/permission/permission/getAll", {
            params: {
                page: pageNumber,
                pageSize: pageSize
            }
        });
    }

    return (
        <Box padding={"xl"}>
            <PaginatedTable
                tableHeader={i18n?.permission_management_header || "Permission Management"}
                queryKey={QueryKeys.PERMISSION_MANAGEMENT}
                columnDefinitions={colDefinition}
                getPaginated={getPermissionPage}
                aggregateProps={{
                    aggregates: [aggregateActions, aggregateResources],
                    loaders: [loadActions, loadResources],
                    queryKeys: [[QueryKeys.PERMISSION_MANAGEMENT, 'actions'], [QueryKeys.PERMISSION_MANAGEMENT, 'resources']]
                }}
                viewActionProps={{}}
                createActionProps={{
                    createLabel: i18n?.permission_management_create_label || "Create permission",
                    createAction: () => {
                        router.push('/permission/createPermission');
                    }
                }}
                deleteActionProps={
                    {
                        deleteLabel: i18n?.permission_management_delete_label || "Delete Permission",
                        deleteConfirmationText: (selectedPermission) => `${i18n?.permission_management_delete_confirmation ||  "Are you sure you would like to delete ${}"}`.replace("${}", selectedPermission?.name!),
                        deleteAction: deletePermission.mutate
                    }
                }
                emptyProps={
                    {
                        emptyMessage: i18n?.permission_management_empty_label || "No permissions available",
                        emptyActionLabel: i18n?.permission_management_empty_action || "Create Permission"
                    }
                }
            />
        </Box>
    )
}