import {NextApiRequest, NextApiResponse} from "next";
import axios, {AxiosResponse} from "axios";
import {getServerSession} from "next-auth";
import {authOptions} from "@/pages/api/auth/[...nextauth]";
import {Page, Role} from "radien";

const getRoleIds = async (roleNames: string [], token: string) => {
    const path = `${process.env.RADIEN_ROLE_URL}/role/find`;
    const bearer = {"Authorization": `Bearer ${token}`};

    return await Promise.all(
        roleNames.map(name => {
            return axios.get<Role[]>(path, { params: {name}, headers: bearer})
        })
    )
}

const getUserIds = async (roleIds: number[], tenantId: number, token: string) => {
    const path = `${process.env.RADIEN_ROLE_URL}/tenantroleuser/userIds`;
    const bearer = {"Authorization": `Bearer ${token}`};
    const pageSize = 200;
    return await Promise.all(
        roleIds.map(id => {
            return axios.get<Page<number>>(path, {params: {roleId: id, tenantId, pageSize}, headers: bearer})
        })
    );
}

export default async (req: NextApiRequest, res: NextApiResponse) => {
    const session = await getServerSession(req, res, authOptions);
    const { tenantId, viewId, language } = req.query;
    const { args, roles }  = req.body;
    if(!session) {
        res.status(401).json({});
        return;
    }

    const roleIds = (await getRoleIds(roles, session.accessToken!))
        .map(result => result.data)
        .filter(result => !!result && result.length > 0)
        .map(result => result[0].id!);

    let userIds = (await getUserIds(roleIds, Number(tenantId), session.accessToken!))
        .map(result => result.data)
        .filter(result => result.totalResults >= 1)
        .map(result => result.results).flat(1);

    if(userIds.length == 0) {
        res.status(404).json({error: "Error: No Administrators to notify were found"})
    }

    const path = `${process.env.RADIEN_NOTIFICATION_URL}/email/users`;

    const result: AxiosResponse = await axios
        .post(path, args, {
            params: {
                userIds: userIds,
                viewId,
                language,
            },
            paramsSerializer: {indexes: null },
            headers: {
                "Authorization": `Bearer ${session.accessToken}`
            }
        });

    res.status(200).json({});
}