import { useMutation, useQueryClient } from "react-query";
import { Permission } from "radien";
import axios, { AxiosError } from "axios";
import { QueryKeys } from "@/consts";
import { useContext } from "react";
import { RadienContext } from "@/context/RadienContextProvider";

export default function useCreatePermission() {
    const { addSuccessMessage, addErrorMessage, i18n } = useContext(RadienContext);
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (newPermission: Permission) => axios.post(`/api/permission/permission/createPermission`, newPermission),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: QueryKeys.PERMISSION_MANAGEMENT });
            let message = `${i18n?.generic_message_success || "Success"}: ${i18n?.permission_creation_success || "Permission created successfully"})`;
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
