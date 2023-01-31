import * as React from "react";
import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button from "@cloudscape-design/components/button";
import Header from "@cloudscape-design/components/header";
import {Alert, Flashbar, FormField, Input, Toggle} from "@cloudscape-design/components";
import Calendar from "@cloudscape-design/components/calendar";
import axios from "axios";
import {useEffect} from "react";
import {User} from "radien";
import {Loader} from "@/components/Loader/Loader";
import Link from "next/link";


export default () => {
    const [email, setEmail] = React.useState("");
    const [firstName, setFirstName] = React.useState("");
    const [lastName, setLastName] = React.useState("");

    const [username, setUsername] = React.useState("");
    const [sub, setSub] = React.useState("");
    const [enabled, setEnabled] = React.useState(false);
    const [delegateCreation, setDelegateCreation] = React.useState(false);
    const [terminationDate, setTerminationDate] = React.useState("");

    const [isEmailTouched, setIsEmailTouched] = React.useState(false);
    const [isFirstNameTouched, setIsFirstNameTouched] = React.useState(false);
    const [isLastNameTouched, setIsLastNameTouched] = React.useState(false);
    const [isEmailValid, setIsEmailValid] = React.useState(false);

    const [isUsernameTouched, setIsUsernameTouched] = React.useState(false);
    const [isSubTouched, setIsSubTouched] = React.useState(false);

    const [isFirstNameValid, setIsFirstNameValid] = React.useState(false);
    const [isLastNameValid, setIsLastNameValid] = React.useState(false);
    const [isFormValid, setIsFormValid] = React.useState(false);
    const [isUsernameValid, setIsUsernameValid] = React.useState(true);
    const [isSubValid, setIsSubValid] = React.useState(true);

    const [formError, setFormError] = React.useState("");
    const [loading, setLoading] = React.useState(false);
    const [success, setSuccess] = React.useState(false);

    useEffect(() => {
        validateAll();
    }, [isEmailValid, isLastNameValid, isFirstNameValid, username, sub, enabled, delegateCreation]);

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
        if (firstName.trim().length > 3 && firstName.trim().length < 20) {
            setIsFirstNameValid(true);
        } else {
            setIsFirstNameValid(false);
        }
    }

    const validateLastName = (lastName: string) => {
        if (lastName.trim().length > 3) {
            setIsLastNameValid(true);
        } else {
            setIsLastNameValid(false);
        }
    }

    const validateUsername = (username: string) => {
        setIsUsernameValid(true);
    }

    const validateSub = (sub: string) => {
        setIsSubValid(true);
    }


    const validateAll = () => {
        setIsFormValid(isEmailValid && isFirstNameValid && isLastNameValid);
    }

    const handleSubmit = async () => {
        setIsEmailTouched(true);
        setIsFirstNameTouched(true);
        setIsLastNameTouched(true);
        setIsSubTouched(true);
        setIsUsernameTouched(true);

        if (!isFormValid) {
            setFormError("Please fill out all required fields");
            return;
        }
        setFormError("");
        setLoading(true);
        setSuccess(false);
        try {
            const newUser: User = {
                sub: sub,
                firstname: firstName,
                lastname: lastName,
                userEmail: email,
                logon: username,
                mobileNumber: '',
                enabled: enabled,
                delegatedCreation: delegateCreation,
                terminationDate: new Date(terminationDate),
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
        <div className={"create-form--wrapper"}>
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
                        <FormField key="cu-form-1" label={"email *"} errorText={!isEmailValid && isEmailTouched ? "Please enter a valid email address" : null} >
                            <Input
                                value={email}
                                onChange={(event) => {
                                    setEmail(event.detail.value);
                                    validateEmail(event.detail.value)
                                }}
                            />
                        </FormField>
                        <FormField key="cu-form-2" label={"first name *"} errorText={!isFirstNameValid && isFirstNameTouched ? "Please enter a valid first name" : null} >
                            <Input
                                value={firstName}
                                onChange={(event) => {
                                    setFirstName(event.detail.value);
                                    validateFirstName(event.detail.value)
                                }}
                            />
                        </FormField>
                        <FormField key="cu-form-3" label={"last name *"} errorText={!isLastNameValid && isLastNameTouched ? "Please enter a valid last name" : null} >
                            <Input
                                value={lastName}
                                onChange={(event) => {
                                    setLastName(event.detail.value)
                                    validateLastName(event.detail.value)
                                }}
                            />
                        </FormField>

                        <FormField key="cu-form-4" label={"Username"} errorText={!isUsernameValid && isUsernameTouched ? "Please enter a valid username" : null} >
                            <Input
                                value={username}
                                onChange={(event) => {
                                    setUsername(event.detail.value)
                                    validateUsername(event.detail.value)
                                }}
                            />
                        </FormField>

                        <FormField key="cu-form-5" label={"Sub"} errorText={!isSubValid && isSubTouched ? "Please enter a sub" : null} >
                            <Input
                                value={sub}
                                onChange={(event) => {
                                    setSub(event.detail.value)
                                    validateSub(event.detail.value)
                                }}
                            />
                        </FormField>
                        <SpaceBetween key="cu-form-6" direction={'horizontal'} size={'l'}>
                            Enabled
                            <Toggle
                                onChange={(event) =>
                                    setEnabled(event.detail.checked)
                                }
                                checked={enabled}
                            />
                        </SpaceBetween>

                        <SpaceBetween key="cu-form-7" direction={'horizontal'} size={'l'}>
                            Delegate creation
                            <Toggle
                                onChange={(event) =>
                                    setDelegateCreation(event.detail.checked)
                                }
                                checked={delegateCreation}
                            />
                        </SpaceBetween>
                    </SpaceBetween>
                    <div className="calendar-form--container">
                        Termination date
                        <Calendar
                            className={"calendar-create-user-form"}
                            onChange={({ detail }) => setTerminationDate(detail.value)}
                            value={terminationDate}
                            isDateEnabled={date => date > new Date()}
                            nextMonthAriaLabel="Next month"
                            previousMonthAriaLabel="Previous month"
                            todayAriaLabel="Today"
                            ariaLabel={"Termination date"}
                        />
                    </div>

                </Form>

            </form>
        </div>
    );
}