import {useMutation, useQueryClient} from "react-query";
import {User} from "radien";
import axios from "axios";
import {QueryKeys} from "@/consts";


export interface DissociateTenantMutationParams {
    tenantId: number,
    userId: number
}
export default function useDissociateTenant() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({tenantId, userId}: DissociateTenantMutationParams) => axios.delete(`/api/role/tenantroleuser/dissociateTenant`, { params: {tenantId, userId} }),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: QueryKeys.AVAILABLE_TENANTS})
            queryClient.invalidateQueries({queryKey: QueryKeys.ACTIVE_TENANT})
        }
    });
}