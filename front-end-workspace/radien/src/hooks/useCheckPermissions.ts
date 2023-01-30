import axios from "axios";
import { useQuery } from "react-query";

const checkPermission = (userId: number, resource: string, action: string, tenantId: number) => {
    const path = `/api/role/tenantrolepermission/hasPermission?userId=${userId}&resource=${resource}&action=${action}&tenantId=${tenantId}`;
    return axios.get(path);
}

export default function useCheckPermissions(userId: number, tenantId: number) {
    const permissions = {
        roles: {resource: "Roles", action: "Read"},
        user: {resource: "User", action: "Read"},
        permission: {resource: "Permission", action: "Read"},
        tenant: {resource: "Tenant", action: "Read"},
    }

    return [
        useQuery([permissions.roles.resource, tenantId], async () => (await checkPermission(userId, permissions.roles.resource, permissions.roles.action, tenantId)).data, { enabled: !!tenantId, refetchInterval: 60000}),
        useQuery([permissions.user.resource, tenantId], async () => (await checkPermission(userId, permissions.user.resource, permissions.user.action, tenantId)).data, { enabled: !!tenantId, refetchInterval: 60000}),
        useQuery([permissions.permission.resource, tenantId], async () => (await checkPermission(userId, permissions.permission.resource, permissions.permission.action, tenantId)).data, { enabled: !!tenantId, refetchInterval: 60000}),
        useQuery([permissions.tenant.resource, tenantId], async () => (await checkPermission(userId, permissions.tenant.resource, permissions.tenant.action, tenantId)).data, { enabled: !!tenantId, refetchInterval: 60000})
    ]
}