import {NextApiRequest, NextApiResponse} from "next";
import axios, {AxiosError, AxiosResponse} from "axios";
import {getServerSession} from "next-auth";
import {authOptions} from "@/pages/api/auth/[...nextauth]";

export default async (req: NextApiRequest, res: NextApiResponse) => {
    const { userId, resource, action, tenantId } = req.query;
    const session = await getServerSession(req, res, authOptions);
    if(!session) {
        res.status(401).json({});
        return;
    }
    let permissionId = await getPermissionId(String(resource), String(action), session.accessToken!);
    if(permissionId != -1) {
        let path = `${process.env.RADIEN_ROLE_URL}/tenantrole/exists/permission?userId=${session.radienUser.id}&permissionId=${permissionId}&tenantId=${tenantId}`;
        let permissionCheck = await checkForPermission(path, session.accessToken!);
        if(!permissionCheck) {
            permissionId = await getPermissionId(String(resource), "All", session.accessToken!);
            path = `${process.env.RADIEN_ROLE_URL}/tenantrole/exists/permission?userId=${session.radienUser.id}&permissionId=${permissionId}&tenantId=${tenantId}`;
            permissionCheck = await checkForPermission(path, session.accessToken!);
        }
        res.status(200).json(permissionCheck);
        return;
    }
    res.status(200).json(false);
}

const getPermissionId = async (resource: string, action: string, accessToken: string): Promise<number> => {
    const path = `${process.env.RADIEN_PERMISSION_URL}/permission/id?resource=${resource}&action=${action}`;
    try {
        const result: AxiosResponse = await axios
            .get(path, {
                headers: {
                    "Authorization": `Bearer ${accessToken}`
                }
            });
        return result.data;
    } catch(e) {
        console.log(e);
        if(e instanceof AxiosError) {
            console.log(e.message);
        }
        return -1;
    }
}

const checkForPermission = async (path: string, accessToken: string) => {
    try {
        const result: AxiosResponse = await axios.get(path, {
            headers: {
                "Authorization": `Bearer ${accessToken}`
            }
        });
        return result.data;
    } catch(e) {
        console.log(e);
        if(e instanceof AxiosError) {
            console.log(e.message);
        }
        return false;
    }
}