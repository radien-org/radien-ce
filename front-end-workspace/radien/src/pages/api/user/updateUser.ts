import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";
import { User } from "radien";

export default async function updateUser(req: NextApiRequest, res: NextApiResponse) {
    const session = await getServerSession(req, res, authOptions);
    const radUser: User = req.body;
    if (!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_USER_URL}/user/${radUser.id}`;
    const result: AxiosResponse = await axios.put(path, radUser, {
        headers: {
            Authorization: `Bearer ${session.accessToken}`,
        },
    });

    res.status(200).json(result.data);
}
