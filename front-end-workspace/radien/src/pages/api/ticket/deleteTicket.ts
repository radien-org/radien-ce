import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";

export default async function deleteTicket(req: NextApiRequest, res: NextApiResponse) {
    const session = await getServerSession(req, res, authOptions);
    const { ticketId } = req.query;
    if (!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_TICKET_URL}/ticket`;
    const result: AxiosResponse = await axios.delete(path, {
        params: {
            id: Number(ticketId),
        },
        headers: {
            Authorization: `Bearer ${session.accessToken}`,
        },
    });

    res.status(200).json(result.data);
};
