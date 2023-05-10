import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosError, AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";
import { Ticket, User } from "radien";
import { TicketType } from "@/consts";

export default async function confirmEmailChange(req: NextApiRequest, res: NextApiResponse) {
    const session = await getServerSession(req, res, authOptions);
    const { ticketToken } = req.query;
    if (!session || !ticketToken) {
        res.status(401).json({});
        return;
    }

    const ticketResponse = await getTicketByToken(String(session.accessToken), String(ticketToken)).catch((ex) => console.log(ex));
    console.log(ticketResponse);
    if (!ticketResponse || !ticketResponse.data || ticketResponse.data.ticketType != TicketType.EMAIL_CHANGE) {
        res.status(400).json({ message: "This link may have already been used and is invalid." });
        return;
    }

    const { data: ticketData } = ticketResponse;
    const newEmail = JSON.parse(ticketData.data).newEmail;
    const { data: userData } = await getUserById(String(session.accessToken), ticketData.userId);
    userData.userEmail = newEmail;
    await updateUser(String(session.accessToken), userData);
    await deleteTicket(String(session.accessToken), ticketData.id!);

    //redirect to confirmation page
    res.writeHead(302, { Location: `/user/confirmationPage` });
    res.end();
}

const getTicketByToken = (accessToken: string, token: string): Promise<AxiosResponse<Ticket, AxiosError>> => {
    const path = `${process.env.RADIEN_TICKET_URL}/ticket/token`;
    return axios.get(path, {
        params: {
            token,
        },
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    });
};

const deleteTicket = (accessToken: string, id: number) => {
    const path = `${process.env.RADIEN_TICKET_URL}/ticket`;
    return axios.delete(path, {
        params: {
            id,
        },
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    });
};

const updateUser = (accessToken: string, user: User) => {
    const path = `${process.env.RADIEN_USER_URL}/user/${user.id}`;
    return axios.put(path, user, {
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    });
};

const getUserById = (accessToken: string, userId: number): Promise<AxiosResponse<User, AxiosError>> => {
    const path = `${process.env.RADIEN_USER_URL}/user/${userId}`;
    return axios.get(path, {
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    });
};
