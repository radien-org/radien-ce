import axios from "axios";
import { useQuery } from "react-query";
import { Action, Page, Permission, Resource } from "radien";
import { QueryKeys } from "@/consts";

interface PermissionParams {
    pageNo?: number;
    pageSize?: number;
}

const loadActions = async () => {
    return await axios.get<Action[]>("/api/action/find");
};

const loadResources = async () => {
    return await axios.get<Resource[]>("/api/resource/find");
};

const aggregateActions = (actions: Action[], data: Permission[]): Permission[] => {
    return data.map((permission: any) => ({
        ...permission,
        action: actions.find((action: any) => action.id === permission.actionId),
    }));
};
const aggregateResources = (resources: Resource[], data: Permission[]) => {
    return data.map((permission: any) => ({
        ...permission,
        resource: resources.find((action: any) => action.id === permission.actionId),
    }));
};

export const getPermissionPage = async (pageNumber: number = 1, pageSize: number = 10) => {
    return await axios.get<Page<Permission>>("/api/permission/permission/getAll", {
        params: {
            page: pageNumber,
            pageSize: pageSize,
        },
    });
};

export default function usePaginatedPermissions({ pageNo, pageSize }: PermissionParams) {
    return useQuery<Page<Permission>, Error>(
        [QueryKeys.PERMISSION_MANAGEMENT, pageNo, pageSize],
        async () => {
            const { data } = await getPermissionPage(pageNo, pageSize);
            const { data: actionsData } = await loadActions();
            const { data: resourcesData } = await loadResources();
            return { ...data, results: aggregateActions(actionsData, aggregateResources(resourcesData, data.results)) };
        },
        { refetchInterval: 300000 }
    );
}
