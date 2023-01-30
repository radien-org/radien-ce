import axios from "axios";
import { useQuery } from "react-query";
import {Page, Tenant} from "radien";
import {QueryKeys} from "@/consts";

const getAvailableTenants = (userId?: number)  => {
    return axios.get(`/api/role/tenantroleuser/getTenants?userId=${userId}`);
}

export default function useAvailableTenants(userId?: number) {
    return useQuery<Page<Tenant>, Error>(QueryKeys.AVAILABLE_TENANTS, async () => { const {data} = await getAvailableTenants(userId); return data;},
        { refetchInterval: 20000, enabled: !!userId});
}