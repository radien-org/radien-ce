import * as React from "react";
import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button from "@cloudscape-design/components/button";
import Header from "@cloudscape-design/components/header";
import { Box, Container, DatePicker, Input, Select } from "@cloudscape-design/components";
import { useContext, useEffect, useState } from "react";
import { Loader } from "@/components/Loader/Loader";
import { RadienContext } from "@/context/RadienContextProvider";
import dynamic from "next/dynamic";
import { Tenant } from "radien";
import useCreateTenant from "@/hooks/useCreateTenant";
import { useRouter } from "next/router";
import {types} from "sass";
import Number = types.Number;
import {OptionDefinition} from "@cloudscape-design/components/internal/components/option/interfaces";
import axios, { AxiosError } from "axios";
import emailValidator from 'email-validator';
import {useQuery} from "react-query";
import useAvailableTenants from "@/hooks/useAvailableTenants";


const FormField = dynamic(() => import("@cloudscape-design/components/form-field"), { ssr: false });

export default function CreateTenant() {
    const [name, setName] = useState("");
    const [clientAddress, setClientAddress] = useState("");
    const [tenantEnd, setTenantEnd] = useState("");
    const [clientCity, setClientCity] = useState("")
    const [clientCountry, setClientCountry] = useState("")
    const [clientEmail, setClientEmail] = useState("")
    const [clientPhoneNumber,setClientPhoneNumber] = useState("")
    const [clientZipCode,setClientZipCode] = useState("")

    const [tenantKey,setTenantKey] = useState("")
    const [tenantStart,setTenantStart] = useState("")
    const [tenantType,setTenantType] = useState("")
    const [selectedTenant, setSelectedTenant] = useState<OptionDefinition | null>(null);
    const [selectedTenantType, setSelectedTenantType] = useState<OptionDefinition | null>(null);
    const { activeTenant } = useContext(RadienContext);


    const [isNameValid, setIsNameValid] = useState(false);
    const [isClientAddressValid, setIsClientAddressValid] = useState(true);
    const [isTenantEndValid, setIsTenantEndValid] = useState(false);
    const [isClientCityValid, setIsClientCityValid] = useState(true)
    const [isClientCountryValid, setIsClientCountryValid] = useState(true)
    const [isClientEmailValid, setIsClientEmailValid] = useState(true)
    const [isClientIdValid,setIsClientIdValid] = useState(true)
    const [isClientPhoneNumberValid,setIsClientPhoneNumberValid] = useState(true)
    const [isClientZipCodeValid,setIsClientZipCodeValid] = useState(true)
    const [isParentIdValid,setIsParentIdValid] = useState(true)
    const [isTenantKeyValid,setIsTenantKeyValid] = useState(false)
    const [isTenantStartValid,setIsTenantStartValid] = useState(false)
    const [isTenantTypeValid,setIsTenantTypeValid] = useState(false)

    const [isFormSubmitted, setIsFormSubmitted] = useState(false);
    const [isFormValid, setIsFormValid] = useState(false);

    const createTenantMutation = useCreateTenant();

    const { i18n } = useContext(RadienContext);
    const { locale } = useRouter();

    const {  data: tenants, isLoading: tenantsLoading, error: tenantLoadError } = useAvailableTenants();

    useEffect(() => {
        validateAll();
    }, [isNameValid, isClientAddressValid, isTenantEndValid, isClientCityValid,
        isClientCountryValid, isClientEmailValid, isClientIdValid, isClientPhoneNumberValid,
        isClientZipCodeValid, isParentIdValid, isTenantTypeValid, isTenantKeyValid, isTenantStartValid]);


    const validateName = (_name: string) => {
        if (_name.trim().length > 0) {
            setIsNameValid(true);
        } else {
            setIsNameValid(false);
        }
    };

    const validateClientEmail = (_clientEmail: string) => {
        if (_clientEmail.trim().length > 0 && emailValidator.validate(String(_clientEmail)) ) {
            setIsClientEmailValid(true);
        } else {
            setIsClientEmailValid(false);
        }
    };
    const validateClientPhoneNumber = (_clientPhoneNumber: string) => {
        if (_clientPhoneNumber.trim().length > 0) {
            setIsClientPhoneNumberValid(true);
        } else {
            setIsClientPhoneNumberValid(false);
        }
    };
    const validateClientZipCode = (_clientZipCode: string) => {
        if (_clientZipCode.trim().length > 0) {
            setIsClientZipCodeValid(true);
        } else {
            setIsClientZipCodeValid(false);
        }
    };
    const validateParentId = (_parentId: string) => {
        if (_parentId.trim().length > 0) {
            setIsParentIdValid(true);
        } else {
            setIsParentIdValid(false);
        }
    };
    const validateTenantType = (_tenantType: string) => {
        if (_tenantType.trim().length > 0) {
            setIsTenantTypeValid(true);
        } else {
            setIsTenantTypeValid(false);
        }

    };
    const validateTenantKey = (_tenantKey: string) => {
        if (_tenantKey.trim().length > 0) {
            setIsTenantKeyValid(true);
        } else {
            setIsTenantKeyValid(false);
        }
    };

    const validateTenantStart = (_tenantStart: any) => {
        if (_tenantStart && new Date(_tenantStart) > new Date()){
            setIsTenantStartValid(true);
        } else {
            setIsTenantStartValid(false);
        }
    };

    const validateTenantEnd = (_tenantEnd: any) => {
        if (_tenantEnd && new Date(_tenantEnd) > new Date(tenantStart))  {
            setIsTenantEndValid(true);
        } else {
            setIsTenantEndValid(false);
        }
    };



    const validateAll = () => {
        setIsFormValid(isNameValid && isClientAddressValid && isTenantEndValid && isClientCityValid && isClientCountryValid && isClientEmailValid && isClientIdValid && isClientPhoneNumberValid && isClientZipCodeValid && isParentIdValid && isTenantTypeValid && isTenantKeyValid && isTenantStartValid);
    };

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsFormSubmitted(true);
        if (!isFormValid) {
            return;
        }
        const tenant: any = {
            clientAddress,
            clientCity,
            clientCountry,
            clientEmail,
            clientZipCode,
            tenantEnd,
            tenantKey,
            tenantStart,
            tenantType,
            name,
            clientId: tenantType === "SUB" ? selectedTenant?.value : null,
            parentId: tenantType !== "ROOT" ? activeTenant.data?.tenantId : null
        };
        createTenantMutation.mutate(tenant);
    };


    return (
        <>
            <Box padding="xl">
                <Container>
                    {createTenantMutation.isLoading && <Loader />}
                    <form className={"create-form--container"} onSubmit={(e) => handleSubmit(e)}>
                        <Form
                            actions={
                                <SpaceBetween direction="horizontal" size="xs">
                                    <Button formAction="none" variant="link" href={"/system/tenantManagement"}>
                                        {i18n?.button_cancel || "Cancel"}
                                    </Button>{" "}
                                    <Button variant="primary">{i18n?.button_create || "Create"}</Button>
                                </SpaceBetween>
                            }
                            header={<Header variant="h1">{i18n?.tenant_creation_header || "Create Tenant"}</Header>}>
                            <SpaceBetween direction="vertical" size="l">
                                <FormField
                                    key="cu-form-3"
                                    label={i18n?.create_tenant_name || "Name*"}
                                    errorText={!isNameValid && isFormSubmitted ? i18n?.create_tenant_name_error || "Please enter a valid name" : null}>
                                    <Input
                                        value={name}
                                        onChange={(event) => {
                                            setName(event.detail.value);
                                            validateName(event.detail.value);
                                        }}
                                    />
                                </FormField>


                                <FormField
                                    key={"per-form--2"}
                                    label={i18n?.create_tenant_email || "Email"}
                                    errorText={
                                        !isClientEmailValid && isFormSubmitted
                                            ? i18n?.create_tenant_email_error || "Please enter a valid email"
                                            : null
                                    }>
                                    <Input
                                        value={clientEmail}
                                        onChange={(event) => {
                                            setClientEmail(event.detail.value);
                                            validateClientEmail(event.detail.value);
                                        }}
                                    />
                                </FormField>

                                <FormField
                                    key={"per-form--2"}
                                    label={i18n?.create_tenant_phone_number || "Phone number"}
                                    errorText={
                                        !isClientPhoneNumberValid && isFormSubmitted
                                            ? i18n?.create_tenant_phone_number_error || "Please enter a valid Phone number"
                                            : null
                                    }>
                                    <Input
                                        value={clientPhoneNumber}
                                        onChange={(event) => {
                                            setClientPhoneNumber(event.detail.value);
                                            validateClientPhoneNumber(event.detail.value);
                                        }}
                                    />
                                </FormField>

                                <FormField
                                    key={"per-form--2"}
                                    label={i18n?.create_tenant_tenant_key|| "Tenant Key*"}
                                    errorText={
                                        !isTenantKeyValid && isFormSubmitted
                                            ? i18n?.create_tenant_key_error || "Please enter a valid Tenant key"
                                            : null
                                    }>
                                    <Input
                                        value={tenantKey}
                                        onChange={(event) => {
                                            setTenantKey(event.detail.value);
                                            validateTenantKey(event.detail.value);
                                        }}
                                    />
                                </FormField>

                                <FormField
                                    key={"per-form--2"}
                                    label={i18n?.create_tenant_tenant_type|| "Tenant Type*"}
                                    errorText={
                                        !isTenantTypeValid && isFormSubmitted
                                            ? i18n?.create_tenant_type_error || "Please enter a valid Tenant type"
                                            : null
                                    }>
                                    <Select
                                        selectedOption={selectedTenantType}
                                        onChange={({detail}) => {
                                            setSelectedTenantType(detail.selectedOption);
                                            setTenantType(String(detail.selectedOption.value));
                                            validateTenantType(String(detail.selectedOption));
                                        }}
                                        options = {[
                                            {label:"ROOT",value:"ROOT"},
                                            {label:"CLIENT",value:"CLIENT"},
                                            {label:"SUB",value:"SUB"}
                                        ]}
                                        loadingText={i18n?.tenant_creation_tenant_type_loading || "Loading tenant types..."}
                                        placeholder={i18n?.tenant_creation_tenant_type_placeholder || "Choose an tenant types"}
                                        selectedAriaLabel={i18n?.tenant_creation_tenant_type_selected_aria || "Selected tenant type"}
                                        statusType={tenantsLoading ? "loading" : tenantLoadError ? "error" : "finished"}
                                    />


                                </FormField>
                                {tenantType === 'SUB' && <FormField
                                    key={"per-form--2"}
                                    label={i18n?.tenant_creation_client_id || "Client ID *"}
                                    errorText={!isClientIdValid && isFormSubmitted ? i18n?.tenant_creation_client_id || "Please select an action" : null}>
                                    <Select
                                        selectedOption={selectedTenant}
                                        onChange={({detail}) => {
                                            setSelectedTenant(detail.selectedOption);
                                            validateParentId(String(detail.selectedOption));
                                        }}
                                        options={
                                           tenants
                                                ? [
                                                   ...tenants?.map((tenant: any) => {
                                                        return {label: tenant.name, value: tenant.id};
                                                    }),
                                                ]
                                                : []
                                        }
                                        loadingText={i18n?.tenant_creation_client_loading || "Loading actions..."}
                                        placeholder={i18n?.tenant_creation_client_placeholder || "Choose an client"}
                                        selectedAriaLabel={i18n?.tenant_creation_client_selected_aria || "Selected client"}
                                        statusType={tenantsLoading ? "loading" : tenantLoadError ? "error" : "finished"}
                                    />
                                </FormField>}

                                {tenantType === 'CLIENT' && <FormField

                                    key={"per-form--2"}
                                    label={i18n?.create_tenant_address || "Address"}
                                    errorText={
                                        !isClientAddressValid && isFormSubmitted
                                            ? i18n?.create_tenant_address_error || "Please enter a valid address"
                                            : null
                                    }>
                                    <Input
                                        value={clientAddress}
                                        onChange={(event) => {
                                            setClientAddress(event.detail.value);
                                        }}
                                    />
                                </FormField>}

                                {tenantType === 'CLIENT' && <FormField
                                    key={"per-form--2"}
                                    label={i18n?.create_tenant_city || "City"}
                                    errorText={
                                        !isClientCityValid && isFormSubmitted
                                            ? i18n?.create_tenant_city_error || "Please enter a valid city"
                                            : null
                                    }>
                                    <Input
                                        value={clientCity}
                                        onChange={(event) => {
                                            setClientCity(event.detail.value);
                                        }}
                                    />
                                </FormField>}

                                {tenantType === 'CLIENT' && <FormField
                                    key={"per-form--2"}
                                    label={i18n?.create_tenant_zipcode || "Zip Code"}
                                    errorText={
                                        !isClientZipCodeValid && isFormSubmitted
                                            ? i18n?.create_tenant_zipcode_error || "Please enter a valid zipcode"
                                            : null
                                    }>
                                    <Input
                                        value={clientZipCode}
                                        onChange={(event) => {
                                            setClientZipCode(event.detail.value);
                                            validateClientZipCode(event.detail.value);
                                        }}
                                    />
                                </FormField>}

                                {tenantType === 'CLIENT' && <FormField
                                    key={"per-form--2"}
                                    label={i18n?.create_tenant_country || "Country"}
                                    errorText={
                                        !isClientCountryValid && isFormSubmitted
                                            ? i18n?.create_tenant_country_error || "Please enter a valid country"
                                            : null
                                    }>
                                    <Input
                                        value={clientCountry}
                                        onChange={(event) => {
                                            setClientCountry(event.detail.value);
                                        }}
                                    />
                                </FormField>}

                                <FormField
                                    key={"per-form--2"}
                                    label={i18n?.create_tenant_start_date || "Tenant Start Date*"}
                                    errorText={
                                        !isTenantStartValid && isFormSubmitted
                                            ? i18n?.create_tenant_start_date_error || "Please select a Tenant start date after today"
                                            : null
                                    }
                                    constraintText="Use YYYY/MM/DD format.">
                                    <DatePicker
                                        onChange={(event) => {
                                            setTenantStart(event.detail.value);
                                            validateTenantStart(event.detail.value);
                                        }}
                                        isDateEnabled={(date) => date > new Date()}
                                        value={String(tenantStart)}
                                        locale={locale}
                                        nextMonthAriaLabel={i18n?.create_tenant_next_month_aria_label || "Next month"}
                                        previousMonthAriaLabel={i18n?.create_tenant_previous_month_aria_label || "Previous month"}
                                        todayAriaLabel={i18n?.create_tenant_today_aria_label || "Today"}
                                        placeholder="YYYY/MM/DD"
                                    />
                                </FormField>



                                <FormField
                                    key={"per-form--2"}
                                    label={i18n?.create_tenant_end_date || "Tenant End Date*"}
                                    errorText={
                                        !isTenantEndValid && isFormSubmitted
                                            ? i18n?.create_tenant_end_date_error || "Please select a Tenant end date after start date"
                                            : null
                                    }
                                    constraintText="Use YYYY/MM/DD format.">
                                    <DatePicker
                                        onChange={(event) => {
                                            setTenantEnd(event.detail.value);
                                            validateTenantEnd(event.detail.value);
                                        }}
                                        isDateEnabled={(date) => date > new Date()}
                                        value={String(tenantEnd)}
                                        locale={locale}
                                        nextMonthAriaLabel={i18n?.create_tenant_next_month_aria_label || "Next month"}
                                        previousMonthAriaLabel={i18n?.create_tenant_previous_month_aria_label || "Previous month"}
                                        todayAriaLabel={i18n?.create_tenant_today_aria_label || "Today"}
                                        placeholder="YYYY/MM/DD"
                                    />
                                </FormField>
                            </SpaceBetween>
                        </Form>
                    </form>
                </Container>
            </Box>
        </>
    );
}
