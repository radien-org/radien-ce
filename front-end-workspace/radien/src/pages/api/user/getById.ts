import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";

export default async function getById(req: NextApiRequest, res: NextApiResponse) {
    const session = await getServerSession(req, res, authOptions);
    const { id } = req.query;
    if (!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_USER_URL}/user/${id}`;
    const result: AxiosResponse = await axios.get(path, {
        headers: {
            Authorization: `Bearer ${session.accessToken}`,
        },
    });

    res.status(200).json(result.data);
}
