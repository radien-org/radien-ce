import { useMutation, useQueryClient } from "react-query";
import { Role } from "radien";
import axios from "axios";
import { QueryKeys } from "@/consts";

export default function useUpdateRole() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (updatedRole: Role) => axios.post(`/api/role/role/updateRole`, updatedRole),
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: [QueryKeys.ROLE_MANAGEMENT], exact: false });
        },
    });
}
