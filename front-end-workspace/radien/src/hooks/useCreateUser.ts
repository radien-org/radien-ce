import { useMutation, useQueryClient } from "react-query";
import { User } from "radien";
import axios, { AxiosError } from "axios";
import { QueryKeys } from "@/consts";
import { useContext } from "react";
import { RadienContext } from "@/context/RadienContextProvider";

export default function useCreateUser() {
    const { addSuccessMessage, addErrorMessage, i18n } = useContext(RadienContext);
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (newUser: Omit<User, "sub" | "mobileNumber">) => axios.post(`/api/user/createUser`, newUser),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: QueryKeys.USER_MANAGEMENT });
            let message = `${i18n?.generic_message_success || "Success"}: ${i18n?.create_user_success || "User created successfully"}`;
            addSuccessMessage(message);
        },
        onError: (e) => {
            if (e instanceof AxiosError) {
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
