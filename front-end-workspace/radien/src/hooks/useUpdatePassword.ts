import { useMutation, useQueryClient } from "react-query";
import { UserPasswordChanging } from "radien";
import axios, { AxiosError } from "axios";
import { QueryKeys } from "@/consts";
import { useContext } from "react";
import { RadienContext } from "@/context/RadienContextProvider";

export default function useUpdatePassword() {
    const { addSuccessMessage, addErrorMessage, i18n } = useContext(RadienContext);
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (passwordChanginUser: UserPasswordChanging) => axios.post(`/api/user/updatePassword`, passwordChanginUser),
        onSuccess: (e) => {
            if (e.data) {
                if (e.data.key == "error.invalid.credentials") {
                    let invalidOldPasswordMessage = `${i18n?.generic_message_error || "Error"}: ${
                        i18n?.error_not_your_old_password || "You got the old password wrong"
                    }`;
                    addErrorMessage(invalidOldPasswordMessage);
                } else {
                    const newString = e.data.substr(1);
                    const arrayError: string[] = newString.substr(0, newString.length - 1).split(",");
                    arrayError.map((error) => {
                        addErrorMessage(error);
                    });
                }
                throw new Error("Error: something bad happened");
            } else {
                queryClient.invalidateQueries({ queryKey: QueryKeys.ME });
                let message = `${i18n?.generic_message_success || "Success"}: ${i18n?.password_change_success || "Password updated successfully"}`;
                addSuccessMessage(message);
            }
        },
        onError: (e) => {
            if (e instanceof AxiosError) {
                console.log(e);
                let message = i18n?.generic_message_error || "Error";
                if (e.response?.status === 401) {
                    message = `${message}: ${i18n?.error_not_logged_in || "You are not logged in, please login again"}`;
                    addErrorMessage(message);
                } else {
                    addErrorMessage(`${message}: ${e.message}`);
                }
            }
        },
    });
}
