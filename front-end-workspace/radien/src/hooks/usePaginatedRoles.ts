import axios, { AxiosResponse } from "axios";
import { useQuery } from "react-query";
import { ActiveTenant, Page, Role, Tenant, TenantRole } from "radien";
import { QueryKeys } from "@/consts";

interface RolesParams {
    pageNo?: number;
    pageSize?: number;
}

const getRolesPage = async (pageNumber: number = 1, pageSize: number = 10) => {
    return await axios.get<Page<Role>>("/api/role/role/getAll", {
        params: {
            page: pageNumber,
            pageSize: pageSize,
        },
    });
};

export default function usePaginatedRoles({ pageNo, pageSize }: RolesParams) {
    return useQuery<Page<Role>, Error>(
        [QueryKeys.ROLE_MANAGEMENT, pageNo, pageSize],
        async () => {
            const { data } = await getRolesPage(pageNo, pageSize);
            return data;
        },
        { refetchInterval: 300000 }
    );
}
