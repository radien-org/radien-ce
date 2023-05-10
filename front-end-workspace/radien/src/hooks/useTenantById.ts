import axios from "axios";
import { useQuery } from "react-query";
import { QueryKeys } from "@/consts";
import { Tenant } from "radien";

const loadTenant = (tenantId?: number) => {
    return axios.get(`/api/tenant/tenant/getById?id=${tenantId}`);
};

export default function useTenantById(tenantId: number) {
    return useQuery<Tenant, Error>(
        [QueryKeys.TENANT_INFO, tenantId],
        async () => {
            const { data } = await loadTenant(tenantId);
            return data;
        },
        { refetchInterval: 300000, enabled: !!tenantId }
    );
}
