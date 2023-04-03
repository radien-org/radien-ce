import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";

export default async function updateRole(req: NextApiRequest, res: NextApiResponse) {
    const session = await getServerSession(req, res, authOptions);
    const roleId = req.body.id;
    if (!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_ROLE_URL}/role/${roleId}`;
    try {
        const result: AxiosResponse = await axios.put(path, req.body, {
            headers: {
                Authorization: `Bearer ${session.accessToken}`,
            },
        });
        res.status(201).json(result.data);
    } catch (e) {
        res.status(500).json(e);
    }
}
