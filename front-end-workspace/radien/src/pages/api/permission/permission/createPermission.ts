import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";

export default async function createPermission(req: NextApiRequest, res: NextApiResponse) {
    const session = await getServerSession(req, res, authOptions);
    if (!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_PERMISSION_URL}/permission`;
    try {
        const result: AxiosResponse = await axios.post(path, req.body, {
            headers: {
                Authorization: `Bearer ${session.accessToken}`,
            },
        });
        res.status(201).json(result.data);
    } catch (e) {
        res.status(500).json(e);
    }
}
