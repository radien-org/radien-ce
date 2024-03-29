import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";
import { Ticket } from "radien";

export default async function createTicket(req: NextApiRequest, res: NextApiResponse) {
    const session = await getServerSession(req, res, authOptions);
    const ticket: Ticket = req.body;
    if (!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_TICKET_URL}/ticket`;
    const result: AxiosResponse = await axios.post(path, ticket, {
        headers: {
            Authorization: `Bearer ${session.accessToken}`,
        },
    });

    res.status(200).json(result.data);
}
