import {NextApiRequest, NextApiResponse} from "next";
import axios, {AxiosResponse} from "axios";
import {unstable_getServerSession} from "next-auth";
import {authOptions} from "@/pages/api/auth/[...nextauth]";

export default async (req: NextApiRequest, res: NextApiResponse) => {
    const session = await unstable_getServerSession(req, res, authOptions);
    const { page, pageSize } = req.query;
    if(!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_USER_URL}/user`;
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

    res.status(200).json(result.data);
}