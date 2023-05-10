import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";
import { Page, Ticket, User } from "radien";

export interface TenantRequestsResult {
    ticket: Ticket;
    user: User;
}

const getUserById = async (userId: number, accessToken: string) => {
    const path = `${process.env.RADIEN_USER_URL}/user/${userId}`;
    const result: AxiosResponse<User> = await axios.get(path, {
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    });

    return result.data;
};

export default async function getTenantRequests(req: NextApiRequest, res: NextApiResponse) {
    const session = await getServerSession(req, res, authOptions);
    const { pageNo, pageSize, ticketType, data } = req.query;
    if (!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_TICKET_URL}/ticket/find`;
    const result: AxiosResponse<Page<Ticket>> = await axios.get(path, {
        params: {
            ticketType,
            data,
            isLogicalConjunction: true,
            pageNo,
            pageSize,
        },
        headers: {
            Authorization: `Bearer ${session.accessToken}`,
        },
    });

    let resultsMap: Page<TenantRequestsResult> | undefined;
    if (result.data) {
        resultsMap = {
            totalPages: result.data.totalPages,
            totalResults: result.data.totalResults,
            currentPage: result.data.currentPage,
            results: [],
        };
        for (const ticket of result.data.results) {
            const user = await getUserById(ticket.userId, session.accessToken!);
            resultsMap.results.push({ ticket, user });
        }
    }

    res.status(200).json(resultsMap);
}
