import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosError, AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";
import { ActiveTenant } from "radien";

const deleteActiveTenant = async (userId: Number, tenantId: string, accessToken: string) => {
    try {
        let path = `${process.env.RADIEN_TENANT_URL}/activeTenant/find?userId=${userId}`;
        const result: AxiosResponse = await axios.get(path, {
            headers: {
                Authorization: `Bearer ${accessToken}`,
            },
        });
        const activeTenants: ActiveTenant[] = result.data;

        for (const tenant of activeTenants) {
            path = `${process.env.RADIEN_TENANT_URL}/activeTenant/${tenant.id}`;
            await axios.delete(path, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            });
        }
        return true;
    } catch (e) {
        if (e instanceof AxiosError) {
            console.log(e.response?.data);
        }
        return e instanceof AxiosError && e.response?.status === 404;
    }
};
export default async function setActiveTenant(req: NextApiRequest, res: NextApiResponse) {
    const { userId, tenantId } = req.query;
    const session = await getServerSession(req, res, authOptions);
    if (!session) {
        res.status(401).json({});
        return;
    }
    let path = `${process.env.RADIEN_TENANT_URL}/activeTenant`;

    if (await deleteActiveTenant(Number(userId), String(tenantId), session.accessToken!)) {
        const tenant: ActiveTenant = {
            userId: Number(userId),
            tenantId: Number(tenantId),
        };
        try {
            const result: AxiosResponse = await axios.post(path, tenant, {
                headers: {
                    Authorization: `Bearer ${session.accessToken}`,
                },
            });

            res.status(200).json(result.data);
        } catch (e) {
            res.status(500).json(e);
        }
    } else {
        res.status(500).json({});
    }
}
