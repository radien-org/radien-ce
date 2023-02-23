import { useMutation, useQueryClient } from "react-query";
import axios from "axios";
import { DeleteParams } from "@/components/PaginatedTable/PaginatedTable";
import { QueryKeys } from "@/consts";
export default function useDeleteUser() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ objectId, userId }: DeleteParams) => axios.delete(`/api/user/delete/${objectId}`),
        onSuccess: () => queryClient.invalidateQueries({ queryKey: QueryKeys.USER_MANAGEMENT, exact: false }),
    });
}
