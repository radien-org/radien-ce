import { useMutation } from "react-query";
import axios from "axios";
import { DeleteParams } from "@/components/PaginatedTable/PaginatedTable";
export default function useDeleteUser() {
    return useMutation({
        mutationFn: ({ objectId, userId }: DeleteParams) => axios.delete(`/api/user/deleteUser/${userId}`),
    });
}
