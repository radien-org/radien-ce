import axios from "axios";
import { useQuery } from "react-query";
import { QueryKeys } from "@/consts";
import { Page, Tenant } from "radien";

const getChildrenByTenantId = async (tenantId?: number) => {
    return await axios.get("/api/tenant/tenant/getChildren", {
        params: {
            id: tenantId,
        },
    });
};

export default function useTenantChildren(tenantId: number) {
    return useQuery<Page<Tenant>, Error>(
        [QueryKeys.TENANT_MANAGEMENT_CHILDREN, tenantId],
        async () => {
            const { data } = await getChildrenByTenantId(tenantId);
            return { ...data, results: data };
        },
        { refetchInterval: 300000, enabled: !!tenantId }
    );
}
