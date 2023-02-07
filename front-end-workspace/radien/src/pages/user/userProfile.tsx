import React, {useEffect, useState} from "react";
import {
    Box,
    Button,
    ButtonDropdown, ButtonDropdownProps,
    Container,
    Form,
    Header,
    Input, Modal, Multiselect,
    SpaceBetween,
    TableProps
} from "@cloudscape-design/components";
import {Tenant, Ticket, User} from "radien";
import axios from "axios";
import { v4 as uuidv4 } from "uuid";
import moment from "moment";
import dynamic from "next/dynamic";
import { QueryKeys, TicketType } from "@/consts";
import { PaginatedTableProps } from "@/components/PaginatedTable/PaginatedTable";
import useUpdateUser from "@/hooks/useUpdateUser";
import useDissociateTenant from "@/hooks/useDissociateTenant";
import { RadienContext } from "@/context/RadienContextProvider";
import useAllTenantListed from "@/hooks/useAllTenantListed";
import { useRouter } from "next/router";
import useCreateTicket from "@/hooks/useCreateTicket";
import useNotifyCurrentUser from "@/hooks/useNotifyCurrentUser";
import {OptionDefinition} from "@cloudscape-design/components/internal/components/option/interfaces";

const FormField = dynamic(() => import("@cloudscape-design/components/form-field"), { ssr: false });
const PaginatedTable = dynamic(() => import("@/components/PaginatedTable/PaginatedTable"), { ssr: false }) as React.ComponentType<PaginatedTableProps<Tenant>>;

