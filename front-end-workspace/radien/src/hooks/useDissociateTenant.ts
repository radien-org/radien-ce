import { useMutation, useQueryClient } from "react-query";
import axios, {AxiosError} from "axios";
import { QueryKeys } from "@/consts";
import { DeleteParams } from "@/components/PaginatedTable/PaginatedTable";
import {useContext} from "react";
import {RadienContext} from "@/context/RadienContextProvider";

export default function useDissociateTenant() {
    const { addSuccessMessage, addErrorMessage, i18n } = useContext(RadienContext);
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ objectId, userId }: DeleteParams) =>
            axios.delete(`/api/role/tenantroleuser/dissociateTenant`, { params: { tenantId: objectId, userId } }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: QueryKeys.AVAILABLE_TENANTS });
            queryClient.invalidateQueries({ queryKey: QueryKeys.ASSIGNED_TENANTS });
            queryClient.invalidateQueries({ queryKey: QueryKeys.ACTIVE_TENANT });
            let message = `${i18n?.generic_message_success || "Success"}: ${i18n?.upate_tenant_success || "User and tenant successfully dissociated"}`;
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
