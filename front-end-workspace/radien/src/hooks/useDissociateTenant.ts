import { useMutation, useQueryClient } from "react-query";
import axios from "axios";
import { QueryKeys } from "@/consts";
import { DeleteParams } from "@/components/PaginatedTable/PaginatedTable";

export default function useDissociateTenant() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ tenantId, userId }: DeleteParams) => axios.delete(`/api/role/tenantroleuser/dissociateTenant`, { params: { tenantId, userId } }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: QueryKeys.AVAILABLE_TENANTS });
            queryClient.invalidateQueries({ queryKey: QueryKeys.ACTIVE_TENANT });
        },
    });
}
