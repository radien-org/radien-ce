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

    const path = `${process.env.RADIEN_PERMISSION_URL}/permission`;
    let pathAction = `${process.env.RADIEN_PERMISSION_URL}/action/find`
    let pathResource = `${process.env.RADIEN_PERMISSION_URL}/resource/find`;
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

        const actionsResp: AxiosResponse = await axios
            .get(pathAction, {
                headers: {"Authorization": `Bearer ${session.accessToken}`}
            });
        const resourcesResp: AxiosResponse = await axios
            .get(pathResource, {
                headers: {"Authorization": `Bearer ${session.accessToken}`}
            });
        let resp = result.data;
        resp.results = resp.results.map((permission: any) => ({
            ...permission,
            action: actionsResp.data.find((action: any) => action.id === permission.actionId),
            resource: resourcesResp.data.find((resource: any) => resource.id === permission.resourceId)
        }));
        res.status(200).json(resp);
    } catch(e) {
        res.status(500).json(e);
    }
}