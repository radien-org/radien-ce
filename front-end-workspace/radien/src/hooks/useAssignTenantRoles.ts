import { useMutation } from "react-query";
import axios from "axios";

interface TenantRoleUserAssignmentProps {
    tenantRoleId: number;
    userId: number;
}

export default function useAssignTenantRoles() {
    return useMutation({
        mutationFn: ({ tenantRoleId, userId }: TenantRoleUserAssignmentProps) =>
            axios.post(`/api/role/tenantroleuser/associateTenantRole`, {}, { params: { tenantRoleId, userId } }),
    });
}
