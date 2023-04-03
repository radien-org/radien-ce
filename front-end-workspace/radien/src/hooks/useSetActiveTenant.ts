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
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: [QueryKeys.ACTIVE_TENANT], exact: false });
            await queryClient.invalidateQueries({ queryKey: [QueryKeys.TENANT_MANAGEMENT], exact: false });
        },
    });
}
