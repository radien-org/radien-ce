import axios from "axios";
import { useQuery } from "react-query";
import {ActiveTenant} from "radien";

const getActiveTenant = (userId: number)  => {
    return axios.get(`/api/tenant/activeTenant/getActiveTenant?userId=${userId}`);
}

export default function useActiveTenant(userId: number) {
    return useQuery<ActiveTenant, Error>("activeTenant", async () => { const {data} = await getActiveTenant(userId); return data ? data[0] : undefined;}, { refetchInterval: 10000, cacheTime: undefined});
}