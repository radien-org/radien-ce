import * as React from "react";
import SpaceBetween from "@cloudscape-design/components/space-between";
import { Box, FormField, Input, Toggle } from "@cloudscape-design/components";
import {FunctionComponent, useContext, useEffect, useState} from "react";
import { User } from "radien";
import { RadienContext } from "@/context/RadienContextProvider";
import useProcessingLockUser from "@/hooks/useProcessingLockUser";
import Checkbox from "@cloudscape-design/components/checkbox";

const UserDetailsView: FunctionComponent<{ data: User }> = ({ data }) => {
    const { i18n } = useContext(RadienContext);
    const processingLockUser = useProcessingLockUser();

    const [processingLocked, setProcessingLocked] = useState<boolean>(data?.processingLocked);

    useEffect(()=> {
        setProcessingLocked(data?.processingLocked)
    }, [data])

    return (
        <Box padding="l">
            <>
                {data && (
                    <>
                        <SpaceBetween direction="vertical" size="l">
                            <FormField key="view-form-1" label={i18n?.user_details_email || "Email"}>
                                <Input disabled={true} value={data!.userEmail} />
                            </FormField>
                            <FormField key="view-form-2" label={i18n?.user_details_firstname || "First Name"}>
                                <Input disabled={true} value={data!.firstname} />
                            </FormField>
                            <FormField key="view-form-3" label={i18n?.user_details_lastname || "Last Name"}>
                                <Input disabled={true} value={data!.lastname} />
                            </FormField>
                            <FormField key="view-form-4" label={i18n?.user_details_username || "Username"}>
                                <Input disabled={true} value={data!.logon} />
                            </FormField>
                            <FormField key="view-form-5" label={i18n?.user_details_subject || "Subject ID"}>
                                <Input disabled={true} value={`${data!.sub}`} />
                            </FormField>
                            <FormField key="view-form-6" label={i18n?.user_details_mobile_number || "Mobile number"}>
                                <Input disabled={true} value={data!.mobileNumber || "-"} />
                            </FormField>
                            <SpaceBetween key="view-form-7" direction={"horizontal"} size={"l"}>
                                {i18n?.user_details_enabled || "Enabled"}
                                <Toggle disabled={true} checked={data!.enabled} />
                            </SpaceBetween>
                            <SpaceBetween key="view-form-10" direction={"horizontal"} size={"l"}>
                                {i18n?.user_details_processing_locked || "Processing Locked"}
                                <Checkbox checked={processingLocked} onChange={({detail}) => {
                                    setProcessingLocked(detail.checked);
                                    processingLockUser.mutate(
                                    {
                                        id: data.id!,
                                        lock: detail.checked})
                                    }}/>
                            </SpaceBetween>
                            <SpaceBetween key="view-form-8" direction={"horizontal"} size={"l"}>
                                {i18n?.user_details_delegated_creation || "Delegated creation"}
                                <Toggle key="space-between-key-1" disabled={true} checked={data!.delegatedCreation} />
                            </SpaceBetween>
                            <FormField key="view-form-9" label={i18n?.user_details_termination_date || "Termination date"}>
                                <Input disabled={true} value={`${data!.terminationDate || "N/A"}`} />
                            </FormField>
                        </SpaceBetween>
                    </>
                )}
            </>
        </Box>
    );
};

export default UserDetailsView;
