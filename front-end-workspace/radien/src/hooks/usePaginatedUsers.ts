import axios, { AxiosResponse } from "axios";
import { useQuery } from "react-query";
import { ActiveTenant, Page, Role, Tenant, TenantRole, User } from "radien";
import { QueryKeys } from "@/consts";

interface UsersParams {
    pageNo?: number;
    pageSize?: number;
}

const getUsersPage = async (pageNumber: number = 1, pageSize: number = 10): Promise<AxiosResponse<Page<User>, any>> => {
    return await axios.get("/api/user/getAll", {
        params: {
            page: pageNumber,
            pageSize: pageSize,
        },
    });
};

export default function usePaginatedUsers({ pageNo, pageSize }: UsersParams) {
    return useQuery<Page<User>, Error>(
        [QueryKeys.USER_MANAGEMENT, pageNo, pageSize],
        async () => {
            const { data } = await getUsersPage(pageNo, pageSize);
            return data;
        },
        { refetchInterval: 300000 }
    );
}
