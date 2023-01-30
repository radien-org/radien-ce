import {NextApiRequest, NextApiResponse} from "next";
import axios, {AxiosResponse} from "axios";
import {getServerSession} from "next-auth";
import {authOptions} from "@/pages/api/auth/[...nextauth]";
import {Page, Tenant} from "radien";

export default async (req: NextApiRequest, res: NextApiResponse) => {
    const { userId } = req.query;
    const session = await getServerSession(req, res, authOptions);
    if(!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_ROLE_URL}/tenantroleuser/tenants?userId=${userId}`;
    try {
        const result: AxiosResponse = await axios
            .get(path, {
                headers: {
                    "Authorization": `Bearer ${session.accessToken}`
                }
            });
        const resultPage: Page<Tenant> = {
            totalResults: result.data.length,
            currentPage: 1,
            totalPages: 1,
            results: result.data
        }
        res.status(200).json(resultPage);
    } catch(e) {
        res.status(500).json(e);
    }
}