import axios from "axios";
import { useQuery } from "react-query";
import {Tenant} from "radien";

const getAvailableTenants = (userId: number)  => {
    return axios.get(`/api/role/tenantroleuser/getTenants?userId=${userId}`);
}

export default function useAvailableTenants(userId: number) {
    return useQuery<Tenant[], Error>("availableTenants", async () => { const {data} = await getAvailableTenants(userId); return data;}, { refetchInterval: 20000});
}