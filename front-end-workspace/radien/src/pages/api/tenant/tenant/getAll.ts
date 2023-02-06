import {NextApiRequest, NextApiResponse} from "next";
import axios, {AxiosResponse} from "axios";
import {getServerSession} from "next-auth";
import {authOptions} from "@/pages/api/auth/[...nextauth]";

export default async (req: NextApiRequest, res: NextApiResponse) => {
    const { page, pageSize } = req.query;
    const session = await getServerSession(req, res, authOptions);
    if(!session) {
        res.status(401).json({});
        return;
    }

    const findTenant = (tenantId: number, tenants: any[]) => {
        return tenants.find((tenant: any) => tenant.id === tenantId);
    }
    const path = `${process.env.RADIEN_TENANT_URL}/tenant`;
    try {
        const result: AxiosResponse = await axios
            .get(path, {
                params: {
                    pageNo: page,
                    pageSize
                },
                headers: {
                    "Authorization": `Bearer ${session.accessToken}`
                }
            });
        let resp = result.data;
        resp.results = resp.results.map((tenant: any) => ({
            ...tenant,
            parentData: findTenant(tenant.parentId, resp.results),
            clientData: findTenant(tenant.clientId, resp.results)
        }));
        res.status(200).json(result.data);
    } catch(e) {
        res.status(500).json(e);
    }
}