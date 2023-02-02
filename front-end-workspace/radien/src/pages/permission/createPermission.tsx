import * as React from "react";
import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button from "@cloudscape-design/components/button";
import Header from "@cloudscape-design/components/header";
import { Box, Container, FormField, Input} from "@cloudscape-design/components";
import axios from "axios";
import {useContext, useEffect, useState} from "react";
import {Loader} from "@/components/Loader/Loader";
import {RadienContext} from "@/context/RadienContextProvider";


export default function createPermission() {
    const [name, setName] = useState("");

    const [isNameValid, setIsNameValid] = useState(false);
    const [isNameTouched, setIsNameTouched] = useState(false);
    const [isFormValid, setIsFormValid] = useState(false);
    const [formError, setFormError] = useState("");
    const [loading, setLoading] = useState(false);

    const {addSuccessMessage, addErrorMessage} = useContext(RadienContext);


    useEffect(() => {
        validateAll();
    }, [isNameValid]);

    const validateName = (_name: string) => {
        if (_name.trim().length > 0) {
            setIsNameValid(true);
        } else {
            setIsNameValid(false);
        }
    }

    const validateAll = () => {
        setIsFormValid(isNameValid);
    }

    const handleSubmit = async () => {
        setIsNameTouched(true);

        if (!isFormValid) {
            setFormError("Please fill out all required fields");
            return;
        }
        setFormError("");
        setLoading(true);
        try {
            const permission = {}
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
                                    <Button formAction="none" variant="link" href={"/system/userManagement"}>
                                        Cancel
                                    </Button>
                                    <Button variant="primary">Create</Button>
                                </SpaceBetween>
                            }
                            errorText={formError}
                            header={<Header variant="h1">Create user</Header>}
                        >
                            <SpaceBetween direction="vertical" size="l">
                                <FormField key="cu-form-3" label={"Lastname *"} errorText={!isNameValid && isNameTouched ? "Please enter a valid name" : null} >
                                    <Input
                                        value={name}
                                        onChange={(event) => {
                                            setName(event.detail.value)
                                            validateName(event.detail.value)
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