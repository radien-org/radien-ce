import axios, { AxiosResponse } from "axios";
import { useQuery } from "react-query";
import { ActiveTenant, Page, Role, Tenant, TenantRole } from "radien";
import { QueryKeys } from "@/consts";

interface TenantRoleParams {
    pageNo?: number;
    pageSize?: number;
}

const loadTenants = async () => {
    return await axios.get<Page<Tenant>>("/api/tenant/tenant/getAll");
};

const loadRoles = async () => {
    return await axios.get<Page<Role>>("/api/role/role/getAll");
};

const aggregateTenants = (tenants: any, data: TenantRole[]): TenantRole[] => {
    return data.map((tenantRole: any) => ({
        ...tenantRole,
        tenant: tenants.results.find((tenant: any) => tenant.id === tenantRole.tenantId),
    }));
};

const aggregateRoles = (roles: any, data: TenantRole[]): TenantRole[] => {
    return data.map((tenantRole: any) => ({
        ...tenantRole,
        role: roles.results.find((role: any) => role.id === tenantRole.roleId),
    }));
};

const getTenantRolesPage = async (pageNumber: number = 1, pageSize: number = 10): Promise<AxiosResponse<Page<TenantRole>, any>> => {
    return await axios.get<Page<TenantRole>>("/api/role/tenantRole/getAll", {
        params: {
            page: pageNumber,
            pageSize: pageSize,
        },
    });
};

export default function usePaginatedTenantRoles({ pageNo, pageSize }: TenantRoleParams) {
    return useQuery<Page<TenantRole>, Error>(
        [QueryKeys.TENANT_ROLE_MANAGEMENT, pageNo, pageSize],
        async () => {
            const { data } = await getTenantRolesPage(pageNo, pageSize);
            const { data: rolesData } = await loadRoles();
            const { data: tenantsData } = await loadTenants();
            return { ...data, results: aggregateRoles(rolesData, aggregateTenants(tenantsData, data.results)) };
        },
        { refetchInterval: 300000 }
    );
}
