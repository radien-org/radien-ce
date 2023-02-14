import { useMutation, useQueryClient } from "react-query";
import axios from "axios";
import { QueryKeys } from "@/consts";

interface SetActiveTenantProps {
    userId: number;
    tenantId: number;
    activeTenantId?: number;
}

export default function useSetActiveTenant() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ userId, tenantId, activeTenantId }: SetActiveTenantProps) =>
            axios.post(`/api/tenant/activeTenant/setActiveTenant`, {}, { params: { userId, tenantId, activeTenantId } }),
        onSuccess: () => queryClient.invalidateQueries(QueryKeys.ACTIVE_TENANT),
    });
}
