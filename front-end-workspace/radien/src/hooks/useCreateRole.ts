import { useMutation, useQueryClient } from "react-query";
import { Role } from "radien";
import axios, { AxiosError } from "axios";
import { QueryKeys } from "@/consts";
import { useContext } from "react";
import { RadienContext } from "@/context/RadienContextProvider";

export default function useCreateRole() {
    const { addSuccessMessage, addErrorMessage, i18n } = useContext(RadienContext);
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (newRole: Role) => axios.post(`/api/role/role/createRole`, newRole),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: QueryKeys.ROLE_MANAGEMENT });
            let message = `${i18n?.generic_message_success || "Success"}: ${i18n?.role_creation_success || "Role created successfully"})`;
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
