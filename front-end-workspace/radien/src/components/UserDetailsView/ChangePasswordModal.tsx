import { Box, Button, Modal, SpaceBetween, Input, Form } from "@cloudscape-design/components";
import React, { useContext, useState, useEffect } from "react";

import { RadienContext } from "@/context/RadienContextProvider";
import dynamic from "next/dynamic";
import { UserPasswordChanging } from "radien";
import useUpdatePassword from "@/hooks/useUpdatePassword";
import { error } from "console";
import Image from "next/image";

const FormField = dynamic(() => import("@cloudscape-design/components/form-field"), { ssr: false });

interface PasswordChangeProps {
    modalVisible: boolean;
    setModalVisible: (value: boolean) => void;
}

interface PasswordValidation {
    value: string;
    valid: boolean;
    touched: boolean;
    visible: boolean;
}

interface OldPasswordValidation {
    value: string;
    valid: string;
    touched: boolean;
    visible: boolean;
}

export default function ChangePasswordModal(props: PasswordChangeProps) {
    const { userInSession: radienUser, i18n } = useContext(RadienContext);
    const { modalVisible, setModalVisible } = props;
    const updatePassword = useUpdatePassword();

    const lowecaseRegex = new RegExp("(.*[a-z].*)");
    const uppercaseRegex = new RegExp("(.*[A-Z].*)");
    const numberRegex = new RegExp("(.*\\d.*)");
    const specialCaracterRegex = new RegExp("(.*[!\"`'#%&,:;<>=@{}~_$()*+/\\\\?\\[\\]^|]+.*)");

    const [isFormValid, setIsFormValid] = useState(false);
    const [newPassword, setNewPassword] = useState<PasswordValidation>({
        touched: false,
        valid: false,
        value: "",
        visible: false,
    });
    const [oldPassword, setOldPassword] = useState<OldPasswordValidation>({
        touched: false,
        valid: "notValidated",
        value: "",
        visible: false,
    });
    const [confirmNewPassword, setConfirmNewPassword] = useState<PasswordValidation>({
        touched: false,
        valid: false,
        value: "",
        visible: false,
    });

    const toggleOldPasswordVisibility = () => {
        setOldPassword((prevValue) => {
            return { ...prevValue, visible: !prevValue.visible };
        });
    };

    const toggleNewPasswordVisibility = () => {
        setNewPassword((prevValue) => {
            return { ...prevValue, visible: !prevValue.visible };
        });
    };

    const toggleConfirmNewPasswordVisibility = () => {
        setConfirmNewPassword((prevValue) => {
            return { ...prevValue, visible: !prevValue.visible };
        });
    };

    const saveData = () => {
        const passwordChanginUser: UserPasswordChanging = {
            login: radienUser?.logon!,
            id: radienUser?.sub!,
            oldPassword: oldPassword.value,
            newPassword: newPassword.value,
        };
        updatePassword.mutate(passwordChanginUser, {
            onSuccess() {
                setOldPassword({
                    touched: false,
                    valid: "notValidated",
                    value: "",
                    visible: false,
                });
                setNewPassword({
                    touched: false,
                    valid: false,
                    value: "",
                    visible: false,
                });
                setConfirmNewPassword({
                    touched: false,
                    valid: false,
                    value: "",
                    visible: false,
                });
                setModalVisible(false);
            },
            onError(e: any) {
                if (e.response?.data.key == "error.invalid.credentials") {
                    setOldPassword((prevState) => {
                        return { ...prevState, valid: "notValid" };
                    });
                }
            },
        });
    };

    const validateNewPassword = (password: string) => {
        setNewPassword((prevValue) => {
            return { ...prevValue, valid: true };
        });
        const passwordLowercase = document.getElementById("passwordLowercase");
        const passwordUppercase = document.getElementById("passwordUppercase");
        const passwordDigits = document.getElementById("passwordDigits");
        const passwordLength = document.getElementById("passwordLength");
        const passwordSpecialCaracter = document.getElementById("passwordSpecialCaracter");

        const requirements = document.querySelectorAll(".requirement");
        requirements.forEach((requirement) => {
            requirement.classList.remove("notSatisfied");
        });

        setNewPassword((prevValue) => {
            return { ...prevValue, valid: true };
        });

        if (!lowecaseRegex.test(password)) {
            passwordLowercase?.classList.add("notSatisfied");
            setNewPassword((prevValue) => {
                return { ...prevValue, valid: false };
            });
        }
        if (!uppercaseRegex.test(password)) {
            passwordUppercase?.classList.add("notSatisfied");
            setNewPassword((prevValue) => {
                return { ...prevValue, valid: false };
            });
        }
        if (!numberRegex.test(password)) {
            passwordDigits?.classList.add("notSatisfied");
            setNewPassword((prevValue) => {
                return { ...prevValue, valid: false };
            });
        }
        if (!specialCaracterRegex.test(password)) {
            passwordSpecialCaracter?.classList.add("notSatisfied");
            setNewPassword((prevValue) => {
                return { ...prevValue, valid: false };
            });
        }
        if (password.trim().length < 7) {
            passwordLength?.classList.add("notSatisfied");
            setNewPassword((prevValue) => {
                return { ...prevValue, valid: false };
            });
        }
    };

    const validateConfirmNewPassword = (confirmNewPwd: string, newPwd: string) => {
        if (!confirmNewPwd) {
            confirmNewPwd = confirmNewPassword.value;
        }
        if (!newPwd) {
            newPwd = newPassword.value;
        }

        setConfirmNewPassword((prevValue) => {
            return { ...prevValue, valid: confirmNewPwd == newPwd };
        });
    };

    const validateAll = () => {
        setIsFormValid(newPassword.valid && confirmNewPassword.valid);
    };

    const handleSubmit = async () => {
        setOldPassword((prevValue) => {
            return { ...prevValue, touched: true };
        });
        setNewPassword((prevValue) => {
            return { ...prevValue, touched: true };
        });
        setConfirmNewPassword((prevValue) => {
            return { ...prevValue, touched: true };
        });

        if (!isFormValid) {
            return;
        }
        saveData();
    };

    useEffect(() => {
        validateAll();
    }, [newPassword, confirmNewPassword]);

    return (
        <Modal
            onDismiss={() => setModalVisible(false)}
            visible={modalVisible}
            closeAriaLabel="Close modal"
            footer={
                <Box float="right">
                    <SpaceBetween direction="horizontal" size="xs">
                        <Button variant="link" onClick={() => setModalVisible(false)}>
                            {i18n?.tenant_request_button_cancel || "Cancel"}
                        </Button>
                        <Button id="submitBtn" variant="primary" onClick={() => handleSubmit()}>
                            {i18n?.button_update || "Update"}
                        </Button>
                    </SpaceBetween>
                </Box>
            }
            header={i18n?.password_change_header || "Change password"}>
            <form
                onSubmit={async (e) => {
                    e.preventDefault();
                    await handleSubmit();
                }}
                className={"create-form--container"}>
                <Form>
                    <FormField
                        label={i18n?.user_profile_old_password || "Old password"}
                        errorText={
                            oldPassword.valid == "notValid" && oldPassword.touched
                                ? i18n?.error_not_your_old_password || "You got the old password wrong"
                                : null
                        }
                        stretch={false}>
                        <Input
                            name="oldPasswordInput"
                            value={oldPassword.value}
                            onChange={(event) => {
                                setOldPassword((prevState) => {
                                    return { ...prevState, value: event.detail.value, valid: "notValidated" };
                                });
                            }}
                            type={oldPassword.visible ? "text" : "password"}
                        />
                        <div className="absolute inset-y-0 right-0 pr-3 flex items-center cursor-pointer" onClick={toggleOldPasswordVisibility}>
                            {oldPassword.visible ? (
                                <Image src="/icons/eye.svg" alt="Password visible" width="16" height="16" className="mr-[15px]" />
                            ) : (
                                <Image src="/icons/eye_not_visible.svg" alt="Password not visible" width="16" height="16" className="mr-[15px]" />
                            )}
                        </div>
                    </FormField>
                    <FormField
                        label={i18n?.user_profile_new_password || "New password"}
                        constraintText={
                            i18n?.user_profile_requirements_password_html || (
                                <div>
                                    {i18n?.user_profile_requirements_password_title || "Requirements for a new password:"}{" "}
                                    <ul id="passwordRequirements" className="ml-[20px] list-disc">
                                        <li className="requirement" id="passwordLength">
                                            {i18n?.password_requirements_length || "At least 8 characters long"}
                                        </li>
                                        <li>
                                            <ul className="list-disc">
                                                {i18n?.user_profile_requirements_password_caracter_title ||
                                                    "Must include at least one character from each of the following categories:"}
                                                <li className="ml-[10px] requirement" id="passwordSpecialCaracter">
                                                    {i18n?.password_requirements_special_characters || "Special Characters (!,@,#,$,%,&,*)"}{" "}
                                                </li>
                                                <li className="ml-[10px] requirement" id="passwordLowercase">
                                                    {i18n?.password_requirements_lowercase_letters || "Lowercase letters (a-z)"}
                                                </li>
                                                <li className="ml-[10px] requirement" id="passwordUppercase">
                                                    {i18n?.password_requirements_uppercase_letters || "Uppercase letters (A-Z)"}
                                                </li>
                                                <li className="ml-[10px] requirement" id="passwordDigits">
                                                    {i18n?.password_requirements_digits || "Digits (0-9)"}
                                                </li>
                                            </ul>
                                        </li>
                                    </ul>
                                </div>
                            )
                        }
                        errorText={
                            !newPassword.valid && newPassword.touched
                                ? i18n?.new_password_error || "You missed some requirements. Please check below to see what you're missing"
                                : null
                        }
                        stretch={false}>
                        <Input
                            name="newPasswordInput"
                            value={newPassword.value}
                            onChange={(event) => {
                                setNewPassword((prevState) => {
                                    return { ...prevState, value: event.detail.value };
                                });
                                validateNewPassword(event.detail.value);
                                validateConfirmNewPassword("", event.detail.value);
                            }}
                            type={newPassword.visible ? "text" : "password"}
                        />
                        <div className="absolute inset-y-0 right-0 pr-3 flex items-center cursor-pointer" onClick={toggleNewPasswordVisibility}>
                            {newPassword.visible ? (
                                <Image src="/icons/eye.svg" alt="Password visible" width="16" height="16" className="mr-[15px]" />
                            ) : (
                                <Image src="/icons/eye_not_visible.svg" alt="Password not visible" width="16" height="16" className="mr-[15px]" />
                            )}
                        </div>
                    </FormField>
                    <FormField
                        label={i18n?.user_profile_confirm_new_password || "Confirm new password"}
                        errorText={!confirmNewPassword.valid && confirmNewPassword.touched ? i18n?.confirm_new_password_error || "Passwords don't match" : null}
                        stretch={false}>
                        <Input
                            name="confirmNewPasswordInput"
                            value={confirmNewPassword.value}
                            onChange={(event) => {
                                setConfirmNewPassword((prevState) => {
                                    return { ...prevState, value: event.detail.value };
                                });
                                validateConfirmNewPassword(event.detail.value, "");
                            }}
                            type={confirmNewPassword.visible ? "text" : "password"}
                        />
                        <div className="absolute inset-y-0 right-0 pr-3 flex items-center cursor-pointer" onClick={toggleConfirmNewPasswordVisibility}>
                            {confirmNewPassword.visible ? (
                                <Image src="/icons/eye.svg" alt="Password visible" width="16" height="16" className="mr-[15px]" />
                            ) : (
                                <Image src="/icons/eye_not_visible.svg" alt="Password not visible" width="16" height="16" className="mr-[15px]" />
                            )}
                        </div>
                    </FormField>
                </Form>
            </form>
        </Modal>
    );
}
