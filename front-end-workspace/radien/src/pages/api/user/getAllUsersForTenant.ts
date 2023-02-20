import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";
import { Page } from "radien";

const getTenantUserIds = async (tenantId: number, pageNo: number, pageSize: number, accessToken: string) => {
    const path = `${process.env.RADIEN_ROLE_URL}/tenantroleuser/userIds`;
    return await axios.get<Page<number>>(path, {
        params: {
            tenantId,
            pageNo,
            pageSize,
        },
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    });
};

export default async function getAll(req: NextApiRequest, res: NextApiResponse) {
    const session = await getServerSession(req, res, authOptions);
    const { tenantId, page, pageSize } = req.query;
    if (!session) {
        res.status(401).json({});
        return;
    }

    const { data } = await getTenantUserIds(Number(tenantId), Number(page), Number(pageSize), session.accessToken!);

    const path = `${process.env.RADIEN_USER_URL}/user`;
    const result: AxiosResponse = await axios.get(path, {
        params: {
            ids: data.results,
            pageNo: page,
            pageSize,
        },
        paramsSerializer: { indexes: null },
        headers: {
            Authorization: `Bearer ${session.accessToken}`,
        },
    });

    res.status(200).json(result.data);
}
