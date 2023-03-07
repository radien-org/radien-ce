import React, { useEffect, useState } from "react";
import {
    Box,
    Button,
    ButtonDropdown,
    ButtonDropdownProps,
    Container,
    Form,
    Header,
    Input,
   Modal, SpaceBetween,
    TableProps
,
} from "@cloudscape-design/components";
import { Tenant, Ticket, User } from "radien";
import { v4 as uuidv4 } from "uuid";
import moment from "moment";
import dynamic from "next/dynamic";
import { QueryKeys, TicketType } from "@/consts";
import PaginatedTable from "@/components/PaginatedTable/PaginatedTable";
import useUpdateUser from "@/hooks/useUpdateUser";
import useDissociateTenant from "@/hooks/useDissociateTenant";
import { RadienContext } from "@/context/RadienContextProvider";
import { useRouter } from "next/router";
import useCreateTicket from "@/hooks/useCreateTicket";
import TenantRequestModal from "@/components/TenantRequest/TenantRequestModal";
import ChangePasswordModal from "@/components/UserDetailsView/ChangePasswordModal";
import useNotifyUser from "@/hooks/useNotifyUser";
import usePaginatedUserTenants from "@/hooks/usePaginatedUserTenants";
import useDeleteUser from "@/hooks/useDeleteUser";
import { useQueryClient } from "react-query";
import { logout } from "@/components/Header/Header";
import {ConfirmationModalComponent} from "@/components/ConfirmationModal/ConfirmationModalComponent";

const FormField = dynamic(() => import("@cloudscape-design/components/form-field"), { ssr: false });

