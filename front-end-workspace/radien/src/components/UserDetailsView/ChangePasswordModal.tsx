import { Box, Button, Modal, SpaceBetween, Input, Form } from "@cloudscape-design/components";
import React, { useContext, useState, useEffect } from "react";

import { RadienContext } from "@/context/RadienContextProvider";
import dynamic from "next/dynamic";
import { UserPasswordChanging } from "radien";
import useUpdatePassword from "@/hooks/useUpdatePassword";

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

const passwordVisibleIcon = (
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="mr-[15px]" viewBox="0 0 16 16">
        <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z" />
        <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z" />
    </svg>
);

const passwordNotVisibleIcon = (
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="mr-[15px]" viewBox="0 0 16 16">
        <path d="m10.79 12.912-1.614-1.615a3.5 3.5 0 0 1-4.474-4.474l-2.06-2.06C.938 6.278 0 8 0 8s3 5.5 8 5.5a7.029 7.029 0 0 0 2.79-.588zM5.21 3.088A7.028 7.028 0 0 1 8 2.5c5 0 8 5.5 8 5.5s-.939 1.721-2.641 3.238l-2.062-2.062a3.5 3.5 0 0 0-4.474-4.474L5.21 3.089z" />
        <path d="M5.525 7.646a2.5 2.5 0 0 0 2.829 2.829l-2.83-2.829zm4.95.708-2.829-2.83a2.5 2.5 0 0 1 2.829 2.829zm3.171 6-12-12 .708-.708 12 12-.708.708z" />
    </svg>
);

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
    const [oldPassword, setOldPassword] = useState<PasswordValidation>({
        touched: false,
        valid: false,
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
                    valid: false,
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
        });
    };

    const validateNewPassword = (password: string) => {
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
                        <Button variant="primary" onClick={() => handleSubmit()}>
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
                    <FormField label={i18n?.user_profile_old_password || "Old password"} stretch={false}>
                        <Input
                            value={oldPassword.value}
                            onChange={(event) => {
                                setOldPassword((prevState) => {
                                    return { ...prevState, value: event.detail.value };
                                });
                                /* validateOldPassword(event.detail.value); */
                            }}
                            type={oldPassword.visible ? "text" : "password"}
                        />
                        <div className="absolute inset-y-0 right-0 pr-3 flex items-center cursor-pointer" onClick={toggleOldPasswordVisibility}>
                            {oldPassword.visible ? passwordVisibleIcon : passwordNotVisibleIcon}
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
                            {newPassword.visible ? passwordVisibleIcon : passwordNotVisibleIcon}
                        </div>
                    </FormField>
                    <FormField
                        label={i18n?.user_profile_confirm_new_password || "Confirm new password"}
                        errorText={!confirmNewPassword.valid && confirmNewPassword.touched ? i18n?.confirm_new_password_error || "Passwords don't match" : null}
                        stretch={false}>
                        <Input
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
                            {confirmNewPassword.visible ? passwordVisibleIcon : passwordNotVisibleIcon}
                        </div>
                    </FormField>
                </Form>
            </form>
        </Modal>
    );
}
