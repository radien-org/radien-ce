import * as React from "react";
import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button from "@cloudscape-design/components/button";
import Header from "@cloudscape-design/components/header";
import {Box, Container, FormField, Input} from "@cloudscape-design/components";
import {useContext, useEffect, useState} from "react";
import {Loader} from "@/components/Loader/Loader";
import {RadienContext} from "@/context/RadienContextProvider";
import useCreateUser from "@/hooks/useCreateUser";
import {User} from "radien";


export default function CreateUser() {
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

    const [loading, setLoading] = useState(false);

    const {i18n} = useContext(RadienContext);
    const createUser = useCreateUser();


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
            return;
        }
        setLoading(true);
        const newUser: Omit<User, "sub" | "mobileNumber"> = {
            firstname: firstName,
            lastname: lastName,
            userEmail: email,
            logon: email,
            enabled: true,
            delegatedCreation: false,
            terminationDate: new Date(),
            createDate: new Date(),
            lastUpdate: new Date()
        }

        createUser.mutate(newUser);
        setLoading(false);
    }

    return (
        <>
            <Box padding="xl">
                <Container>
                    {loading && <Loader/>}

                    <form onSubmit={e => {e.preventDefault(); handleSubmit(); }}
                          className={"create-form--container"}>
                        <Form
                            actions={
                                <SpaceBetween direction="horizontal" size="xs">
                                    <Button formAction="none" variant="link" href={"/system/userManagement"}>
                                        {i18n?.create_user_cancel_action || "Cancel"}
                                    </Button>
                                    <Button variant="primary">${i18n?.create_user_create_action || "Create"}</Button>
                                </SpaceBetween>
                            }
                            header={<Header variant="h1">{i18n?.create_user_form_header || "Create user"}</Header>}>
                            <SpaceBetween direction="vertical" size="l">
                                <FormField key="cu-form-1"
                                           label={i18n?.create_user_email || "Email *"}
                                           errorText={!isEmailValid && isEmailTouched ? i18n?.create_user_email_error || "Please enter a valid email address" : null} >
                                    <Input
                                        value={email}
                                        onChange={(event) => {
                                            setEmail(event.detail.value);
                                            validateEmail(event.detail.value)
                                        }}
                                    />
                                </FormField>
                                <FormField key="cu-form-2"
                                           label={i18n?.create_user_firstname || "Firstname *"}
                                           errorText={!isFirstNameValid && isFirstNameTouched ? i18n?.create_user_firstname_error || "Please enter a valid first name" : null} >
                                    <Input
                                        value={firstName}
                                        onChange={(event) => {
                                            setFirstName(event.detail.value);
                                            validateFirstName(event.detail.value)
                                        }}
                                    />
                                </FormField>
                                <FormField key="cu-form-3"
                                           label={i18n?.create_user_lastname || "Lastname *"}
                                           errorText={!isLastNameValid && isLastNameTouched ? i18n?.create_user_lastname_error || "Please enter a valid last name" : null} >
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
        </>
    );
}