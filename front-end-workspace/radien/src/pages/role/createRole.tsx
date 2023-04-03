import * as React from "react";
import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button from "@cloudscape-design/components/button";
import Header from "@cloudscape-design/components/header";
import { Box, Container, Input } from "@cloudscape-design/components";
import { useContext, useEffect, useState } from "react";
import { Loader } from "@/components/Loader/Loader";
import { RadienContext } from "@/context/RadienContextProvider";
import dynamic from "next/dynamic";
import { Role } from "radien";
import useCreateRole from "@/hooks/useCreateRole";
import { useRouter } from "next/router";

const FormField = dynamic(() => import("@cloudscape-design/components/form-field"), { ssr: false });

export default function CreateRole() {
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [isNameValid, setIsNameValid] = useState(false);
    const [isDescriptionValid, setIsDescriptionValid] = useState(false);
    const [isFormSubmitted, setIsFormSubmitted] = useState(false);
    const [isFormValid, setIsFormValid] = useState(false);
    const createRoleMutation = useCreateRole();
    const { i18n } = useContext(RadienContext);
    const { locale } = useRouter();

    useEffect(() => {
        validateAll();
    }, [isNameValid, isDescriptionValid]);

    const validateName = (_name: string) => {
        if (_name.trim().length > 0) {
            setIsNameValid(true);
        } else {
            setIsNameValid(false);
        }
    };

    const validateDescription = (_description: any) => {
        if (_description.trim().length > 0) {
            setIsDescriptionValid(true);
        } else {
            setIsDescriptionValid(false);
        }
    };

    const validateAll = () => {
        setIsFormValid(isNameValid && isDescriptionValid);
    };

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsFormSubmitted(true);
        if (!isFormValid) {
            return;
        }
        const role: Role = {
            name,
            description: String(description),
        };
        createRoleMutation.mutate(role);
    };

    return (
        <>
            <Box padding="xl">
                <Container>
                    {createRoleMutation.isLoading && <Loader />}
                    <form className={"create-form--container"} onSubmit={(e) => handleSubmit(e)}>
                        <Form
                            actions={
                                <SpaceBetween direction="horizontal" size="xs">
                                    <Button formAction="none" variant="link" href={"/system/roleManagement"}>
                                        {i18n?.button_cancel || "Cancel"}
                                    </Button>{" "}
                                    <Button variant="primary">{i18n?.button_create || "Create"}</Button>
                                </SpaceBetween>
                            }
                            header={<Header variant="h1">{i18n?.role_creation_header || "Create Role"}</Header>}>
                            <SpaceBetween direction="vertical" size="l">
                                <FormField
                                    key="cu-form-3"
                                    label={i18n?.create_role_name || "Name*"}
                                    errorText={!isNameValid && isFormSubmitted ? i18n?.create_role_name_error || "Please enter a valid name" : null}>
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
                                    label={i18n?.create_role_description || "Description*"}
                                    errorText={
                                        !isDescriptionValid && isFormSubmitted
                                            ? i18n?.create_role_description_error || "Please enter a valid description"
                                            : null
                                    }>
                                    <Input
                                        value={description}
                                        onChange={(event) => {
                                            setDescription(event.detail.value);
                                            validateDescription(event.detail.value);
                                        }}
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
