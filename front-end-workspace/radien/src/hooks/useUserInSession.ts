import {useSession} from "next-auth/react";
import {useQuery} from "react-query";
import axios, {AxiosError, AxiosResponse} from "axios";
import {User} from "radien";
import {QueryKeys} from "@/consts";

const getUser = () => {
    return axios.get<User>("/api/user/getUserInSession");
}

export const useUserInSession = () => {
    const { status } = useSession();

    const query = useQuery<AxiosResponse<User>, AxiosError>(QueryKeys.ME, () => getUser(), {
        enabled: status !== 'loading' && status !== "unauthenticated",
        onError: (error) => {
            console.error('me query error: ', error.response);
        },
    });

    return { userInSession: query.data, isLoadingUserInSession: query.isLoading, isLoadingUserInSessionError: query.isError };
};