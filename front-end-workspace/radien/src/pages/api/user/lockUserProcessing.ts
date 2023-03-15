import {NextApiRequest, NextApiResponse} from "next";
import axios, {AxiosResponse} from "axios";
import {getServerSession} from "next-auth";
import {authOptions} from "@/pages/api/auth/[...nextauth]";

export default async function lockUserProcessing(req: NextApiRequest, res: NextApiResponse){
    const session = await getServerSession(req, res, authOptions);
    if (!session) {
        res.status(401).json({});
        return;
    }
    const { id, lock } = req.body;
    const path = `${process.env.RADIEN_USER_URL}/user/lock/${id}&${lock}`;
    const result: AxiosResponse = await axios.put(path, {}, {
        headers: {
            Authorization: `Bearer ${session!.accessToken}`,
        }
    });
    res.status(200).json(result.data);
}