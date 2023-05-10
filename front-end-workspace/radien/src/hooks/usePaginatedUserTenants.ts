import axios from "axios";
import { useQuery } from "react-query";
import { Page, Tenant } from "radien";
import { QueryKeys } from "@/consts";

interface UserTenantsParams {
    userId: number;
}

export const getUserTenantPage = async (userId: number) => {
    return await axios.get<Page<Tenant>>("/api/role/tenantroleuser/getTenants", {
        params: {
            userId,
        },
    });
};

export default function usePaginatedUserTenants({ userId }: UserTenantsParams) {
    return useQuery<Page<Tenant>, Error>(
        QueryKeys.ASSIGNED_TENANTS,
        async () => {
            const { data } = await getUserTenantPage(userId);
            return data;
        },
        { refetchInterval: 300000, enabled: !!userId }
    );
}
