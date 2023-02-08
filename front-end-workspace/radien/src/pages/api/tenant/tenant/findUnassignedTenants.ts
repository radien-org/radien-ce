import {NextApiRequest, NextApiResponse} from "next";
import axios, {AxiosResponse} from "axios";
import {getServerSession} from "next-auth";
import {authOptions} from "@/pages/api/auth/[...nextauth]";
import {Tenant} from "radien";

const getUserTenants = async (userId: number, accessToken: string) => {
    const path = `${process.env.RADIEN_ROLE_URL}/tenantroleuser/tenants?userId=${userId}`;
    const result: AxiosResponse<Tenant[]> = await axios
        .get(path, {
            headers: {
                "Authorization": `Bearer ${accessToken}`
            }
        });
    return result.data;
}

export default async (req: NextApiRequest, res: NextApiResponse) => {
    const session = await getServerSession(req, res, authOptions);
    const { userId } = req.query;
    if(!session) {
        res.status(401).json({});
        return;
    }

    const userTenants = await getUserTenants(Number(userId), session.accessToken!)
    const path = `${process.env.RADIEN_TENANT_URL}/tenant/find`;
    try {
        const result: AxiosResponse<Tenant[]> = await axios
            .get(path, {
                headers: { "Authorization": `Bearer ${session.accessToken}` }
            });

        const availableTenants = result.data
            .filter(tenant => tenant.id !== 1)
            .filter(tenant => !userTenants.find(userTenant => userTenant.id == tenant.id));

        res.status(200).json(availableTenants);
    } catch(e) {
        res.status(500).json(e);
    }
}