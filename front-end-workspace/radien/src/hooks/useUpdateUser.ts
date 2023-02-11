import { useMutation, useQueryClient } from "react-query";
import { User } from "radien";
import axios from "axios";
import { QueryKeys } from "@/consts";

export default function useUpdateUser() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (updatedUser: User) => axios.post(`/api/user/updateUser`, updatedUser),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: QueryKeys.ME });
        },
    });
}
