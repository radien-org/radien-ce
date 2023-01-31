import React, {useEffect, useState} from "react";
import {
    Box,
    Button,
    ButtonDropdown, ButtonDropdownProps,
    Container,
    Form,
    Header,
    Input,
    SpaceBetween,
    TableProps
} from "@cloudscape-design/components";
import {Tenant, Ticket, User} from "radien";
import {useUserInSession} from "@/hooks/useUserInSession";
import axios from "axios";
import {v4 as uuidv4} from 'uuid';
import moment from 'moment';
import dynamic from "next/dynamic";
import {QueryKeys, TicketType} from "@/consts";
import {PaginatedTableProps} from "@/components/PaginatedTable/PaginatedTable";
import useUpdateUser from "@/hooks/useUpdateUser";
import useDissociateTenant from "@/hooks/useDissociateTenant";
import {FlashbarContext} from "@/context/FlashbarContext";

const FormField = dynamic(
    () => import("@cloudscape-design/components/form-field"),
    { ssr: false}
);
const PaginatedTable = dynamic(
    () => import("@/components/PaginatedTable/PaginatedTable"),
    { ssr: false}
) as React.ComponentType<PaginatedTableProps<Tenant>>

export default function UserProfile() {
    const flashbarContext = React.useContext(FlashbarContext);
    const { userInSession: radienUser, isLoadingUserInSession } = useUserInSession();
    const updateUser = useUpdateUser()
    const dissociateUser = useDissociateTenant();

    const [ selectedTenant, setSelectedTenant ] = useState<Tenant>();

    const [ firstName, setFirstName ] = useState<string>(radienUser?.data.firstname || '');
    const [ lastName, setLastName ] = useState<string>(radienUser?.data.lastname || '');
    const [ logon, setLogon ] = useState<string>(radienUser?.data.logon || '');
    const [ userEmail, setUserEmail ] = useState<string>(radienUser?.data.userEmail || '');
    const [ sub, setSub ] = useState<string>(radienUser?.data.sub || '');

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

    const getTenantPage = (pageNumber: number = 1, pageSize: number = 10) => {
        return axios.get("/api/role/tenantroleuser/getTenants", {
            params: {
                userId: radienUser?.data.id,
            }
        });
    }

    const dropdownClickEvent = async (event: CustomEvent<ButtonDropdownProps.ItemClickDetails>) => {
        if(event.detail.id == "dataReq") {
            // TODO: Figure out replacement for JSF servlet!
            // TODO: Include language
            const uuid = uuidv4();
            const ticket: Ticket = {
                userId: Number(radienUser?.data.id),
                token: uuid,
                ticketType: TicketType.GDPR_DATA_REQUEST,
                data: "",
                createUser: Number(radienUser?.data.id),
                expireDate: moment().add(24, "hours").toDate()
            }
            await axios.post("/api/ticket/createTicket", ticket);

            const referenceUrl: string = `${process.env.NEXT_PUBLIC_RADIEN_WEB_URL}/confirmData?ticket=${uuid}")`;
            const viewId: string = "email-7";
            const args = {
                firstName: radienUser?.data.firstname,
                lastName: radienUser?.data.lastname,
                portalUrl: "radien",
                targetUrl: referenceUrl
            }
            await axios.post("/api/notification/notifyCurrentUser", args, {
                params: {
                    viewId,
                    language: "en"
                }
            })
            flashbarContext.addSuccessMessage("Successfully requested data. Please check your email for more details.");
        }
    }

    const tenantDetailsView = (
        <div>
            <div><b>Tenant Key:</b> {selectedTenant?.tenantKey}</div>
            <div><b>Tenant Name:</b> {selectedTenant?.name}</div>
            <div><b>Tenant Type:</b> {selectedTenant?.tenantType}</div>
        </div>
    )

    return (
        <>
            <Box padding="xl">
                <Container
                    header={
                        <Header
                            variant="h1"
                            description="Here you can manage your profile details"
                            actions={
                                <ButtonDropdown variant="icon" onItemClick={(event) => dropdownClickEvent(event)}
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
            <Box padding={"xl"}>
                <Container>
                    <PaginatedTable
                        tableHeader={"Associated Tenants"}
                        tableVariant={"embedded"}
                        queryKey={QueryKeys.AVAILABLE_TENANTS}
                        columnDefinitions={colDefinition}
                        getPaginated={getTenantPage}
                        selectedItemDetails={
                            {
                                selectedItem: selectedTenant,
                                setSelectedItem: setSelectedTenant
                            }
                        }
                        viewActionProps={
                            {
                                viewComponent: tenantDetailsView
                            }
                        }
                        createActionProps={
                            {
                                createLabel: "Request Tenant"
                            }
                        }
                        deleteActionProps={
                            {
                                deleteLabel: "Dissociate Tenant",
                                deleteConfirmationText: `Are you sure you would like to dissociate ${selectedTenant?.name}`,
                                deleteAction: dissociateUser.mutate

                            }
                        }
                        emptyProps={
                            {
                                emptyMessage: "No tenants available",
                                emptyActionLabel: "Request Tenant"
                            }
                        }
                    />
                </Container>
            </Box>
        </>

    )
}