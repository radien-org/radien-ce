import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";

export default async (req: NextApiRequest, res: NextApiResponse) => {
    const { tenantRoleId, userId } = req.query;
    const session = await getServerSession(req, res, authOptions);

    //TODO: Prevent any user from just calling this endpoint
    if (!session) {
        res.status(401).json({});

        return;
    }

    const path = `${process.env.RADIEN_ROLE_URL}/tenantroleuser`;
    console.log(path);
    try {
        await axios.post(
            path,
            {
                tenantRoleId: Number(tenantRoleId),
                userId: Number(userId),
            },
            {
                headers: {
                    Authorization: `Bearer ${session.accessToken}`,
                },
            }
        );

        res.status(200).json({});
    } catch (e) {
        res.status(500).json(e);
    }
};
