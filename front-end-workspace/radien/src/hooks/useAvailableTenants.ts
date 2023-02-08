import axios from "axios";
import { useQuery } from "react-query";
import {  Tenant} from "radien";
import {QueryKeys} from "@/consts";
import {useContext} from "react";
import {RadienContext} from "@/context/RadienContextProvider";

const getListOfTenants = (userId?: number) => {
    return axios.get(`/api/tenant/tenant/findUnassignedTenants?userId=${userId}`);
};

export default function useAvailableTenants() {
    const { userInSession } = useContext(RadienContext)
    return useQuery<Tenant[], Error>(QueryKeys.AVAILABLE_TENANTS,
        async () => { const {data}  = await getListOfTenants(userInSession?.id);
            return data;
        },
        { refetchInterval: 300000, enabled: !!userInSession?.id }
    );
}
