import axios from "axios";
import { useQuery } from "react-query";
import { Page, Tenant } from "radien";
import { QueryKeys } from "@/consts";

const getAssignedTenants = (userId?: number) => {
    return axios.get(`/api/role/tenantroleuser/getTenants?userId=${userId}`);
};

export default function useAssignedTenants(userId?: number) {
    return useQuery<Page<Tenant>, Error>(
        QueryKeys.ASSIGNED_TENANTS,
        async () => {
            const { data } = await getAssignedTenants(userId);
            return data;
        },
        { refetchInterval: 300000, enabled: !!userId }
    );
}
