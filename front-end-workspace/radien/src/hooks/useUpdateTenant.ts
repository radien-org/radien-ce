import { useMutation, useQueryClient } from "react-query";
import {Tenant, User} from "radien";
import axios, { AxiosError } from "axios";
import { QueryKeys } from "@/consts";
import { useContext } from "react";
import { RadienContext } from "@/context/RadienContextProvider";

export default function useUpdateTenant(tenantId: number) {
    const { addSuccessMessage, addErrorMessage, i18n } = useContext(RadienContext);
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (newTenantInfo: any) => axios.put(`/api/tenant/tenant/updateTenant?tenantId=${tenantId}`, newTenantInfo),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: QueryKeys.TENANT_MANAGEMENT });
            let message = `${i18n?.generic_message_success || "Success"}: ${i18n?.upate_tenant_success || "Tenant updated successfully"}`;
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
