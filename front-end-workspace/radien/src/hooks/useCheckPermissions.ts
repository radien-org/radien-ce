import axios from "axios";
import { useQuery } from "react-query";

const checkPermission = (resource: string, action: string, userId?: number, tenantId?: number) => {
    const path = `/api/role/tenantrolepermission/hasPermission?userId=${userId}&resource=${resource}&action=${action}&tenantId=${tenantId}`;
    return axios.get(path);
};

export default function useCheckPermissions(userId?: number, tenantId?: number) {
    const permissions = {
        roles: { resource: "Roles", action: "Read" },
        tenantRoles: { resource: "Tenant Role", action: "Read" },
        user: { resource: "User", action: "Read" },
        permission: { resource: "Permission", action: "Read" },
        tenant: { resource: "Tenant", action: "Read" },
        isTenantAdmin: { resource: "Tenant", action: "Write" },
    };

    return {
        roles: useQuery(
            [permissions.roles.resource, tenantId],
            async () => (await checkPermission(permissions.roles.resource, permissions.roles.action, userId, tenantId)).data,
            { enabled: !!tenantId && !!userId, refetchInterval: 60000 }
        ),
        tenantRoles: useQuery(
            [permissions.tenantRoles.resource, tenantId],
            async () => (await checkPermission(permissions.tenantRoles.resource, permissions.tenantRoles.action, userId, tenantId)).data,
            { enabled: !!tenantId && !!userId, refetchInterval: 60000 }
        ),
        user: useQuery(
            [permissions.user.resource, tenantId],
            async () => (await checkPermission(permissions.user.resource, permissions.user.action, userId, tenantId)).data,
            { enabled: !!tenantId && !!userId, refetchInterval: 60000 }
        ),
        permission: useQuery(
            [permissions.permission.resource, tenantId],
            async () => (await checkPermission(permissions.permission.resource, permissions.permission.action, userId, tenantId)).data,
            { enabled: !!tenantId && !!userId, refetchInterval: 60000 }
        ),
        tenant: useQuery(
            [permissions.tenant.resource, tenantId],
            async () => (await checkPermission(permissions.tenant.resource, permissions.tenant.action, userId, tenantId)).data,
            { enabled: !!tenantId && !!userId, refetchInterval: 60000 }
        ),
        tenantAdmin: useQuery(
            [permissions.isTenantAdmin.resource, tenantId],
            async () => (await checkPermission(permissions.isTenantAdmin.resource, permissions.isTenantAdmin.action, userId, tenantId)).data,
            { enabled: !!tenantId && !!userId, refetchInterval: 60000 }
        ),
    };
}
