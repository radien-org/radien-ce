import { useMutation, useQueryClient } from "react-query";
import { UserRequest } from "radien";
import axios from "axios";
import { QueryKeys } from "@/consts";

export default function useUpdateUser() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (updatedUser: UserRequest) => axios.post(`/api/user/updateUser`, updatedUser),
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: [QueryKeys.ME], exact: false });
            await queryClient.invalidateQueries({ queryKey: [QueryKeys.USER_MANAGEMENT], exact: false });
        },
    });
}
