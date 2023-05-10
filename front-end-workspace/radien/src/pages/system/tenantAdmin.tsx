import { User } from "radien";
import {Box, Container, DatePicker, FormField, Input, Modal, TableProps} from "@cloudscape-design/components";
import { Loader } from "@/components/Loader/Loader";
import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button from "@cloudscape-design/components/button";
import Header from "@cloudscape-design/components/header";
import * as React from "react";
import { useContext, useEffect } from "react";
import { RadienContext } from "@/context/RadienContextProvider";
import useAssignedTenants from "@/hooks/useAssignedTenants";
import useUpdateTenant from "@/hooks/useUpdateTenant";
import PaginatedTable from "@/components/PaginatedTable/PaginatedTable";
import { QueryKeys } from "@/consts";
import UserDetailsView from "@/components/UserDetailsView/UserDetailsView";
import {getUsersForTenantPage} from "@/hooks/usePaginatedUsersForTenant";
import {getColDefinitionUser} from "@/utils/tablesColDefinitions";
import useDeleteUser from "@/hooks/useDeleteUser";
import { useRouter } from "next/router";
import useDissociateTenant from "@/hooks/useDissociateTenant";
import AssociateUser from "@/components/UserAssociation/associateUser";

export default function TenantAdmin() {
    const CLIENT_TYPE = "CLIENT";
    const [loading, setLoading] = React.useState<boolean>(true);
    const {
        userInSession: radienUser,
        activeTenant: { data: tenantId, isLoading: isLoadingActiveTenant },
        i18n,
        addErrorMessage,
        addSuccessMessage,
    } = useContext(RadienContext);
    const { isLoading: loadingAssignedTenants, data: assignedTenants } = useAssignedTenants(radienUser?.id!);
    let tenant = assignedTenants?.results.filter((tenant) => tenant.id === tenantId?.tenantId)[0];
    const [name, setName] = React.useState<string>(tenant?.name || "");
    const [tenantKey, setTenantKey] = React.useState<string>(tenant?.tenantKey || "");
    const [tenantType, setTenantType] = React.useState<string>(tenant?.tenantType || "");
    const [tenantStart, setTenantStart] = React.useState<string>(`${tenant?.tenantStart}` || "");
    const [tenantEnd, setTenantEnd] = React.useState<string>(`${tenant?.tenantEnd}` || "");
    const [clientEmail, setClientEmail] = React.useState<string>(tenant?.clientEmail || "");
    const [clientAddress, setClientAddress] = React.useState<string>(tenant?.clientAddress || "");
    const [clientZipCode, setClientZipCode] = React.useState<string>(tenant?.clientZipCode || "");
    const [clientCity, setClientCity] = React.useState<string>(tenant?.clientCity || "");
    const [clientCountry, setClientCountry] = React.useState<string>(tenant?.clientCountry || "");
    const [clientPhoneNumber, setClientPhoneNumber] = React.useState<number>(tenant?.clientPhoneNumber || 0);
    const updateTenant = useUpdateTenant(tenantId?.tenantId!);
    const [isFormSubmitted, setIsFormSubmitted] = React.useState<boolean>(false);
    const [newTenantData, setNewTenantData] = React.useState<any>(null);
    const [updateModalVisible, setUpdateModalVisible] = React.useState<boolean>(false);

    const validateTextInputNonEmpty = (value: string) => {
        return value.trim().length > 0;
    };
    const validateTextInputEmail = (value: string) => {
        const re = /\S+@\S+\.\S+/;
        return re.test(value);
    };

    const validateDateFuture = (value: string) => {
        const date = new Date(value);
        return date >= new Date();
    };
    const colDefinition: TableProps.ColumnDefinition<User>[] = getColDefinitionUser(i18n);
    const deleteUser = useDeleteUser();
    const router = useRouter();
    const dissociateUser = useDissociateTenant();

    const resetForm = () => {
        tenant = newTenantData ? newTenantData : assignedTenants?.results.filter((tenant) => tenant.id === tenantId?.tenantId)[0];
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
    };
    useEffect(() => {
        resetForm();
    }, [assignedTenants, radienUser]);

    const handleSubmit = async () => {
        setIsFormSubmitted(true);
        const isFormValid =
            validateTextInputNonEmpty(name) &&
            validateTextInputNonEmpty(tenantKey) &&
            validateTextInputNonEmpty(tenantStart) &&
            validateTextInputNonEmpty(tenantEnd) &&
            validateDateFuture(tenantStart) &&
            validateDateFuture(tenantEnd);

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
        };
        updateTenant
            .mutateAsync(newTenantInfo).then((res) => {
                addSuccessMessage(i18n?.tenant_admin_update_success || "Tenant updated successfully");
                setNewTenantData(newTenantInfo);
            }).catch((e) => {
                addErrorMessage(i18n?.tenant_admin_update_error || "Error updating tenant");
                resetForm();
            })
            .finally(() => {
                setLoading(false);
            });
    };

    return (
        <div>
            <Box padding="xl">
                <Modal
                    onDismiss={() => setUpdateModalVisible(false)}
                    visible={updateModalVisible}
                    closeAriaLabel="Close modal"
                    footer={
                        <Box float="right">
                            <SpaceBetween direction="horizontal" size="xs">
                                <Button variant="link" onClick={() => {
                                    setUpdateModalVisible(false);
                                    resetForm();
                                }}>
                                    {i18n?.button_cancel || "Cancel"}
                                </Button>
                                <Button variant="primary" onClick={() => handleSubmit()}>
                                    {i18n?.button_update || "Update"}
                                </Button>
                            </SpaceBetween>
                        </Box>
                    }
                    header={'Update tenant'}>
                    {i18n?.tenant_admin_update_user_confirm || "Are you sure you want to update tenant information?"}
                </Modal>
                <Container>
                    {(isLoadingActiveTenant || loadingAssignedTenants) && <Loader />}

                    {tenant && (
                        <form
                            onSubmit={(e) => {
                                e.preventDefault();
                                setUpdateModalVisible(true);
                            }}
                            className={"create-form--container"}>
                            <Form
                                actions={
                                    <SpaceBetween direction="horizontal" size="xs">
                                        <Button disabled={radienUser?.processingLocked} formAction="none" variant="normal" onClick={resetForm}>
                                            {i18n?.tenant_admin_cancel_action || "Cancel"}
                                        </Button>
                                        <Button disabled={radienUser?.processingLocked} variant="primary">
                                            {i18n?.tenant_admin_update_action || "Update"}
                                        </Button>
                                    </SpaceBetween>
                                }
                                header={<Header variant="h1">{i18n?.tenant_admin_form_title || "Tenant information"}</Header>}>
                                <SpaceBetween direction="vertical" size="l">
                                    <FormField
                                        key="tenant-form-1"
                                        label={i18n?.tenant_management_column_name || "Name"}
                                        errorText={
                                            !validateTextInputNonEmpty(name) && isFormSubmitted
                                                ? i18n?.tenant_admin_column_invalid_name || "Please enter a valid tenant name"
                                                : null
                                        }>
                                        <Input
                                            value={name}
                                            disabled={radienUser?.processingLocked}
                                            onChange={(event) => {
                                                setName(event.detail.value);
                                            }}
                                        />
                                    </FormField>
                                    <FormField key="tenant-form-2" label={i18n?.tenant_management_column_key || "Tenant key"}>
                                        <Input value={tenantKey} disabled={true} />
                                    </FormField>
                                    <FormField key="tenant-form-3" label={i18n?.tenant_management_column_type || "Tenant type"}>
                                        <Input value={tenantType} disabled={true} />
                                    </FormField>
                                    <FormField
                                        key={"tenant-form-4"}
                                        label={i18n?.tenant_management_column_start_date || "Tenant start date"}
                                        constraintText="Use YYYY/MM/DD format."
                                        errorText={
                                            !validateDateFuture(tenantStart) && isFormSubmitted
                                                ? i18n?.tenant_admin_column_invalid_start_date || "Please enter a valid tenant start date"
                                                : null
                                        }>
                                        <DatePicker
                                            disabled={radienUser?.processingLocked}
                                            onChange={({ detail }) => setTenantStart(detail.value)}
                                            value={tenantStart}
                                            isDateEnabled={(date) => date >= new Date()}
                                            nextMonthAriaLabel={i18n?.datepicker_next_month || "Next month"}
                                            placeholder="YYYY/MM/DD"
                                            previousMonthAriaLabel={i18n?.datepicker_previous_month || "Previous month"}
                                            todayAriaLabel={i18n?.datepicker_today || "Today"}
                                        />
                                    </FormField>
                                    <FormField
                                        key={"tenant-form-5"}
                                        label={i18n?.tenant_management_column_end_date || "Tenant end date"}
                                        constraintText="Use YYYY/MM/DD format."
                                        errorText={
                                            !validateDateFuture(tenantEnd) && isFormSubmitted
                                                ? i18n?.tenant_admin_column_invalid_end_date || "Please enter a valid tenant end date"
                                                : null
                                        }>
                                        <DatePicker
                                            disabled={radienUser?.processingLocked}
                                            onChange={({ detail }) => setTenantEnd(detail.value)}
                                            value={tenantEnd}
                                            isDateEnabled={(date) => date > new Date()}
                                            nextMonthAriaLabel={i18n?.datepicker_next_month || "Next month"}
                                            placeholder="YYYY/MM/DD"
                                            previousMonthAriaLabel={i18n?.datepicker_previous_month || "Previous month"}
                                            todayAriaLabel={i18n?.datepicker_today || "Today"}
                                        />
                                    </FormField>
                                    {tenant?.tenantType === CLIENT_TYPE && (
                                        <>
                                            <FormField
                                                key="tenant-form-15"
                                                label={i18n?.tenant_management_column_email || "Client email"}
                                                errorText={
                                                    !validateTextInputEmail(clientEmail) && isFormSubmitted
                                                        ? i18n?.tenant_admin_column_invalid_email || "Please enter a valid client email"
                                                        : null
                                                }>
                                                <Input
                                                    value={clientEmail}
                                                    onChange={(event) => {
                                                        setClientEmail(event.detail.value);
                                                    }}
                                                />
                                            </FormField>
                                            <FormField key="tenant-form-6" label={i18n?.tenant_management_column_address || "client address"}>
                                                <Input
                                                    value={clientAddress}
                                                    onChange={(event) => {
                                                        setClientAddress(event.detail.value);
                                                    }}
                                                />
                                            </FormField>
                                            <FormField key="tenant-form-7" label={i18n?.tenant_management_column_zip_code || "client zip code"}>
                                                <Input
                                                    value={clientZipCode}
                                                    onChange={(event) => {
                                                        setClientZipCode(event.detail.value);
                                                    }}
                                                />
                                            </FormField>
                                            <FormField key="tenant-form-8" label={i18n?.tenant_management_column_city || "client city"}>
                                                <Input
                                                    value={clientCity}
                                                    onChange={(event) => {
                                                        setClientCity(event.detail.value);
                                                    }}
                                                />
                                            </FormField>
                                            <FormField key="tenant-form-10" label={i18n?.tenant_management_column_country || "Client country"}>
                                                <Input
                                                    value={clientCountry}
                                                    onChange={(event) => {
                                                        setClientCountry(event.detail.value);
                                                    }}
                                                />
                                            </FormField>
                                        </>
                                    )}
                                </SpaceBetween>
                            </Form>
                        </form>
                    )}
                </Container>
            </Box>

        <Box>
            <AssociateUser/>
        </Box>

        </div>
    );
}
