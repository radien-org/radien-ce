import axios from "axios";
import { useQuery } from "react-query";
import { Action, Page, Permission, Resource } from "radien";
import { QueryKeys, TicketType } from "@/consts";
import { TenantRequestsResult } from "@/pages/api/ticket/getTenantRequests";

interface TenantRequestParams {
    tenantId: number;
    pageNo?: number;
    pageSize?: number;
    setRequestCount: (value: number) => void;
}

export const getTenantRequestsPage = async (data: number, pageNumber: number = 1, pageSize: number = 10) => {
    return await axios.get<Page<TenantRequestsResult>>("/api/ticket/getTenantRequests", {
        params: {
            ticketType: TicketType.TENANT_REQUEST,
            data: data,
            page: pageNumber,
            pageSize: pageSize,
        },
    });
};

export default function usePaginatedTenantRequests({ tenantId, pageNo, pageSize, setRequestCount }: TenantRequestParams) {
    return useQuery<Page<TenantRequestsResult>, Error>(
        [QueryKeys.PERMISSION_MANAGEMENT, pageNo, pageSize],
        async () => {
            const { data } = await getTenantRequestsPage(tenantId, pageNo, pageSize);
            return data;
        },
        { refetchInterval: 300000, enabled: !!tenantId, onSuccess: (data) => setRequestCount(data.totalResults) }
    );
}
