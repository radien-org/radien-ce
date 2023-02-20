import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";

export default async function updateTenant(req: NextApiRequest, res: NextApiResponse) {
    const { tenantId } = req.query;
    const session = await getServerSession(req, res, authOptions);
    if (!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_TENANT_URL}/tenant/${tenantId}`;
    try {
        const result: AxiosResponse = await axios.put(path, req.body, {
            headers: {
                Authorization: `Bearer ${session.accessToken}`,
            },
        });
        res.status(200).json(result.data);
    } catch (e) {
        res.status(500).json(e);
    }
}
