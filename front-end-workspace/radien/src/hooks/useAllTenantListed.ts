import axios from "axios";
import { useQuery } from "react-query";
import { Tenant} from "radien";
import {QueryKeys} from "@/consts";

const getListOfTenants = ()  => {
    return axios.get(`/api/tenant/tenant/find`);
}

export default function useAllTenantListed() {
    return useQuery<Tenant[], Error>(QueryKeys.COMPLETE_TENANT_LIST,
        async () => { const {data} = await getListOfTenants(); return data;},
        {refetchInterval: 300000});
}