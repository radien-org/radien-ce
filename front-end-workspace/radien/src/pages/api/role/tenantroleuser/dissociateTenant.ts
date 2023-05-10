import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";

export default async function dissociateTenant(req: NextApiRequest, res: NextApiResponse) {
    const { userId, tenantId } = req.query;
    const session = await getServerSession(req, res, authOptions);
    if (!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_ROLE_URL}/tenantroleuser?userId=${userId}&tenantId=${tenantId}`;
    try {
        const result: AxiosResponse = await axios.delete(path, {
            headers: {
                Authorization: `Bearer ${session.accessToken}`,
            },
        });

        res.status(200).json(result.data);
    } catch (e) {
        res.status(500).json(e);
    }
}
