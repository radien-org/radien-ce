import {useMutation} from "react-query";
import axios from "axios";
import {DeleteParams} from "@/components/PaginatedTable/PaginatedTable";
export default function useDeleteUser() {
    return useMutation({
        mutationFn: ({tenantId, userId}: DeleteParams) => axios.delete(`/api/user/deleteUser/${userId}`),
    });
}