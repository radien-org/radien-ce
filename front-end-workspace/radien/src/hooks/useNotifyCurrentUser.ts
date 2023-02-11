import { useMutation } from "react-query";
import axios from "axios";

interface NotificationArgs {
    params: any;
    viewId: string;
    language?: string;
}

export default function useNotifyCurrentUser() {
    return useMutation({
        mutationFn: ({ params, viewId, language }: NotificationArgs) =>
            axios.post(`/api/notification/notifyCurrentUser`, params, { params: { viewId, language } }),
    });
}
