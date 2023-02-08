import {useMutation} from "react-query";
import axios from "axios";

interface NotificationArgs {
    tenantId: number,
    roles: string[],
    params: any,
    viewId: string,
    language?: string
}

export default function useNotifyTenantRoles() {
    return useMutation({
        mutationFn: ({tenantId, roles, params, viewId, language}: NotificationArgs) => {
            return axios.post(`/api/notification/notifyTenantAdmins`, {
                    args: params, roles
                },
                {
                    params: {tenantId, viewId, language}
                })
        }
    });
}