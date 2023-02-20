import {RadienModel, Tenant, User} from "radien";
import useActiveTenant from "@/hooks/useActiveTenant";
import {Box, Container, DatePicker, FormField, Input, TableProps} from "@cloudscape-design/components";
import {Loader} from "@/components/Loader/Loader";
import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button from "@cloudscape-design/components/button";
import Header from "@cloudscape-design/components/header";
import * as React from "react";
import {useContext, useEffect} from "react";
import {RadienContext} from "@/context/RadienContextProvider";
import useAssignedTenants from "@/hooks/useAssignedTenants";
import useUpdateTenant from "@/hooks/useUpdateTenant";
import PaginatedTable from "@/components/PaginatedTable/PaginatedTable";
import {QueryKeys} from "@/consts";
import usePaginatedUsers from "@/hooks/usePaginatedUsers";
import UserDetailsView from "@/components/UserDetailsView/UserDetailsView";
import usePaginatedUsersForTenant from "@/hooks/usePaginatedUsersForTenant";
import {getColDefinitionUser} from "@/utils/tablesColDefinitions";
import useDeleteUser from "@/hooks/useDeleteUser";
import {useRouter} from "next/router";
import useDissociateTenant from "@/hooks/useDissociateTenant";


export default function TenantAdmin() {
    const CLIENT_TYPE = "CLIENT";
    const [loading, setLoading] = React.useState<boolean>(true);
    const {userInSession: radienUser, activeTenant: { data: tenantId , isLoading: isLoadingActiveTenant }, i18n } = useContext(RadienContext);
    const { isLoading: loadingAssignedTenants, data: assignedTenants } = useAssignedTenants(radienUser?.id!);
    let tenant = assignedTenants?.results.filter((tenant) => tenant.id === tenantId?.tenantId)[0];
    const [name, setName] = React.useState<string>(tenant?.name || "");
    const [tenantKey, setTenantKey] = React.useState<string>(tenant?.tenantKey || "");
    const [tenantType, setTenantType] = React.useState<string>(tenant?.tenantType || "");
    const [tenantStart, setTenantStart] = React.useState<string>(`${tenant?.tenantStart}` || '');
    const [tenantEnd, setTenantEnd] = React.useState<string>(`${tenant?.tenantEnd}` || '');
    const [clientEmail, setClientEmail] = React.useState<string>(tenant?.clientEmail || "");
    const [clientAddress, setClientAddress] = React.useState<string>(tenant?.clientAddress || "");
    const [clientZipCode, setClientZipCode] = React.useState<string>(tenant?.clientZipCode || "");
    const [clientCity, setClientCity] = React.useState<string>(tenant?.clientCity || "");
    const [clientCountry, setClientCountry] = React.useState<string>(tenant?.clientCountry || "");
    const [clientPhoneNumber, setClientPhoneNumber] = React.useState<number>(tenant?.clientPhoneNumber || 0);
    const updateTenant = useUpdateTenant(tenantId?.tenantId!);
    const [isFormSubmitted, setIsFormSubmitted] = React.useState<boolean>(false);

    const validateTextInputNonEmpty = (value: string) => {
        return value.trim().length > 0;
    }
    const validateTextInputEmail = (value: string) => {
        const re = /\S+@\S+\.\S+/;
        return re.test(value);
    }

    const validateDateFuture = (value: string) => {
        const date = new Date(value);
        return date >= new Date();
    }
    const colDefinition: TableProps.ColumnDefinition<User>[] = getColDefinitionUser(i18n);
    const deleteUser = useDeleteUser();
    const router = useRouter();
    const dissociateUser = useDissociateTenant();

    const resetForm = () => {
        tenant = assignedTenants?.results.filter((tenant) => tenant.id === tenantId?.tenantId)[0];
        if (tenant) {
            setName(tenant?.name);
            setTenantKey(tenant?.tenantKey);
            setTenantType(tenant?.tenantType);
            setTenantStart(`${tenant?.tenantStart}`);
            setTenantEnd(`${tenant?.tenantEnd}`);
            setClientAddress(tenant?.clientAddress);
            setClientZipCode(tenant?.clientZipCode);
            setClientCity(tenant?.clientCity);
            setClientCountry(tenant?.clientCountry);
            setClientPhoneNumber(tenant?.clientPhoneNumber);
        }
    }
    useEffect(() => {
        resetForm()
    }, [assignedTenants, radienUser]);
    useEffect(() => {
        if (!isLoadingActiveTenant && !loadingAssignedTenants) {
            setLoading(false);
        }
    }, [isLoadingActiveTenant, loadingAssignedTenants])

    const handleSubmit = async () => {
        setIsFormSubmitted(true);
        const isFormValid = (
            validateTextInputNonEmpty(name) &&
            // validateTextInputNonEmpty(tenantKey) &&
            // validateTextInputNonEmpty(tenantType) &&
            validateTextInputNonEmpty(tenantStart) &&
            validateTextInputNonEmpty(tenantEnd) &&
            // validateTextInputNonEmpty(clientAddress) &&
            // validateTextInputNonEmpty(clientZipCode) &&
            // validateTextInputNonEmpty(clientCity) &&
            // validateTextInputNonEmpty(clientCountry) &&
            // validateTextInputNonEmpty(clientEmail) &&
            // validateTextInputEmail(clientEmail) &&
            // validateTextInputNonEmpty(`${clientPhoneNumber}`) &&
            validateDateFuture(tenantStart) &&
            validateDateFuture(tenantEnd));

        if (!isFormValid) {
            return;
        }
        setLoading(true);
        const newTenantInfo: any = {
            name: name.trim(),
            tenantKey,
            tenantType,
            tenantStart,
            tenantEnd,
            clientAddress,
            clientZipCode,
            clientEmail,
            clientCity,
            clientCountry,
            clientPhoneNumber,
        }
        updateTenant.mutateAsync(newTenantInfo).catch(e => {
            console.log(e);
        }).finally(() => {
            setLoading(false);
        });
    };

    return <div>
        <Box padding="xl">
            <Container>
                {loading && <Loader />}

                {tenant && <form
                    onSubmit={(e) => {
                        e.preventDefault();
                        handleSubmit();
                    }}
                    className={"create-form--container"}>
                    <Form
                        actions={
                            <SpaceBetween direction="horizontal" size="xs">
                                <Button formAction="none" variant="normal" onClick={resetForm}>
                                    {i18n?.update_tenant_cancel_action || "Cancel"}
                                </Button>
                                <Button variant="primary">{i18n?.update_tenant_action || "Update"}</Button>
                            </SpaceBetween>
                        }
                        header={<Header variant="h1">{i18n?.update_tenant_form_header || "Tenant information"}</Header>}>
                        <SpaceBetween direction="vertical" size="l">
                            <FormField
                                key="tenant-form-1"
                                label={i18n?.update_tenant_name || "Name"}
                                errorText={!validateTextInputNonEmpty(name) && isFormSubmitted ? i18n?.update_tenant_name_error || "Please enter a valid tenant name" : null}>
                                <Input
                                    value={name}
                                    onChange={(event) => {
                                        setName(event.detail.value);
                                    }}
                                />
                            </FormField>
                            <FormField
                                key="tenant-form-2"
                                label={i18n?.update_tenant_key || "Tenant key"}>
                                <Input
                                    value={tenantKey}
                                    disabled={true}
                                />
                            </FormField>
                            <FormField
                                key="tenant-form-3"
                                label={i18n?.update_tenant_type || "Tenant type"}
                            >
                                <Input
                                    value={tenantType}
                                    disabled={true}
                                />
                            </FormField>
                            <FormField
                                key={"tenant-form-4"}
                                label={i18n?.update_tenant_start_date || "Tenant start date"}
                                constraintText="Use YYYY/MM/DD format."
                                errorText={!validateDateFuture(tenantStart) && isFormSubmitted ? i18n?.update_tenant_tenant_start_date || "Please enter a valid tenant start date" : null}>
                                <DatePicker
                                    onChange={({ detail }) => setTenantStart(detail.value)}
                                    value={tenantStart}
                                    openCalendarAriaLabel={selectedDate =>
                                        "Choose tenant start date" +
                                        (selectedDate
                                            ? `, selected date is ${selectedDate}`
                                            : "")
                                    }
                                    isDateEnabled={date => date >= new Date()}
                                    nextMonthAriaLabel="Next month"
                                    placeholder="YYYY/MM/DD"
                                    previousMonthAriaLabel="Previous month"
                                    todayAriaLabel="Today"
                                />
                            </FormField>
                            <FormField
                                key={"tenant-form-5"}
                                label={i18n?.update_tenant_end_date || "Tenant end date"}
                                constraintText="Use YYYY/MM/DD format."
                                errorText={!validateDateFuture(tenantEnd) && isFormSubmitted ? i18n?.update_tenant_tenant_start_end || "Please enter a valid tenant end date" : null}>
                                <DatePicker
                                    onChange={({ detail }) => setTenantEnd(detail.value)}
                                    value={tenantStart}
                                    openCalendarAriaLabel={selectedDate =>
                                        "Choose tenant end date" +
                                        (selectedDate
                                            ? `, selected date is ${selectedDate}`
                                            : "")
                                    }
                                    isDateEnabled={date => date > new Date()}
                                    nextMonthAriaLabel="Next month"
                                    placeholder="YYYY/MM/DD"
                                    previousMonthAriaLabel="Previous month"
                                    todayAriaLabel="Today"
                                />
                            </FormField>
                            {tenant?.tenantType === CLIENT_TYPE && <>
                                <FormField
                                    key="tenant-form-15"
                                    label={i18n?.update_tenant_country || "Client email"}
                                    errorText={!validateTextInputEmail(clientEmail) && isFormSubmitted ? i18n?.update_tenant_tenant_start_end || "Please enter a valid client email" : null}
                                >
                                    <Input
                                        value={clientEmail}
                                        onChange={(event) => {
                                            setClientEmail(event.detail.value);
                                        }}
                                    />
                                </FormField>
                                <FormField
                                    key="tenant-form-6"
                                    label={i18n?.update_tenant_address || "client address"}
                                >
                                    <Input
                                        value={clientAddress}
                                        onChange={(event) => {
                                            setClientAddress(event.detail.value);
                                        }}
                                    />
                                </FormField>
                                <FormField
                                    key="tenant-form-7"
                                    label={i18n?.update_tenant_zip_code || "client zip code"}
                                >
                                    <Input
                                        value={clientZipCode}
                                        onChange={(event) => {
                                            setClientZipCode(event.detail.value);
                                        }}
                                    />
                                </FormField>
                                <FormField
                                    key="tenant-form-8"
                                    label={i18n?.update_tenant_city || "client city"}
                                >
                                    <Input
                                        value={clientCity}
                                        onChange={(event) => {
                                            setClientCity(event.detail.value);
                                        }}
                                    />
                                </FormField>
                                <FormField
                                    key="tenant-form-10"
                                    label={i18n?.update_tenant_country || "Client country"}
                                >
                                    <Input
                                        value={clientCountry}
                                        onChange={(event) => {
                                            setClientCountry(event.detail.value);
                                        }}
                                    />
                                </FormField>
                            </>}
                        </SpaceBetween>
                    </Form>
                </form>}
            </Container>
        </Box>

        <Box>
            <Box padding={"xl"}>
                <PaginatedTable
                    tableHeader={i18n?.user_management_header || "Associated users"}
                    queryKey={QueryKeys.USER_MANAGEMENT}
                    columnDefinitions={colDefinition}
                    getPaginated={(pageNumber, pageSize) => usePaginatedUsersForTenant({tenantId: tenantId?.tenantId!, pageNo: pageNumber, pageSize })}
                    viewActionProps={{
                        ViewComponent: UserDetailsView,
                        viewTitle: i18n?.user_management_view_label || "User details",
                    }}
                    createActionProps={{
                        createLabel: i18n?.user_management_create_label || "Add user",
                        createAction: () => {
                            router.push("/user/createUser");
                        },
                    }}
                    deleteActionProps={{
                        deleteLabel: i18n?.user_management_delete_label || "Dissociate User",
                        deleteConfirmationText: (selectedUser) =>
                            `${i18n?.user_management_delete_confirmation || "Are you sure you would like to dissociate ${}"}`.replace(
                                "${}",
                                `${selectedUser?.firstname} ${selectedUser?.lastname}`
                            ),
                        deleteAction: dissociateUser.mutate,
                    }}
                    emptyProps={{
                        emptyMessage: i18n?.user_management_empty_label || "No users available",
                        emptyActionLabel: i18n?.user_management_empty_action || "Create User",
                    }}
                />
            </Box>
        </Box>
    </div>
}