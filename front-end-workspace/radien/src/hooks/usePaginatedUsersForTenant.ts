import axios, { AxiosResponse } from "axios";
import { useQuery } from "react-query";
import { ActiveTenant, Page, Role, Tenant, TenantRole, User } from "radien";
import { QueryKeys } from "@/consts";

interface UsersForTenantParams {
    tenantId: number;
    pageNo?: number;
    pageSize?: number;
}

export const getUsersForTenantPage = async (tenantId: number, pageNumber: number = 1, pageSize: number = 10): Promise<AxiosResponse<Page<User>, any>> => {
    return await axios.get("/api/user/getAllUsersForTenant", {
        params: {
            tenantId,
            page: pageNumber,
            pageSize: pageSize,
        },
    });
};

export default function usePaginatedUsersForTenant({ tenantId, pageNo, pageSize }: UsersForTenantParams) {
    return useQuery<Page<User>, Error>(
        [QueryKeys.USER_MANAGEMENT, tenantId, pageNo, pageSize],
        async () => {
            const { data } = await getUsersForTenantPage(tenantId, pageNo, pageSize);
            return data;
        },
        { refetchInterval: 300000, enabled: !!tenantId }
    );
}
