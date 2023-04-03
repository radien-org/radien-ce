import axios from "axios";
import { useQuery } from "react-query";
import { Page, Tenant } from "radien";
import { QueryKeys } from "@/consts";

interface TenantParams {
    pageNo?: number;
    pageSize?: number;
}

const findTenant = (tenantId: number, tenants: Tenant[]) => {
    return tenants.find((tenant: any) => tenant.id === tenantId);
};
const loadTenants = async () => {
    return await axios.get<Tenant[]>("/api/tenant/tenant/find");
};
const aggregateTenant = (tenants: Tenant[], data: Tenant[]) => {
    return data.map((tenant: any) => ({
        ...tenant,
        parentData: findTenant(tenant.parentId, tenants),
        clientData: findTenant(tenant.clientId, tenants),
    }));
};

export const getTenantPage = async (pageNumber: number = 1, pageSize: number = 10) => {
    return await axios.get<Page<Tenant>>("/api/tenant/tenant/getAll", {
        params: {
            page: pageNumber,
            pageSize: pageSize,
        },
    });
};

export default function usePaginatedTenants({ pageNo, pageSize }: TenantParams) {
    return useQuery<Page<Tenant>, Error>(
        [QueryKeys.TENANT_MANAGEMENT, pageNo, pageSize],
        async () => {
            const { data } = await getTenantPage(pageNo, pageSize);
            const { data: tenantData } = await loadTenants();
            return { ...data, results: aggregateTenant(tenantData, data.results) };
        },
        { refetchInterval: 300000 }
    );
}
