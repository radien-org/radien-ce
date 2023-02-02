import React, {useContext, useState} from "react";
import {RadienModel, Tenant} from "radien";
import axios from "axios";
import {DeleteParams, PaginatedTableProps} from "@/components/PaginatedTable/PaginatedTable";
import {Box, TableProps} from "@cloudscape-design/components";
import dynamic from "next/dynamic";
import {QueryKeys} from "@/consts";
import {RadienContext} from "@/context/RadienContextProvider";

export default function TenantManagement() {
    const PaginatedTable = dynamic(
        () => import("@/components/PaginatedTable/PaginatedTable"),
        { ssr: false}
    ) as React.ComponentType<PaginatedTableProps<Tenant>>



    const {addSuccessMessage, addErrorMessage} = useContext(RadienContext);

    const colDefinition: TableProps.ColumnDefinition<Tenant>[] = [
        {
            id: "name",
            header: "Name",
            cell: (item: Tenant) => item?.name || "-",
            sortingField: "name"
        },{
            id: "tenantKey",
            header: "Key",
            cell: (item: Tenant) => item?.tenantKey || "-",
            sortingField: "tenantKey"
        },
        {
            id: "tenantType",
            header: "Type",
            cell: (item: Tenant) => item?.tenantType || "-",
            sortingField: "tenantType"
        },
        {
            id: "tenantStart",
            header: "Start Date",
            cell: (item: Tenant) => item?.tenantStart.toString() || "-",
            sortingField: "tenantStart"
        },
        {
            id: "tenantEnd",
            header: "End Date",
            cell: (item: Tenant) => item?.tenantEnd.toString() || "-",
            sortingField: "tenantEnd"
        },
        {
            id: "clientAddress",
            header: "Address",
            cell: (item: Tenant) => item?.clientAddress || "-",
            sortingField: "clientAddress"
        },
        {
            id: "clientZipCode",
            header: "Zip Code",
            cell: (item: Tenant) => item?.clientZipCode || "-",
            sortingField: "clientZipCode"
        },
        {
            id: "clientCity",
            header: "City",
            cell: (item: Tenant) => item?.clientCity || "-",
            sortingField: "clientCity"
        },
        {
            id: "clientCountry",
            header: "Country",
            cell: (item: Tenant) => item?.clientCountry || "-",
            sortingField: "clientCountry"
        },
        {
            id: "clientPhoneNumber",
            header: "Phone Number",
            cell: (item: Tenant) => item?.clientPhoneNumber || "-",
            sortingField: "clientPhoneNumber"
        },
        {
            id: "clientEmail",
            header: "Email",
            cell: (item: Tenant) => item?.clientEmail || "-",
            sortingField: "clientEmail"
        },
        {
            id: "parentId",
            header: "Parent Tenant",
            cell: (item: Tenant) => item?.parentId || "-",
            sortingField: "parentId"
        },
        {
            id: "clientId",
            header: "Client Tenant",
            cell: (item: Tenant) => item?.clientId || "-",
            sortingField: "clientId"
        },
    ]

    const getTenantPage = async (pageNumber: number = 1, pageSize: number = 10) => {
        return await axios.get("/api/tenant/tenant/getAll", {
            params: {
                page: pageNumber,
                pageSize: pageSize
            }
        });
    }



    const deleteTenant = async (data: DeleteParams) => {
       try {
           await axios.delete(`/api/tenant/delete/${data.tenantId}`);
           addSuccessMessage("Tenant deleted successfully");
       } catch (e) {
           addErrorMessage("Failed to delete tenant");
       }
    }

    return (
        <Box padding={"xl"}>
            <PaginatedTable
                tableHeader={"Tenant Management"}
                queryKey={QueryKeys.TENANT_MANAGEMENT}
                columnDefinitions={colDefinition}
                getPaginated={getTenantPage}
                viewActionProps={{}}
                createActionProps={{}}
                deleteActionProps={
                    {
                        deleteLabel: "Delete Tenant",
                        deleteConfirmationText: (selectedTenant) => `Are you sure you would like to delete ${selectedTenant?.name}`,
                        deleteAction: deleteTenant
                    }
                }
                emptyProps={
                    {
                        emptyMessage: "No tenants available",
                        emptyActionLabel: "Create Tenant"
                    }
                }
            />
        </Box>
    )
}
