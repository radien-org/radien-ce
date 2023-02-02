import * as React from "react";
import SpaceBetween from "@cloudscape-design/components/space-between";
import {Box, Container, FormField, Input, Toggle} from "@cloudscape-design/components";
import {FunctionComponent} from "react";
import {User} from "radien";


const UserDetailsView: FunctionComponent<{data: User}> =  ({data}) => {

    return (
        <Box padding="l">
            <>
                {data && ( <>
                    <SpaceBetween direction="vertical" size="l">
                        <FormField key="view-form-1" label={"Email"}>
                            <Input
                                disabled={true}
                                value={data!.userEmail}
                            />
                        </FormField>
                        <FormField key="view-form-2" label={"Firstname"} >
                            <Input
                                disabled={true}
                                value={data!.firstname}
                            />
                        </FormField>
                        <FormField key="view-form-3" label={"Lastname"} >
                            <Input
                                disabled={true}
                                value={data!.lastname}
                            />
                        </FormField>
                        <FormField key="view-form-4" label={"Username"} >
                            <Input
                                disabled={true}
                                value={data!.logon}
                            />
                        </FormField>
                        <FormField key="view-form-5" label={"Sub"} >
                            <Input
                                disabled={true}
                                value={`${data!.sub}`}
                            />
                        </FormField>
                        <FormField key="view-form-6" label={"Mobile number"} >
                            <Input
                                disabled={true}
                                value={data!.mobileNumber || "-"}
                            />
                        </FormField>
                        <SpaceBetween key="view-form-7" direction={"horizontal"} size={"l"}>
                            Enabled
                            <Toggle
                                disabled={true}
                                checked={data!.enabled}
                            />
                        </SpaceBetween>
                        <SpaceBetween key="view-form-8" direction={"horizontal"} size={"l"}>
                            Delegated creation
                            <Toggle key="space-between-key-1"
                                disabled={true}
                                checked={data!.delegatedCreation}
                            />
                        </SpaceBetween>
                        <FormField key="view-form-9" label={"Termination date"} >
                            <Input
                                disabled={true}
                                value={`${data!.terminationDate ||  "N/A"}`}
                            />
                        </FormField>
                    </SpaceBetween>
                </> )}

            </>
        </Box>
    );
}

export default UserDetailsView;