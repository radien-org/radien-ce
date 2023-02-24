import * as React from "react";
import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button from "@cloudscape-design/components/button";
import Header from "@cloudscape-design/components/header";
import { Box, Container, Input, Select } from "@cloudscape-design/components";
import axios, { AxiosError } from "axios";
import { useContext, useEffect, useState } from "react";
import { Loader } from "@/components/Loader/Loader";
import { RadienContext } from "@/context/RadienContextProvider";
import { useQuery } from "react-query";
import { OptionDefinition } from "@cloudscape-design/components/internal/components/option/interfaces";
import dynamic from "next/dynamic";
import { Permission } from "radien";
import useCreatePermission from "@/hooks/useCreatePermission";

const FormField = dynamic(() => import("@cloudscape-design/components/form-field"), { ssr: false });

export default function CreatePermission() {
    const [name, setName] = useState("");
    const [selectedAction, setSelectedAction] = useState<OptionDefinition | null>(null);
    const [selectedResource, setSelectedResource] = useState<OptionDefinition | null>(null);

    const [isNameValid, setIsNameValid] = useState(false);
    const [isActionValid, setIsActionValid] = useState(false);
    const [isResourceValid, setIsResourceValid] = useState(false);
    const [isFormSubmitted, setIsFormSubmitted] = useState(false);

    const [isFormValid, setIsFormValid] = useState(false);
    const [loading, setLoading] = useState(false);

    const createPermissionMut = useCreatePermission();
    const { addErrorMessage, i18n } = useContext(RadienContext);

    const loadActions = async () => {
        return await axios.get("/api/action/find");
    };
    const loadResources = async () => {
        return await axios.get("/api/resource/find");
    };

    useEffect(() => {
        validateAll();
    }, [isNameValid, isResourceValid, isActionValid]);

    const validateName = (_name: string) => {
        if (_name.trim().length > 0) {
            setIsNameValid(true);
        } else {
            setIsNameValid(false);
        }
    };

    const validateAction = (_action: any) => {
        if (_action) {
            setIsActionValid(true);
        } else {
            setIsActionValid(false);
        }
    };

    const validateResource = (_resource: any) => {
        if (_resource) {
            setIsResourceValid(true);
        } else {
            setIsResourceValid(false);
        }
    };

    const validateAll = () => {
        setIsFormValid(isNameValid && isActionValid && isResourceValid);
    };

    const handleSubmit = async () => {
        setIsFormSubmitted(true);

        if (!isFormValid) {
            return;
        }
        setLoading(true);
        const permission: Permission = {
            name,
            actionId: Number(selectedAction?.value),
            resourceId: Number(selectedResource?.value),
        };
        createPermissionMut.mutate(permission);

        setLoading(false);
    };

    const { data: actions, isLoading: actionsLoading, error: actionLoadError } = useQuery("actions", loadActions);
    const { data: resources, isLoading: resourcesLoading, error: resourceLoadError } = useQuery("resources", loadResources);

    useEffect(() => {
        let error = i18n?.generic_message_error || "Error";
        if (actionLoadError) {
            addErrorMessage(`${error}: ${i18n?.error_loading_actions || "Could not load actions"}`);
        }
        if (resourceLoadError) {
            addErrorMessage(`${error}: ${i18n?.error_loading_resources || "Could not load resources"}`);
        }
    }, [actionLoadError, resourceLoadError, addErrorMessage, i18n]);

    return (
        <>
            <Box padding="xl">
                <Container>
                    {loading && <Loader />}

                    <form
                        onSubmit={(e) => {
                            e.preventDefault();
                            handleSubmit();
                        }}
                        className={"create-form--container"}>
                        <Form
                            actions={
                                <SpaceBetween direction="horizontal" size="xs">
                                    <Button formAction="none" variant="link" href={"/system/permissionManagement"}>
                                        {i18n?.button_cancel || "Cancel"}
                                    </Button>
                                    <Button variant="primary">{i18n?.button_create || "Create"}</Button>
                                </SpaceBetween>
                            }
                            header={<Header variant="h1">{i18n?.permission_creation_header || "Create permission"}</Header>}>
                            <SpaceBetween direction="vertical" size="l">
                                <FormField
                                    key="cu-form-3"
                                    label={i18n?.permission_creation_name || "Name *"}
                                    errorText={!isNameValid && isFormSubmitted ? i18n?.permission_creation_name_error || "Please enter a valid name" : null}>
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
                                    label={i18n?.permission_creation_action || "Action *"}
                                    errorText={!isActionValid && isFormSubmitted ? i18n?.permission_creation_action_error || "Please select an action" : null}>
                                    <Select
                                        selectedOption={selectedAction}
                                        onChange={({ detail }) => {
                                            setSelectedAction(detail.selectedOption);
                                            validateAction(detail.selectedOption);
                                        }}
                                        options={
                                            actions?.data && Array.isArray(actions?.data)
                                                ? [
                                                      ...actions?.data.map((action: any) => {
                                                          return { label: action.name, value: action.id };
                                                      }),
                                                  ]
                                                : []
                                        }
                                        loadingText={i18n?.permission_creation_action_loading || "Loading actions..."}
                                        placeholder={i18n?.permission_creation_action_placeholder || "Choose an action"}
                                        selectedAriaLabel={i18n?.permission_creation_action_selected_aria || "Selected action"}
                                        statusType={actionsLoading ? "loading" : actionLoadError ? "error" : "finished"}
                                    />
                                </FormField>
                                <FormField
                                    key={"per-form--3"}
                                    label={i18n?.permission_creation_resource || "Resource *"}
                                    errorText={
                                        !isResourceValid && isFormSubmitted ? i18n?.permission_creation_resource_error || "Please select a resource" : null
                                    }>
                                    <Select
                                        selectedOption={selectedResource}
                                        onChange={({ detail }) => {
                                            setSelectedResource(detail.selectedOption);
                                            validateResource(detail.selectedOption);
                                        }}
                                        options={
                                            resources?.data && Array.isArray(resources?.data)
                                                ? [
                                                      ...resources?.data.map((resource: any) => {
                                                          return { label: resource.name, value: resource.id };
                                                      }),
                                                  ]
                                                : []
                                        }
                                        loadingText={i18n?.permission_creation_resource_loading || "Loading resources..."}
                                        placeholder={i18n?.permission_creation_resource_placeholder || "Choose a resource"}
                                        selectedAriaLabel={i18n?.permission_creation_resource_selected_aria || "Selected resource"}
                                        statusType={resourcesLoading ? "loading" : resourceLoadError ? "error" : "finished"}
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
