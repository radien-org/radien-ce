import * as React from "react";
import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button from "@cloudscape-design/components/button";
import Header from "@cloudscape-design/components/header";
import {Box, Container, DatePicker, Input, Select} from "@cloudscape-design/components";
import axios, {AxiosError} from "axios";
import {useContext, useEffect, useState} from "react";
import {Loader} from "@/components/Loader/Loader";
import {RadienContext} from "@/context/RadienContextProvider";
import {useQuery} from "react-query";
import {OptionDefinition} from "@cloudscape-design/components/internal/components/option/interfaces";
import dynamic from "next/dynamic";
import {Role} from "radien";
import useCreateRole from "@/hooks/useCreateRole";
import {DateRangePickerDropdown} from "@cloudscape-design/components/date-range-picker/dropdown";

const FormField = dynamic(
    () => import("@cloudscape-design/components/form-field"),
    { ssr: false}
);

export default function createRole() {
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [selectTerminationDate, setTerminationDate] = useState(new Date());

    const [isNameValid, setIsNameValid] = useState(false);
    const [isDescriptionValid, setIsDescriptionValid] = useState(false);
    const [isTerminationDateValid, setIsTerminationDateValid] = useState(false);
    const [isFormSubmitted, setIsFormSubmitted] = useState(false);

    const [isFormValid, setIsFormValid] = useState(false);
    const [loading, setLoading] = useState(false);

    const createRoleMut = useCreateRole();
    const {addErrorMessage, i18n} = useContext(RadienContext);

    const loadDescription = async () => {
        return await axios.get("/api/description/getAll");
    }
    const loadTerminationDate = async () => {
        return await axios.get("/api/terminationdate/getAll");
    }

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
        if (_description.trim()) {
            setIsDescriptionValid(true);
        } else {
            setIsDescriptionValid(false);
        }
    }

    const validateTerminationDate = (_terminationDate: any) => {
        if (_terminationDate) {
            setIsTerminationDateValid(true);
        } else {
            setIsTerminationDateValid(false);
        }
    }

    const validateAll = () => {
        setIsFormValid(isNameValid && isDescriptionValid && isTerminationDateValid);
    }

    const handleSubmit = async () => {
        setIsFormSubmitted(true);

        if (!isFormValid) {
            return;
        }
        setLoading(true);
        const role: Role = {
            name,
            description: String(description),
            terminationDate: new Date
        }
        createRoleMut.mutate(role)

        setLoading(false);
    }

    const {data: descriptions, isLoading: descriptionLoading, error: descriptionLoadError} = useQuery("description", loadDescription);
    const {data: terminationDate, isLoading: terminationDateLoading, error: terminationDateLoadError} = useQuery("termination", loadTerminationDate);

    useEffect(() => {
        let error = i18n?.generic_message_error || "Error";
        if (descriptionLoadError) {
            addErrorMessage(`${error}: ${i18n?.error_loading_actions || "Could not load actions"}`);
        }
        if (terminationDateLoadError) {
            addErrorMessage(`${error}: ${i18n?.error_loading_resources || "Could not load resources"}`);
        }
    }, [descriptionLoadError, terminationDateLoadError]);

    return (
        <>
            <Box padding="xl">
                <Container>
                    {loading && <Loader/>}

                    <form onSubmit={e => {
                        e.preventDefault();
                        handleSubmit();
                    }}
                          className={"create-form--container"}>
                        <Form
                            actions={
                                <SpaceBetween direction="horizontal" size="xs">
                                    <Button formAction="none" variant="link" href={"/system/roleManagement"}>
                                        Cancel
                                    </Button>
                                    <Button variant="primary">{i18n?.button_create || "Create"}</Button>
                                </SpaceBetween>
                            }
                            header={<Header variant="h1">{i18n?.role_creation_header || "Create Role"}</Header>}
                        >
                            <SpaceBetween direction="vertical" size="l">
                                <FormField key="cu-form-3" label={i18n?.role_creation_name || "Name *"}
                                           errorText={!isNameValid && isFormSubmitted ? i18n?.role_creation_name_error || "Please enter a valid name" : null}>
                                    <Input
                                        value={name}
                                        onChange={(event) => {
                                            setName(event.detail.value)
                                            validateName(event.detail.value)
                                        }}
                                    />
                                </FormField>
                                <FormField key={"per-form--2"} label={i18n?.role_creation_description || "Description *"}
                                           errorText={!isDescriptionValid && isFormSubmitted ? i18n?.role_creation_action_error || "Please select an action" : null}>
                                    <Input
                                        value={description}
                                        onChange={(event) => {
                                            setDescription(event.detail.value)
                                            validateDescription(event.detail.value)
                                        }}
                                    />
                                </FormField>
                                <FormField key={"per-form--3"} label={i18n?.role_creation_termination_date || "Termination Date *"}
                                           errorText={!isTerminationDateValid && isFormSubmitted ? i18n?.role_creation_termination_date_error || "Please select a termination date" : null}
                                           constraintText="Use DD/MM/YYYY format.">

                                        <DatePicker
                                            onChange={( event) => {
                                                setTerminationDate(event.detail.value)
                                            }}
                                            value={String(terminationDate)}
                                            openCalendarAriaLabel={terminationDate =>
                                                "Choose certificate expiry date" +
                                                (terminationDate
                                                    ? `, selected date is ${terminationDate}`
                                                    : "")
                                            }
                                            nextMonthAriaLabel="Next month"
                                            placeholder="DD/MM/YYYY"
                                            previousMonthAriaLabel="Previous month"
                                            todayAriaLabel="Today"
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