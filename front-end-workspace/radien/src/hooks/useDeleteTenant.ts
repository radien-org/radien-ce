import { useMutation, useQueryClient } from "react-query";
import axios, { AxiosError } from "axios";
import { DeleteParams } from "@/components/PaginatedTable/PaginatedTable";
import { useContext } from "react";
import { RadienContext } from "@/context/RadienContextProvider";
import { QueryKeys } from "@/consts";
export default function useDeleteTenant() {
    const { addSuccessMessage, addErrorMessage, i18n } = useContext(RadienContext);
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ tenantId, userId }: DeleteParams) => axios.delete(`/api/tenant/delete/${tenantId}`),
        onSuccess: () => {
            queryClient.invalidateQueries(QueryKeys.TENANT_MANAGEMENT);
            queryClient.invalidateQueries(QueryKeys.AVAILABLE_TENANTS);
            queryClient.invalidateQueries(QueryKeys.ACTIVE_TENANT);

            let message = `${i18n?.generic_message_success || "Success"}: ${i18n?.tenant_management_delete_success || "Tenant deleted successfully"}`;
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
