import { FunctionComponent } from "react";
import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button from "@cloudscape-design/components/button";
import { Box, Input, Select, Spinner } from "@cloudscape-design/components";
import { useContext, useEffect, useState } from "react";
import { RadienContext } from "@/context/RadienContextProvider";
import dynamic from "next/dynamic";
import { Role } from "radien";
import useCheckPermissions from "@/hooks/useCheckPermissions";
import useUpdateRole from "@/hooks/useUpdateRole";
import * as React from "react";

const FormField = dynamic(() => import("@cloudscape-design/components/form-field"), { ssr: false });

export const RoleDetailsView: FunctionComponent<{ data: Role }> = ({ data }) => {
    const [name, setName] = useState(data?.name);
    const [description, setDescription] = useState(data?.description);
    const [isNameValid, setIsNameValid] = useState(true);
    const [isDescriptionValid, setIsDescriptionValid] = useState(true);
    const [isFormSubmitted, setIsFormSubmitted] = useState(false);
    const [newRoleData, setNewRoleData] = useState<Role>();
    const mutation = useUpdateRole();
    const {
        userInSession: radienUser,
        i18n,
        activeTenant: { data: activeTenant, isLoading: activeTenantLoading },
        addErrorMessage,
        addSuccessMessage,
    } = useContext(RadienContext);
    const {
        roleEdit: { data: canEdit, isLoading: loadingPermission },
    } = useCheckPermissions(radienUser?.id, activeTenant?.id);

    const validateNotEmpty = (value: string) => {
        return value.trim().length > 0;
    };

    const resetForm = () => {
        const _data = newRoleData || data;
        setName(_data?.name);
        setDescription(_data?.description);
        setIsFormSubmitted(false);
    };

    useEffect(() => {
        resetForm();
    }, [data]);

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsFormSubmitted(true);
        const isFormValid = isNameValid && isDescriptionValid;
        if (!isFormValid) {
            return;
        }
        const role = {
            ...data,
            name,
            description,
        };
        mutation
            .mutateAsync(role)
            .then(() => {
                addSuccessMessage(i18n?.role_update_success || "Role updated successfully");
                setNewRoleData(role);
            })
            .catch(() => {
                addErrorMessage(i18n?.role_update_error || "Error updating role");
            });
    };

    return (
        <>
            {data && (
                <Box padding="s">
                    <>
                        {mutation.isLoading || loadingPermission || activeTenantLoading ? (
                            <Spinner />
                        ) : (
                            <form className={"create-form--container"} onSubmit={(e) => handleSubmit(e)}>
                                <Form
                                    actions={
                                        <SpaceBetween direction="horizontal" size="xs">
                                            <Button formAction="none" onClick={resetForm}>
                                                {i18n?.button_cancel || "Reset"}
                                            </Button>{" "}
                                            <Button variant="primary">{i18n?.button_update || "Update"}</Button>
                                        </SpaceBetween>
                                    }>
                                    <SpaceBetween direction="vertical" size="l">
                                        <FormField
                                            key="cu-form-3"
                                            label={i18n?.create_role_name || "Name*"}
                                            errorText={!isNameValid && isFormSubmitted ? i18n?.create_role_name_error || "Please enter a valid name" : null}>
                                            <Input
                                                disabled={!canEdit}
                                                value={name}
                                                onChange={(event) => {
                                                    setName(event.detail.value);
                                                    setIsNameValid(validateNotEmpty(event.detail.value));
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
                                                disabled={!canEdit}
                                                value={description}
                                                onChange={(event) => {
                                                    setDescription(event.detail.value);
                                                    setIsDescriptionValid(validateNotEmpty(event.detail.value));
                                                }}
                                            />
                                        </FormField>
                                    </SpaceBetween>
                                </Form>
                            </form>
                        )}
                    </>
                </Box>
            )}
        </>
    );
};
