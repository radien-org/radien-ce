import {useMutation} from "react-query";
import axios, {AxiosError} from "axios";
import {useContext} from "react";
import {RadienContext} from "@/context/RadienContextProvider";

interface NotificationArgs {
    tenantId: number,
    roles: string[],
    params: any,
    viewId: string,
    language?: string
}

export default function useNotifyTenantRoles() {
    const { addSuccessMessage, addErrorMessage } = useContext(RadienContext);
    return useMutation({
        mutationFn: ({tenantId, roles, params, viewId, language}: NotificationArgs) =>
            axios.post(`/api/notification/notifyTenantAdmins`, {
                args: params, roles
                },
                {
                    params: {tenantId, viewId, language}
            }),
        onError: (e: AxiosError) => {addErrorMessage((e.response?.data as {error: string}).error)}
    });
}