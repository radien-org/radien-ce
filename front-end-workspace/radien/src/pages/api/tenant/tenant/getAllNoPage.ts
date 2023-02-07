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

    const path = `${process.env.RADIEN_TENANT_URL}/tenant/find`;
    try {
        const result: AxiosResponse = await axios
            .get(path, {
                headers: {
                    "Authorization": `Bearer ${session.accessToken}`
                }
            });

        res.status(200).json(result.data);
    } catch(e) {
        res.status(500).json(e);
    }
}