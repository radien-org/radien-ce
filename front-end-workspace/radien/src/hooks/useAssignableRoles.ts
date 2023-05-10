import axios from "axios";
import { useQuery } from "react-query";
import { QueryKeys } from "@/consts";
import { TenantRoleMapping } from "@/pages/api/role/tenantRole/getTenantRolesByTenantId";

const getTenantRolesByTenantId = (tenantId?: number) => {
    return axios.get(`/api/role/tenantRole/getTenantRolesByTenantId?tenantId=${tenantId}`);
};

export default function useAssignableRoles(tenantId: number) {
    return useQuery<TenantRoleMapping[], Error>(
        [QueryKeys.ASSIGNABLE_ROLES, tenantId],
        async () => {
            const { data } = await getTenantRolesByTenantId(tenantId);
            return data;
        },
        { refetchInterval: 300000, enabled: !!tenantId }
    );
}
