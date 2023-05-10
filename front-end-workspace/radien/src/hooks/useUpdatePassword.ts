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
        onSuccess: async (e) => {
            await queryClient.invalidateQueries({ queryKey: QueryKeys.ME });
            let message = `${i18n?.generic_message_success || "Success"}: ${i18n?.password_change_success || "Password updated successfully"}`;
            addSuccessMessage(message);
        },
        onError: (e) => {
            if (e instanceof AxiosError) {
                let message = i18n?.generic_message_error || "Error";
                if (e.response?.status === 401) {
                    message = `${message}: ${i18n?.error_not_logged_in || "You are not logged in, please login again"}`;
                    addErrorMessage(message);
                }
                if (e.response?.status === 400) {
                    if (e.response?.data) {
                        if (e.response.data.key != "error.invalid.credentials") {
                            const errorArray: string[] = e.response.data.replace("[", "").replace("]", "").replace(/ /g, "").split(",");

                            errorArray.map((error) => {
                                let errorMsg = `${message}: ${i18n[error] || error}`;
                                addErrorMessage(errorMsg);
                            });
                        }
                    }
                } else {
                    addErrorMessage(`${message}: ${e.message}`);
                }
            }
        },
    });
}