export default function UserProfile() {
    const { addSuccessMessage, userInSession: radienUser, isLoadingUserInSession, i18n } = React.useContext(RadienContext);
    const { locale } = useRouter();
    const updateUser = useUpdateUser();
    const dissociateUser = useDissociateTenant();
    const createTicket = useCreateTicket();
    const notifyCurrentUser = useNotifyCurrentUser();

    const [ requestTenant, setRequestTenantModalVisible ] = useState(false);
    const [ selectedOptions, setSelectedOptions ] = useState<OptionDefinition[]>([]);
    const { data } = useAllTenantListed();

    const [selectedTenant, setSelectedTenant] = useState<Tenant>();

    const [firstName, setFirstName] = useState<string>(radienUser?.firstname || "");
    const [lastName, setLastName] = useState<string>(radienUser?.lastname || "");
    const [logon, setLogon] = useState<string>(radienUser?.logon || "");
    const [userEmail, setUserEmail] = useState<string>(radienUser?.userEmail || "");
    const [sub, setSub] = useState<string>(radienUser?.sub || "");

    useEffect(() => {
        setFirstName(radienUser?.firstname || "");
        setLastName(radienUser?.lastname || "");
        setLogon(radienUser?.logon || "");
        setUserEmail(radienUser?.userEmail || "");
        setSub(radienUser?.sub || "");
    }, [radienUser]);

    if (isLoadingUserInSession) {
        return <div>Loading...</div>;
    }

    const saveData = () => {
        const radUser: User = radienUser!;
        radUser.firstname = firstName;
        radUser.lastname = lastName;
        radUser.logon = logon;
        radUser.userEmail = userEmail;
        radUser.sub = sub;

        updateUser.mutate(radUser);
    };

    const colDefinition: TableProps.ColumnDefinition<Tenant>[] = [
        {
            id: "key",
            header: i18n?.user_profile_tenants_column_key || "Key",
            cell: (item: Tenant) => item?.tenantKey || "-",
            sortingField: "key",
        },
        {
            id: "name",
            header: i18n?.user_profile_tenants_column_name || "Name",
            cell: (item: Tenant) => item?.name || "-",
            sortingField: "name",
        },
        {
            id: "type",
            header: i18n?.user_profile_tenants_column_type || "Type",
            cell: (item: Tenant) => item?.tenantType || "-",
            sortingField: "type",
        },
    ];

    const getTenantPage = (pageNumber: number = 1, pageSize: number = 10) => {
        return axios.get("/api/role/tenantroleuser/getTenants", {
            params: {
                userId: radienUser?.id,
            },
        });
    };

    const sendEmailToAdministrator = () => {
        console.log("Carreguei no ok");
    }

    const dropdownClickEvent = async (event: CustomEvent<ButtonDropdownProps.ItemClickDetails>) => {
        if (event.detail.id == "dataReq") {
            const uuid = uuidv4();
            const ticket: Ticket = {
                userId: Number(radienUser?.id),
                token: uuid,
                ticketType: TicketType.GDPR_DATA_REQUEST,
                data: "",
                createUser: Number(radienUser?.id),
                expireDate: moment().add(24, "hours").toDate(),
            };
            createTicket.mutate(ticket);

            const referenceUrl: string = `${process.env.NEXTAUTH_URL}/api/data-privacy/dataRequest?ticket=${uuid}")`;
            const viewId: string = "email-7";
            const args = {
                firstName: radienUser?.firstname,
                lastName: radienUser?.lastname,
                portalUrl: "radien",
                targetUrl: referenceUrl,
            };
            await axios.post("/api/notification/notifyCurrentUser", args, {
                params: {
                    viewId,
                    language: "en"
                }
            })
            addSuccessMessage("Successfully requested data. Please check your email for more details.");
        } else if(event.detail.id == "tenantReq") {
            setRequestTenantModalVisible(true)
        }
    }

    return (
        <>
            <Modal
                onDismiss={() => setRequestTenantModalVisible(false)}
                visible={requestTenant}
                closeAriaLabel="Close modal"
                footer={
                    <Box float="right">
                        <SpaceBetween direction="horizontal" size="xs">
                            <Button variant="primary" onClick={() => sendEmailToAdministrator()}>Ok</Button>
                            <Button variant="link" onClick={() => setRequestTenantModalVisible(false)}>Cancel</Button>
                        </SpaceBetween>
                    </Box>
                }
                header="Request tenant">
                <Multiselect
                    selectedOptions={selectedOptions}
                    onChange={({ detail }) =>
                        setSelectedOptions(() => [...detail.selectedOptions])
                    }
                    deselectAriaLabel={e => `Remove ${e.label}`}
                    options={ data?.map(t => { return {label: t.name, value: String(t.id)} }) }
                    placeholder="Choose options"
                    selectedAriaLabel="Selected"
                />
            </Modal>

            <Box padding="xl">
                <Container
                    header={
                        <Header
                            variant="h1"
                            description={i18n?.user_profile_header_description || "Here you can manage your profile details"}
                            actions={
                                <ButtonDropdown
                                    variant="icon"
                                    onItemClick={(event) => dropdownClickEvent(event)}
                                    items={[
                                        { text: i18n?.user_profile_delete_label || "Delete", id: "delUser", disabled: false },
                                        { text: i18n?.user_profile_request_tenant || "Request Tenant", id: "tenantReq", disabled: false },
                                        { text: i18n?.user_profile_request_user_data || "Request User Data", id: "dataReq", disabled: false },
                                    ]}></ButtonDropdown>
                            }>
                            User Profile
                        </Header>
                    }>
                    <form
                        onSubmit={(e) => {
                            e.preventDefault();
                            saveData();
                        }}>
                        <Form
                            actions={
                                <SpaceBetween direction="horizontal" size="xs">
                                    <Button formAction="none" variant="link">
                                        {i18n?.user_profile_cancel_action || "Cancel"}
                                    </Button>
                                    <Button variant="primary" formAction={"submit"}>
                                        {i18n?.user_profile_submit_action || "Submit"}
                                    </Button>
                                </SpaceBetween>
                            }>
                            <FormField label={i18n?.user_profile_firstname || "First name"} stretch={false}>
                                <Input value={firstName} onChange={(event) => setFirstName(event.detail.value)} />
                            </FormField>
                            <FormField label={i18n?.user_profile_lastname || "Last name"} stretch={false}>
                                <Input value={lastName} onChange={(event) => setLastName(event.detail.value)} />
                            </FormField>
                            <FormField label={i18n?.user_profile_username || "Username"} stretch={false}>
                                <Input value={logon} onChange={(event) => setLogon(event.detail.value)} />
                            </FormField>
                            <FormField label={i18n?.user_profile_email || "Email"} stretch={false}>
                                <Input value={userEmail} disabled={true} onChange={(event) => setUserEmail(event.detail.value)} />
                            </FormField>
                            <FormField label={i18n?.user_profile_subject || "Subject Id"} stretch={false}>
                                <Input value={sub} disabled={true} onChange={(event) => setSub(event.detail.value)} />
                            </FormField>
                        </Form>
                    </form>
                </Container>
            </Box>
            <Box padding={"xl"}>
                <Container>
                    <PaginatedTable
                        tableHeader={i18n?.user_profile_tenants_table_header || "Associated Tenants"}
                        tableVariant={"embedded"}
                        queryKey={QueryKeys.AVAILABLE_TENANTS}
                        columnDefinitions={colDefinition}
                        getPaginated={getTenantPage}
                        viewActionProps={{}}
                        createActionProps={{
                            createLabel: i18n?.user_profile_tenants_create_label || "Request Tenant",
                        }}
                        deleteActionProps={{
                            deleteLabel: i18n?.user_profile_tenants_delete_label || "Dissociate Tenant",
                            deleteConfirmationText: (selectedTenant) =>
                                `${i18n?.user_profile_tenants_delete_confirmation || "Are you sure you would like to dissociate ${}"}`.replace(
                                    "${}",
                                    selectedTenant?.name!
                                ),
                            deleteAction: dissociateUser.mutate,
                        }}
                        emptyProps={{
                            emptyMessage: i18n?.user_profile_tenants_empty_label || "No tenants available",
                            emptyActionLabel: i18n?.user_profile_tenants_empty_action || "Request Tenant",
                        }}
                    />
                </Container>
            </Box>
        </>
    );
}
