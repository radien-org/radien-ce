import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";
import { Role, TenantRole } from "radien";

export interface TenantRoleMapping {
    role: Role;
    tenantRole: TenantRole;
}

const getRolesByIds = async (roleIds: number[], accessToken: string) => {
    const path = `${process.env.RADIEN_ROLE_URL}/role/find`;
    const result: AxiosResponse<Role[]> = await axios.get(path, {
        params: {
            ids: roleIds,
        },

        paramsSerializer: { indexes: null },
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    });
    return result.data;
};

export default async (req: NextApiRequest, res: NextApiResponse) => {
    const { tenantId } = req.query;
    const session = await getServerSession(req, res, authOptions);
    if (!session) {
        res.status(401).json({});
        return;
    }

    const path = `${process.env.RADIEN_ROLE_URL}/tenantrole/find`;
    try {
        const { data }: AxiosResponse<TenantRole[]> = await axios.get(path, {
            params: {
                tenantId,
            },
            headers: {
                Authorization: `Bearer ${session.accessToken}`,
            },
        });

        const roles = await getRolesByIds(
            data.map((v) => v.roleId),
            session.accessToken!
        );
        const result: TenantRoleMapping[] = data.map((tenantRole) => {
            const role = roles.find((r) => r.id == tenantRole.roleId);
            return {
                role: role!,
                tenantRole,
            };
        });

        res.status(200).json(result);
    } catch (e) {
        res.status(500).json(e);
    }
};