export default function UserProfile() {
    const processingLockButtonID: string = "lockUser";

    const { addSuccessMessage, userInSession: radienUser, isLoadingUserInSession, i18n } = React.useContext(RadienContext);
    const queryClient = useQueryClient();
    const { locale } = useRouter();
    const updateUser = useUpdateUser();
    const dissociateUser = useDissociateTenant();
    const createTicket = useCreateTicket();
    const notifyUser = useNotifyUser();
    const deleteUser = useDeleteUser();

    const [firstName, setFirstName] = useState<string>(radienUser?.firstname || "");
    const [lastName, setLastName] = useState<string>(radienUser?.lastname || "");
    const [logon, setLogon] = useState<string>(radienUser?.logon || "");
    const [userEmail, setUserEmail] = useState<string>(radienUser?.userEmail || "");
    const [sub, setSub] = useState<string>(radienUser?.sub || "");
    const [processingLocked, setProcessingLocked] = useState<boolean>(radienUser?.processingLocked || false);
    const [processingLockedModalVisible, setProcessingLockedModalVisible] = useState<boolean>(false);

    const [requestTenantVisibility, setRequestTenantVisibility] = useState(false);
    const [requestPasswordChangeVisibility, setRequestPasswordChange] = useState(false);
    const [selfDeletionVisibility, setSelfDeletionVisibility] = useState(false);

    useEffect(() => {
        setFirstName(radienUser?.firstname || "");
        setLastName(radienUser?.lastname || "");
        setLogon(radienUser?.logon || "");
        setUserEmail(radienUser?.userEmail || "");
        setSub(radienUser?.sub || "");
        console.log("processing locked: "+ radienUser?.processingLocked)
        setProcessingLocked(radienUser?.processingLocked || false);
    }, [radienUser]);

    if (isLoadingUserInSession) {
        return <div>Loading...</div>;
    }

    const saveData = () => {
        updateUser.mutate(cloneCurrentUser());
    };

    const cloneCurrentUser = () : User => {
        const radUser: User = radienUser!;
        radUser.firstname = firstName;
        radUser.lastname = lastName;
        radUser.logon = logon;
        radUser.userEmail = userEmail;
        radUser.sub = sub;

        return radUser;
    }

    const processingLock = () : void => {
        const radUser: User = cloneCurrentUser();
        radUser.processingLocked = true;

        updateUser.mutate(radUser);
        addSuccessMessage(i18n?.user_profile_processing_lock_success || "Successfully locked the processing of this account's data.");
    }

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

    const dropdownClickEvent = async (event: CustomEvent<ButtonDropdownProps.ItemClickDetails>) => {
        if (event.detail.id == "changePwd") {
            setRequestPasswordChange(true);
        }

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
            const viewId: string = "email-9";
            const args = {
                firstName: radienUser?.firstname,
                lastName: radienUser?.lastname,
                portalUrl: "radien",
                targetUrl: referenceUrl,
            };

            notifyUser.mutate({
                email: radienUser?.userEmail!,
                viewId,
                language: locale,
                params: args,
            });
            addSuccessMessage("Successfully requested data. Please check your email for more details.");
        } else if (event.detail.id == "delUser") {
            setSelfDeletionVisibility(true);
        }
        if(event.detail.id == processingLockButtonID){
            setProcessingLockedModalVisible(true);
        }
    };

    return (
        <>
            <TenantRequestModal modalVisible={requestTenantVisibility} setModalVisible={setRequestTenantVisibility} />
            <ConfirmationModalComponent header={i18n?.user_profile_processing_lock_confirmation_header || "Processing Lock"}
                                        body={i18n?.user_profile_processing_lock_confirmation_body || "Are you sure you would like to lock the processing of your account's data? This action will lock the processing of all user data including deletion requests."}
                                        modalVisible={processingLockedModalVisible}
                                        setModalVisible={setProcessingLockedModalVisible}
                                        closeModalOnConfirm={true}
                                        confirmBehaviour={processingLock}/>

            <ChangePasswordModal modalVisible={requestPasswordChangeVisibility} setModalVisible={setRequestPasswordChange} />
            <Modal
                onDismiss={() => setSelfDeletionVisibility(false)}
                visible={selfDeletionVisibility}
                closeAriaLabel="Close modal"
                footer={
                    <Box float="right">
                        <SpaceBetween direction="horizontal" size="xs">
                            <Button
                                variant="primary"
                                onClick={() =>
                                    deleteUser.mutate(
                                        { objectId: String(radienUser!.id!), userId: String(radienUser!.id!) },
                                        {
                                            onSuccess: async () => {
                                                setSelfDeletionVisibility(false);
                                                await logout(queryClient);
                                            },
                                        }
                                    )
                                }>
                                {i18n?.user_profile_self_deletion_confirm || "Ok"}
                            </Button>
                            <Button variant="link" onClick={() => setSelfDeletionVisibility(false)}>
                                {i18n?.user_profile_self_deletion_cancel || "Cancel"}
                            </Button>
                        </SpaceBetween>
                    </Box>
                }
                header={i18n?.user_profile_self_deletion_header || "Delete User Account"}>
                {i18n?.user_profile_self_deletion_confirmation ||
                    "Are you sure you would like to delete your user account? After confirmation your user account and all user data will be deleted."}
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
                                        { text: i18n?.user_profile_request_user_data || "Request User Data", id: "dataReq", disabled: false },
                                        { text: i18n?.user_profile_lock_label || "Lock Account from Processing", id: processingLockButtonID, disabled: false },
                                        { text: i18n?.password_change_header || "Change password", id: "changePwd", disabled: false },
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
                                    <Button variant="primary" disabled={processingLocked} formAction={"submit"}>
                                        {i18n?.user_profile_submit_action || "Submit"}
                                    </Button>
                                </SpaceBetween>
                            }>
                            <FormField label={i18n?.user_profile_firstname || "First name"} stretch={false}>
                                <Input value={firstName} disabled={processingLocked} onChange={(event) => setFirstName(event.detail.value)} />
                            </FormField>
                            <FormField label={i18n?.user_profile_lastname || "Last name"} stretch={false}>
                                <Input value={lastName} disabled={processingLocked} onChange={(event) => setLastName(event.detail.value)} />
                            </FormField>
                            <FormField label={i18n?.user_profile_username || "Username"} stretch={false}>
                                <Input value={logon} disabled={processingLocked} onChange={(event) => setLogon(event.detail.value)} />
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
                        queryKey={QueryKeys.ASSIGNED_TENANTS}
                        columnDefinitions={colDefinition}
                        getPaginated={() => usePaginatedUserTenants({ userId: radienUser?.id! })}
                        viewActionProps={{}}
                        createActionProps={{
                            createLabel: i18n?.user_profile_tenants_create_label || "Request Tenant",
                            createAction: () => setRequestTenantVisibility(true),
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
