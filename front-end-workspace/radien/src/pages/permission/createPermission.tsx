import * as React from "react";
import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button from "@cloudscape-design/components/button";
import Header from "@cloudscape-design/components/header";
import {Box, Container, FormField, Input, Select} from "@cloudscape-design/components";
import axios from "axios";
import {useContext, useEffect, useState} from "react";
import {Loader} from "@/components/Loader/Loader";
import {RadienContext} from "@/context/RadienContextProvider";
import {useQuery} from "react-query";
import {OptionDefinition} from "@cloudscape-design/components/internal/components/option/interfaces";


export default function createPermission() {
    const [name, setName] = useState("");
    const [selectedAction, setSelectedAction] = useState<any>(null);
    const [selectedResource, setSelectedResource] = useState<any>(null);

    const [isNameValid, setIsNameValid] = useState(false);
    const [isNameTouched, setIsNameTouched] = useState(false);
    const [isActionValid, setIsActionValid] = useState(false);
    const [isActionTouched, setIsActionTouched] = useState(false);
    const [isResourceValid, setIsResourceValid] = useState(false);
    const [isResourceTouched, setIsResourceTouched] = useState(false);

    const [isFormValid, setIsFormValid] = useState(false);
    const [formError, setFormError] = useState("");
    const [loading, setLoading] = useState(false);

    const {addSuccessMessage, addErrorMessage} = useContext(RadienContext);

    const loadActions = async () => {
        return await axios.get("/api/action/getAll");
    }
    const loadResources = async () => {
        return await axios.get("/api/resource/getAll");
    }

    useEffect(() => {
        validateAll();
    }, [isNameValid, isResourceValid, isActionValid]);

    const validateName = (_name: string) => {
        if (_name.trim().length > 0) {
            setIsNameValid(true);
        } else {
            setIsNameValid(false);
        }
    }

    const validateAction = (_action: any) => {
        if (_action) {
            setIsActionValid(true);
        } else {
            setIsActionValid(false);
        }
    }

    const validateResource = (_resource: any) => {
        if (_resource) {
            setIsResourceValid(true);
        } else {
            setIsResourceValid(false);
        }
    }

    const validateAll = () => {
        setIsFormValid(isNameValid && isActionValid && isResourceValid);
    }

    const handleSubmit = async () => {
        setIsNameTouched(true);
        setIsActionTouched(true);
        setIsResourceTouched(true);

        if (!isFormValid) {
            setFormError("Please fill out all required fields");
            return;
        }
        setFormError("");
        setLoading(true);
        try {
            const permission = {
                name,
                actionId: selectedAction.value,
                resourceId: selectedResource.value
            }
            await axios.post("/api/permission/permission/createPermission", permission);
            addSuccessMessage("Permission created successfully")
        } catch (e) {
            console.log(e);
            // @ts-ignore
            if (e.response.status === 401) {
                addErrorMessage("Error: You are not logged in, please login again");
            } else {
                // @ts-ignore
                addErrorMessage(`Error: ${e.message}`);
            }
        }
        setLoading(false);
    }

    const {data: actions, isLoading: actionsLoading, error: actionLoadError} = useQuery("actions", loadActions);
    const {data: resources, isLoading: resourcesLoading, error: resourceLoadError} = useQuery("resources", loadResources);

    useEffect(() => {
        if (actionLoadError) {
            addErrorMessage("Error: Could not load actions");
        }
        if (resourceLoadError) {
            addErrorMessage("Error: Could not load resources");
        }
    }, [actionLoadError, resourceLoadError]);

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
                                    <Button formAction="none" variant="link" href={"/system/permissionManagement"}>
                                        Cancel
                                    </Button>
                                    <Button variant="primary">Create</Button>
                                </SpaceBetween>
                            }
                            errorText={formError}
                            header={<Header variant="h1">Create permission</Header>}
                        >
                            <SpaceBetween direction="vertical" size="l">
                                <FormField key="cu-form-3" label={"Name *"}
                                           errorText={!isNameValid && isNameTouched ? "Please enter a valid name" : null}>
                                    <Input
                                        value={name}
                                        onChange={(event) => {
                                            setName(event.detail.value)
                                            validateName(event.detail.value)
                                        }}
                                    />
                                </FormField>
                                <FormField key={"per-form--2"} label={"Action *"} errorText={!isActionValid && isActionTouched ? "Please select an action" : null}>
                                    <Select
                                        selectedOption={selectedAction}
                                        onChange={({ detail }) => {
                                            setSelectedAction(detail.selectedOption)
                                            validateAction(detail.selectedOption)
                                        }}
                                        options={actions?.data ? [...actions?.data.map((action: any) => {return {label: action.name, value: action.id}})] : []}
                                        loadingText="Loading actions..."
                                        placeholder="Choose an action"
                                        selectedAriaLabel="Selected action"
                                        statusType={actionsLoading ? "loading" : (actionLoadError ? "error" : "finished")}
                                    />
                                </FormField>
                                <FormField key={"per-form--3"} label={"Resource *"} errorText={!isResourceValid && isResourceTouched ? "Please select a resource" : null}>
                                    <Select
                                        selectedOption={selectedResource}
                                        onChange={({ detail }) => {
                                            setSelectedResource(detail.selectedOption);
                                            validateResource(detail.selectedOption)
                                        }}
                                        options={resources?.data ? [...resources?.data.map((resource: any) => {return {label: resource.name, value: resource.id}})] : []}
                                        loadingText="Loading resources..."
                                        placeholder="Choose a resource"
                                        selectedAriaLabel="Selected resource"
                                        statusType={resourcesLoading ? "loading" : (resourceLoadError ? "error" : "finished")}
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