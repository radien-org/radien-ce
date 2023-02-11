import { useMutation, useQueryClient } from "react-query";
import axios, { AxiosError } from "axios";
import { QueryKeys } from "@/consts";
import { useContext } from "react";
import { RadienContext } from "@/context/RadienContextProvider";
import { DeleteParams } from "@/components/PaginatedTable/PaginatedTable";

export default function useDeletePermission() {
    const { addSuccessMessage, addErrorMessage, i18n } = useContext(RadienContext);
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ tenantId, userId }: DeleteParams) => axios.delete(`/api/permission/permission/delete/${tenantId}`),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: QueryKeys.PERMISSION_MANAGEMENT });
            let message = `${i18n?.generic_message_success || "Success"}: ${i18n?.permission_management_delete_success || "Permission deleted successfully"}`;
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
