import * as React from "react";
import SpaceBetween from "@cloudscape-design/components/space-between";
import { Box, FormField, Input, Spinner, Toggle } from "@cloudscape-design/components";
import { FunctionComponent, useContext, useEffect } from "react";
import { User, UserRequest } from "radien";
import { RadienContext } from "@/context/RadienContextProvider";
import useCheckPermissions from "@/hooks/useCheckPermissions";
import Form from "@cloudscape-design/components/form";
import Button from "@cloudscape-design/components/button";
import useUpdateUser from "@/hooks/useUpdateUser";
import moment from "moment/moment";
import { useState } from "react";
import useProcessingLockUser from "@/hooks/useProcessingLockUser";
import Checkbox from "@cloudscape-design/components/checkbox";

const UserDetailsView: FunctionComponent<{ data: User }> = ({ data }) => {
    const [email, setEmail] = React.useState(data?.userEmail);
    const [firstName, setFirstName] = React.useState(data?.firstname);
    const [lastName, setLastName] = React.useState(data?.lastname);
    const [username, setUsername] = React.useState(data?.logon);
    const [mobileNumber, setMobileNumber] = React.useState(data?.mobileNumber);
    const [enabled, setEnabled] = React.useState(data?.enabled);
    const [delegatedCreation, setDelegatedCreation] = React.useState(data?.delegatedCreation);
    const [terminationDate, setTerminationDate] = React.useState(moment(data?.terminationDate).format("YYYY/MM/DD") || "");
    const [isFirstNameValid, setIsFirstNameValid] = React.useState(true);
    const [isLastNameValid, setIsLastNameValid] = React.useState(true);
    const [isEmailValid, setIsEmailValid] = React.useState(true);
    const [isMobileNumberValid, setIsMobileNumberValid] = React.useState(true);
    const [isTerminationDateValid, setIsTerminationDateValid] = React.useState(true);
    const [isFormSubmitted, setIsFormSubmitted] = React.useState(false);
    const [newUserData, setNewUserData] = React.useState<UserRequest>();
    const {
        userInSession: radienUser,
        i18n,
        activeTenant: { data: activeTenant, isLoading: activeTenantLoading },
        addErrorMessage,
        addSuccessMessage,
    } = useContext(RadienContext);
    const {
        userEdit: { data: canEdit, isLoading: loadingPermission },
    } = useCheckPermissions(radienUser?.id, activeTenant?.id);
    const mutation = useUpdateUser();

    const validateNotEmpty = (value: string) => {
        return value.trim().length > 0;
    };

    const validateMobileNumber = (mobileNumber: string) => {
        if (mobileNumber.trim().length <= 0) return true;
        try {
            return Number(mobileNumber) > 0;
        } catch (e) {
            return false;
        }
    };
    const validateTerminationDate = (terminationDate: string) => {
        try {
            return new Date(terminationDate) > new Date();
        } catch (e) {
            return false;
        }
    };
    const processingLockUser = useProcessingLockUser();

    const [processingLocked, setProcessingLocked] = useState<boolean>(data?.processingLocked);

    useEffect(() => {
        resetForm();
    }, [data]);

    const resetForm = () => {
        const _data = newUserData || data;
        setProcessingLocked(_data?.processingLocked);
        setFirstName(_data?.firstname);
        setLastName(_data?.lastname);
        setUsername(_data?.logon);
        setEmail(_data?.userEmail);
        setMobileNumber(_data?.mobileNumber);
        setEnabled(_data?.enabled);
        setTerminationDate(moment(_data?.terminationDate).format("YYYY/MM/DD") || "");
        setIsFormSubmitted(false);
    };

    const validateEmail = (email: string) => {
        const re = /\S+@\S+\.\S+/;
        return re.test(email);
    };

    const handleUserUpdate = (e: React.SyntheticEvent) => {
        e.preventDefault();
        setIsFormSubmitted(true);
        const isFormValid = isFirstNameValid && isLastNameValid && isEmailValid && isMobileNumberValid && isTerminationDateValid;
        if (!isFormValid) {
            return;
        }
        const newUser = {
            ...data,
            firstname: firstName,
            lastname: lastName,
            logon: username,
            userEmail: email,
            terminationDate: terminationDate,
            mobileNumber: mobileNumber,
            enabled: enabled,
        };
        mutation
            .mutateAsync(newUser)
            .then(() => {
                addSuccessMessage(i18n?.update_user_success || "User updated successfully");
                setNewUserData(newUser);
            })
            .catch((error) => {
                addErrorMessage(i18n?.update_user_error || "Error updating user");
            });
    };

    return (
        <Box padding="s">
            <div className={"overflow-x-hidden overflow-y-scroll"}>
                {data && (
                    <>
                        {mutation.isLoading || activeTenantLoading || loadingPermission || processingLockUser.isLoading ? (
                            <Spinner />
                        ) : (
                            <form onSubmit={handleUserUpdate} className={"create-form--container"}>
                                <Form
                                    actions={
                                        <SpaceBetween direction="horizontal" size="xs">
                                            {canEdit && !processingLocked && (
                                                <>
                                                    <Button formAction="none" onClick={resetForm}>
                                                        {i18n?.update_user_cancel_action || "Cancel"}
                                                    </Button>
                                                    <Button variant="primary">{i18n?.update_user_update_action || "Update"}</Button>
                                                </>
                                            )}
                                        </SpaceBetween>
                                    }>
                                    <SpaceBetween direction="vertical" size="l">
                                        <FormField
                                            key="cu-form-1"
                                            label={i18n?.create_user_email || "Email *"}
                                            errorText={
                                                !isEmailValid && isFormSubmitted ? i18n?.create_user_email_error || "Please enter a valid email address" : null
                                            }>
                                            <Input
                                                disabled={!canEdit || processingLocked}
                                                value={email}
                                                onChange={(event) => {
                                                    setEmail(event.detail.value);
                                                    setIsEmailValid(validateEmail(event.detail.value));
                                                }}
                                            />
                                        </FormField>
                                        <FormField
                                            key="cu-form-2"
                                            label={i18n?.create_user_firstname || "Firstname *"}
                                            errorText={
                                                !isFirstNameValid && isFormSubmitted
                                                    ? i18n?.create_user_firstname_error || "Please enter a valid first name"
                                                    : null
                                            }>
                                            <Input
                                                disabled={!canEdit || processingLocked}
                                                value={firstName}
                                                onChange={(event) => {
                                                    setFirstName(event.detail.value);
                                                    setIsFirstNameValid(validateNotEmpty(event.detail.value));
                                                }}
                                            />
                                        </FormField>
                                        <FormField
                                            key="view-form-2"
                                            label={i18n?.create_user_lastname || "Lastname *"}
                                            errorText={
                                                !isLastNameValid && isFormSubmitted
                                                    ? i18n?.create_user_lastname_error || "Please enter a valid last name"
                                                    : null
                                            }>
                                            <Input
                                                disabled={!canEdit || processingLocked}
                                                value={lastName}
                                                onChange={(event) => {
                                                    setLastName(event.detail.value);
                                                    setIsLastNameValid(validateNotEmpty(event.detail.value));
                                                }}
                                            />
                                        </FormField>
                                        <FormField key="view-update-form-3" label={i18n?.user_details_username || "Username *"}>
                                            <Input
                                                disabled={!canEdit || processingLocked}
                                                value={username}
                                                onChange={(event) => {
                                                    setUsername(event.detail.value);
                                                }}
                                            />
                                        </FormField>
                                        <FormField
                                            key="view-update-form-4"
                                            label={i18n?.user_details_mobile_number || "Mobile number"}
                                            errorText={
                                                !isMobileNumberValid && isFormSubmitted
                                                    ? i18n?.update_user_mobile_number_error || "Please enter a valid mobile number"
                                                    : null
                                            }>
                                            <Input
                                                disabled={!canEdit || processingLocked}
                                                value={mobileNumber}
                                                onChange={(event) => {
                                                    setMobileNumber(event.detail.value);
                                                    setIsMobileNumberValid(validateMobileNumber(event.detail.value));
                                                }}
                                            />
                                        </FormField>
                                        <FormField key="view-update-form-5" label={i18n?.user_details_subject || "Subject ID"}>
                                            <Input disabled={true} value={`${data!.sub}`} />
                                        </FormField>
                                        <SpaceBetween key="view-update-form-7" direction={"horizontal"} size={"l"}>
                                            {i18n?.user_details_enabled || "Enabled"}
                                            <Toggle
                                                disabled={!canEdit || processingLocked}
                                                checked={enabled}
                                                onChange={(event) => setEnabled(event.detail.checked)}
                                            />
                                        </SpaceBetween>
                                        <SpaceBetween key="view-update-form-8" direction={"horizontal"} size={"l"}>
                                            {i18n?.user_details_delegated_creation || "Delegated creation"}
                                            <Toggle
                                                key="space-between-key-1"
                                                disabled={true}
                                                checked={delegatedCreation}
                                                onChange={(event) => setDelegatedCreation(event.detail.checked)}
                                            />
                                        </SpaceBetween>
                                        <SpaceBetween key="view-form-10" direction={"horizontal"} size={"l"}>
                                            {i18n?.user_details_processing_locked || "Processing Locked"}
                                            <Checkbox
                                                disabled={!canEdit}
                                                checked={processingLocked}
                                                onChange={({ detail }) => {
                                                    processingLockUser
                                                        .mutateAsync({
                                                            id: data.id!,
                                                            lock: detail.checked,
                                                        })
                                                        .then(() => {
                                                            addSuccessMessage(i18n?.user_details_processing_locked_success || "Processing locked successfully");
                                                            setProcessingLocked(detail.checked);
                                                            const _newUser = {
                                                                ...data,
                                                                processingLocked: detail.checked,
                                                            };
                                                            setNewUserData(_newUser);
                                                        })
                                                        .catch(() => {
                                                            addErrorMessage(i18n?.user_details_processing_locked_error || "Error locking processing");
                                                        });
                                                }}
                                            />
                                        </SpaceBetween>
                                    </SpaceBetween>
                                </Form>
                            </form>
                        )}
                    </>
                )}
            </div>
        </Box>
    );
};

export default UserDetailsView;
