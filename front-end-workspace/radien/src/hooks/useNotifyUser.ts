import { useMutation } from "react-query";
import axios from "axios";

interface NotificationArgs {
    email: string | undefined;
    params: any;
    viewId: string;
    language?: string;
}

export default function useNotifyUser() {
    return useMutation({
        mutationFn: ({ email, params, viewId, language }: NotificationArgs) =>
            axios.post(`/api/notification/notifyUser`, params, { params: { email, viewId, language } }),
    });
}
