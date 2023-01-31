import * as React from "react";
import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button from "@cloudscape-design/components/button";
import Header from "@cloudscape-design/components/header";
import {Alert, Box, Container, FormField, Input} from "@cloudscape-design/components";
import axios from "axios";
import {useEffect, useState} from "react";
import {Loader} from "@/components/Loader/Loader";
import {User} from "radien";


export default () => {
    const [email, setEmail] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");

    const [isEmailTouched, setIsEmailTouched] = useState(false);
    const [isFirstNameTouched, setIsFirstNameTouched] = useState(false);
    const [isLastNameTouched, setIsLastNameTouched] = useState(false);
    const [isEmailValid, setIsEmailValid] = useState(false);

    const [isFirstNameValid, setIsFirstNameValid] = useState(false);
    const [isLastNameValid, setIsLastNameValid] = useState(false);
    const [isFormValid, setIsFormValid] = useState(false);

    const [formError, setFormError] = useState("");
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState(false);

    useEffect(() => {
        validateAll();
    }, [isEmailValid, isLastNameValid, isFirstNameValid]);

    const validateEmail = (email: string) => {
        const re = /\S+@\S+\.\S+/;
        const res = re.test(email);
        if (res) {
            setIsEmailValid(true);
        } else {
            setIsEmailValid(false);
        }
    }

    const validateFirstName = (firstName: string) => {
        if (firstName.trim().length > 0) {
            setIsFirstNameValid(true);
        } else {
            setIsFirstNameValid(false);
        }
    }

    const validateLastName = (lastName: string) => {
        if (lastName.trim().length > 0) {
            setIsLastNameValid(true);
        } else {
            setIsLastNameValid(false);
        }
    }

    const validateAll = () => {
        setIsFormValid(isEmailValid && isFirstNameValid && isLastNameValid);
    }

    const handleSubmit = async () => {
        setIsEmailTouched(true);
        setIsFirstNameTouched(true);
        setIsLastNameTouched(true);

        if (!isFormValid) {
            setFormError("Please fill out all required fields");
            return;
        }
        setFormError("");
        setLoading(true);
        setSuccess(false);
        try {
            const newUser: User = {
                sub: "",
                firstname: firstName,
                lastname: lastName,
                userEmail: email,
                logon: email,
                mobileNumber: '',
                enabled: true,
                delegatedCreation: true,
                terminationDate: new Date(),
                createDate: new Date(),
                lastUpdate: new Date()
            }
            await axios.post("/api/user/createUser", newUser);
            setSuccess(true);
        } catch (e) {
            console.log(e);
            // @ts-ignore
            if (e.response.status === 401) {
                setFormError("Error: You are not logged in, please login again");
            } else {
                // @ts-ignore
                setFormError(`Error: ${e.message}`);
            }
        }
        setLoading(false);
    }



    return (
        <Box padding="xl">
            <Container>
                    {loading && <Loader/>}
                    {success && <Alert
                        dismissAriaLabel="Close alert"
                        dismissible
                        statusIconAriaLabel="Success"
                        type="success"
                        onDismiss={() => setSuccess(false)}
                    >
                        User created successfully
                    </Alert>}
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
                                <FormField key="cu-form-1" label={"Email *"} errorText={!isEmailValid && isEmailTouched ? "Please enter a valid email address" : null} >
                                    <Input
                                        value={email}
                                        onChange={(event) => {
                                            setEmail(event.detail.value);
                                            validateEmail(event.detail.value)
                                        }}
                                    />
                                </FormField>
                                <FormField key="cu-form-2" label={"Firstname *"} errorText={!isFirstNameValid && isFirstNameTouched ? "Please enter a valid first name" : null} >
                                    <Input
                                        value={firstName}
                                        onChange={(event) => {
                                            setFirstName(event.detail.value);
                                            validateFirstName(event.detail.value)
                                        }}
                                    />
                                </FormField>
                                <FormField key="cu-form-3" label={"Lastname *"} errorText={!isLastNameValid && isLastNameTouched ? "Please enter a valid last name" : null} >
                                    <Input
                                        value={lastName}
                                        onChange={(event) => {
                                            setLastName(event.detail.value)
                                            validateLastName(event.detail.value)
                                        }}
                                    />
                                </FormField>

                            </SpaceBetween>
                        </Form>
                    </form>
            </Container>
        </Box>
    );
}