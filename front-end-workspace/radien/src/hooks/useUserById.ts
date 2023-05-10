import axios, { AxiosResponse } from "axios";
import { useQuery } from "react-query";
import { ActiveTenant, Page, Role, Tenant, TenantRole, User } from "radien";
import { QueryKeys } from "@/consts";


const getUserById = async (userId: number): Promise<AxiosResponse<Page<User>, any>> => {
    return await axios.get("/api/user/getById", {
        params: {
            id: userId
        }
    });
};

export default function useUserById(userId: number) {
    return useQuery<Page<User>, Error>(
        [QueryKeys.USER_MANAGEMENT, userId],
        async () => {
            const { data } = await getUserById(userId);
            return data;
        },
        { refetchInterval: 300000 }
    );
}
