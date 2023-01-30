import React, {useEffect, useState} from "react";
import {Box, Button, ButtonDropdown, Container, Form, Header, Input, SpaceBetween, TableProps} from "@cloudscape-design/components";
import {RadienModel, Tenant, User} from "radien";
import {useUserInSession} from "@/hooks/useUserInSession";
import {useMutation, useQueryClient} from "react-query";
import axios from "axios";
import dynamic from "next/dynamic";
import {QueryKeys} from "@/consts";
import {PaginatedTableProps} from "@/components/PaginatedTable/PaginatedTable";

const FormField = dynamic(
    () => import("@cloudscape-design/components/form-field"),
    { ssr: false}
);
const PaginatedTable = dynamic(
    () => import("@/components/PaginatedTable/PaginatedTable"),
    { ssr: false}
) as React.ComponentType<PaginatedTableProps<Tenant>>

export default function UserProfile() {
    const queryClient = useQueryClient();
    const { userInSession: radienUser, isLoadingUserInSession } = useUserInSession();
    const updateUser = useMutation({
        mutationFn: (updatedUser: User) => axios.post(`/api/user/updateUser`, updatedUser),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: QueryKeys.ME})
        }
    });
    const dissociateUser = useMutation({
        mutationFn: (tenantId: number) => axios.delete(`/api/role/tenantroleuser/dissociateTenant`, { params: {tenantId, userId: radienUser?.data.id} }),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: QueryKeys.AVAILABLE_TENANTS})
            queryClient.invalidateQueries({queryKey: QueryKeys.ACTIVE_TENANT})
        }
    });

    const [ firstName, setFirstName ] = useState<string>(radienUser?.data.firstname || '');
    const [ lastName, setLastName ] = useState<string>(radienUser?.data.lastname || '');
    const [ logon, setLogon ] = useState<string>(radienUser?.data.logon || '');
    const [ userEmail, setUserEmail ] = useState<string>(radienUser?.data.userEmail || '');
    const [ sub, setSub ] = useState<string>(radienUser?.data.sub || '');



    const colDefinition: TableProps.ColumnDefinition<Tenant>[] = [
        {
            id: "key",
            header: "Key",
            cell: (item: Tenant) => item?.tenantKey || "-",
            sortingField: "key"
        },
        {
            id: "name",
            header: "Name",
            cell: (item: Tenant) => item?.name || "-",
            sortingField: "name"
        },
        {
            id: "type",
            header: "Type",
            cell: (item: Tenant) => item?.tenantType || "-",
            sortingField: "type"
        }
    ]

    useEffect(() => {
        setFirstName(radienUser?.data.firstname || '');
        setLastName(radienUser?.data.lastname || '');
        setLogon(radienUser?.data.logon || '');
        setUserEmail(radienUser?.data.userEmail || '');
        setSub(radienUser?.data.sub || '');
    }, [radienUser])

    if(isLoadingUserInSession) {
        return <div>Loading...</div>
    }

    const saveData = () => {
        const radUser: User = radienUser!.data;
        radUser.firstname = firstName;
        radUser.lastname = lastName;
        radUser.logon = logon;
        radUser.userEmail = userEmail;
        radUser.sub = sub;

        updateUser.mutate(radUser);
    }

    const getTenants = async (pageNumber: number = 1, pageSize: number = 10) => {
        return await axios.get("/api/tenant/getAll", {
            params: {
                userId: radienUser!.data.id,
                page: pageNumber,
                pageSize: pageSize
            }
        });
    }

    return (
        <>
            <Box padding="xl">
                <Container
                    header={
                        <Header
                            variant="h1"
                            description="Here you can manage your profile details"
                            actions={
                                <ButtonDropdown variant="icon"
                                                items={[
                                                    { text: "Delete", id: "delUser", disabled: false },
                                                    { text: "Request Tenant", id: "tenantReq", disabled: false },
                                                    { text: "Request User Data", id: "dataReq", disabled: false }
                                                ]}>
                                </ButtonDropdown>
                            }>
                            User Profile
                        </Header>
                    }>
                    <form onSubmit={e => { e.preventDefault(); saveData()}}>
                        <Form actions={
                            <SpaceBetween direction="horizontal" size="xs">
                                <Button formAction="none" variant="link">
                                    Cancel
                                </Button>
                                <Button variant="primary" formAction={"submit"}>Submit</Button>
                            </SpaceBetween>
                        }>
                            <FormField label="First name" stretch={false}>
                                <Input value={firstName}
                                    onChange={event => setFirstName(event.detail.value)}
                                />
                            </FormField>
                            <FormField label="Last name" stretch={false}>
                                <Input value={lastName}
                                    onChange={event => setLastName(event.detail.value)}
                                />
                            </FormField>
                            <FormField label="Username" stretch={false}>
                                <Input value={logon}
                                    onChange={event => setLogon(event.detail.value)}
                                />
                            </FormField>
                            <FormField label="Email" stretch={false}>
                                <Input value={userEmail} disabled={true}
                                    onChange={event => setUserEmail(event.detail.value)}
                                />
                            </FormField>
                            <FormField label="Subject Id" stretch={false}>
                                <Input value={sub} disabled={true}
                                    onChange={event => setSub(event.detail.value)}
                                />
                            </FormField>
                        </Form>
                    </form>
                </Container>
            </Box>
            <PaginatedTable
                queryKey={QueryKeys.AVAILABLE_TENANTS}
                getPaginated={getTenants}
                columnDefinitions={colDefinition}
                deleteConfirmationText={"Are you sure you would like to dissociate yourself from this tenant?"}
                tableHeader={"User Tenants"}
                hideCreate={true}
                deleteAction={dissociateUser.mutate}
                emptyAction={"Request Tenant"}
                emptyMessage={"No tenants found"}/>
        </>

    )
}