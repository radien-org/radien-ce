import {useMutation, useQueryClient} from "react-query";
import axios from "axios";
import {QueryKeys} from "@/consts";

interface ProcessingLockUserProps{
    id: number,
    lock: boolean;
}

export default function useProcessingLockUser(){
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, lock} : ProcessingLockUserProps) => axios.post(`/api/user/lockUserProcessing`, {id, lock}),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: QueryKeys.ME });
        }
    });
}