import { useMutation } from "react-query";
import { Ticket } from "radien";
import axios from "axios";

export default function useCreateTicket() {
    return useMutation({
        mutationFn: (newTicket: Ticket) => axios.post(`/api/ticket/createTicket`, newTicket),
    });
}
