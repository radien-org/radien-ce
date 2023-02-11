import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";

export default async function deleteById(req: NextApiRequest, res: NextApiResponse) {
    const session = await getServerSession(req, res, authOptions);
    if (!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_PERMISSION_URL}/permission/${req.query.pid}`;
    try {
        const result: AxiosResponse = await axios.delete(path, {
            headers: {
                Authorization: `Bearer ${session.accessToken}`,
            },
        });
        res.status(204).json(result.data);
    } catch (e) {
        res.status(500).json(e);
    }
}
