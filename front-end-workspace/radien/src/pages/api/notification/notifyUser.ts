import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";

export default async function notifyUser(req: NextApiRequest, res: NextApiResponse) {
    const session = await getServerSession(req, res, authOptions);
    const { email, viewId, language } = req.query;
    const args = req.body;
    if (!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_NOTIFICATION_URL}/email`;
    const result: AxiosResponse = await axios.post(path, args, {
        params: {
            email,
            viewId,
            language,
        },
        headers: {
            Authorization: `Bearer ${session.accessToken}`,
        },
    });

    res.status(200).json(result.data);
}
