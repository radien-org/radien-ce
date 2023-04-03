import axios from "axios";
import { useQuery } from "react-query";
import { Page, Tenant } from "radien";
import { QueryKeys } from "@/consts";

interface TenantParams {
    parentId?: number;
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

export const getTenantPage = async (pageNumber: number = 1, pageSize: number = 10, parentId?: number) => {
    return await axios.get<Page<Tenant>>("/api/tenant/tenant/getAll", {
        params: {
            parentId,
            page: pageNumber,
            pageSize: pageSize,
        },
    });
};

export default function usePaginatedTenants({ parentId, pageNo, pageSize }: TenantParams) {
    return useQuery<Page<Tenant>, Error>(
        [QueryKeys.TENANT_MANAGEMENT, parentId, pageNo, pageSize],
        async () => {
            const { data } = await getTenantPage(pageNo, pageSize, parentId);
            const { data: tenantData } = await loadTenants();
            return { ...data, results: aggregateTenant(tenantData, data.results) };
        },
        { refetchInterval: 300000 }
    );
}
