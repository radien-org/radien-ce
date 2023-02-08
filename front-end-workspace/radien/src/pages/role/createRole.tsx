import * as React from "react";
import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button from "@cloudscape-design/components/button";
import Header from "@cloudscape-design/components/header";
import {Box, Container, DatePicker, Input, Select} from "@cloudscape-design/components";
import {useContext, useEffect, useState} from "react";
import {Loader} from "@/components/Loader/Loader";
import {RadienContext} from "@/context/RadienContextProvider";
import dynamic from "next/dynamic"; import {Role} from "radien";
import useCreateRole from "@/hooks/useCreateRole";


const FormField = dynamic(() => import("@cloudscape-design/components/form-field"), { ssr: false} );

export default function createRole() {

    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [terminationDate, setTerminationDate] = useState("");
    const [isNameValid, setIsNameValid] = useState(false);
    const [isDescriptionValid, setIsDescriptionValid] = useState(false);
    const [isTerminationDateValid, setIsTerminationDateValid] = useState(false);
    const [isFormSubmitted, setIsFormSubmitted] = useState(false);
    const [isFormValid, setIsFormValid] = useState(false);
    const createRoleMutation = useCreateRole();
    const { i18n} = useContext(RadienContext);

    useEffect(() => {
        validateAll();
    }, [isNameValid, isDescriptionValid, isTerminationDateValid]);

    const validateName = (_name: string) => {
        if (_name.trim().length > 0) {
            setIsNameValid(true);
        } else {
            setIsNameValid(false);
        }
    }

    const validateDescription = (_description: any) => {
        if (_description.trim().length > 0) {
            setIsDescriptionValid(true);
        } else {
            setIsDescriptionValid(false);
        }
    }

    const validateTerminationDate = (_terminationDate: any) => {
        if (_terminationDate && new Date(_terminationDate) > new Date()) {
            setIsTerminationDateValid(true);
        } else {
            setIsTerminationDateValid(false);
        }
    }

    const validateAll = () => {
        setIsFormValid(isNameValid && isDescriptionValid && isTerminationDateValid);
    }

    const handleSubmit = async () => {
        setIsFormSubmitted(true); if (!isFormValid) {
            return;
        } const role: Role = {
            name,
            description: String(description),
            terminationDate: new Date
        };
        createRoleMutation.mutate(role)
    }

    return (
        <>
            <Box padding="xl">
                <Container>
                    {createRoleMutation.isLoading && <Loader/>} <form onSubmit={e => {
                    e.preventDefault(); handleSubmit();
                }}
                                                                      className={"create-form--container"}>
                    <Form
                        actions={
                            <SpaceBetween direction="horizontal" size="xs">
                                <Button formAction="none" variant="link" href={"/system/roleManagement"}>
                                    Cancel
                                </Button> <Button variant="primary">{i18n?.button_create || "Create"}</Button>
                            </SpaceBetween>
                        } header={<Header variant="h1">{i18n?.role_creation_header || "Create Role"}</Header>}
                    >
                        <SpaceBetween direction="vertical" size="l">
                            <FormField key="cu-form-3" label={i18n?.role_creation_name || "Name *"}
                                       errorText={!isNameValid && isFormSubmitted ? i18n?.role_creation_name_error || "Please enter a valid name" : null}>
                                <Input
                                    value={name} onChange={(event) => {
                                    setName(event.detail.value);
                                    validateName(event.detail.value);
                                }}
                                />

                            </FormField> <FormField key={"per-form--2"} label={i18n?.role_creation_description || "Description *"}
                                                    errorText={!isDescriptionValid && isFormSubmitted ? i18n?.role_creation_action_error || "Please select an action" : null}>
                            <Input
                                value={description} onChange={(event) => {
                                setDescription(event.detail.value);
                                validateDescription(event.detail.value);
                            }}
                            />

                        </FormField> <FormField key={"per-form--3"} label={i18n?.role_creation_termination_date || "Termination Date *"}
                                                errorText={!isTerminationDateValid && isFormSubmitted ? i18n?.role_creation_termination_date_error || "Please select a termination date after today." : null}
                                                constraintText="Use YYYY/MM/DD format.">
                            <DatePicker
                                onChange={( event) => {
                                    setTerminationDate(event.detail.value);
                                    validateTerminationDate(event.detail.value);

                                }} isDateEnabled={date => date > new Date()} value={String(terminationDate)} openCalendarAriaLabel={terminationDate =>
                                "Choose certificate expiry date" + (terminationDate
                                    ? `, selected date is ${terminationDate}` : "")
                            } locale={i18n?.locale || "de-DE"} nextMonthAriaLabel="Next month" placeholder="YYYY/MM/DD" previousMonthAriaLabel="Previous month"
                                todayAriaLabel="Today"
                            />
                        </FormField>

                        </SpaceBetween>
                    </Form>
                </form>
                </Container>
            </Box>
        </>
    ); }
