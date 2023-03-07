import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosError, AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";
import { UserPasswordChanging } from "radien";

export default async function updatePassword(req: NextApiRequest, res: NextApiResponse) {
    const session = await getServerSession(req, res, authOptions);
    const userPassword: UserPasswordChanging = req.body;
    if (!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_USER_URL}/user/${userPassword.id}/passCredential`;
    let resultCode = 200;
    const result: AxiosResponse = await axios
        .patch(path, userPassword, {
            headers: {
                Authorization: `Bearer ${session.accessToken}`,
            },
        })
        .catch((error) => {
            if (error instanceof AxiosError && error.response?.status == 400) {
                resultCode = 400;
                return error.response;
            }
            throw error;
        });
    res.status(resultCode).json(result.data);
}
