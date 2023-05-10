import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosError, AxiosResponse } from "axios";
import { getServerSession } from "next-auth";
import { authOptions } from "@/pages/api/auth/[...nextauth]";
import { Role, Tenant, Ticket, User } from "radien";
import { TicketType } from "@/consts";

export default async function getUserData(req: NextApiRequest, res: NextApiResponse) {
    const session = await getServerSession(req, res, authOptions);
    const { ticket } = req.query;
    if (!session || !ticket) {
        res.status(401).json({});
        return;
    }

    const ticketResponse = await getTicketByToken(String(session.accessToken), String(ticket)).catch(() => undefined);
    console.log(ticketResponse);
    if (!ticketResponse || !ticketResponse.data || ticketResponse.data.ticketType != TicketType.GDPR_DATA_REQUEST) {
        res.status(401).json({ message: "Invalid token provided" });
        return;
    }

    const { data: ticketData } = ticketResponse;
    const { data: userData } = await getUserById(String(session.accessToken), ticketData.userId);
    const { data: tenantData } = await getTenants(String(session.accessToken), ticketData.userId);
    let userTenantRoles: Role[] = [];
    for (const tenant of tenantData) {
        const { data: roleData } = await getRolesForUserTenant(String(session.accessToken), ticketData.userId, tenant.id!);
        userTenantRoles = userTenantRoles.concat(roleData);
    }

    const result = {
        userData,
        tenantData,
        roleData: userTenantRoles,
    };

    await deleteTicket(String(session.accessToken), ticketData.id!);
    res.status(200).setHeader("Content-disposition", "attachment; filename=user-data.json").json(result);
}

const getTicketByToken = (accessToken: string, token: string): Promise<AxiosResponse<Ticket, AxiosError>> => {
    const path = `${process.env.RADIEN_TICKET_URL}/ticket/token`;
    return axios.get(path, {
        params: {
            token,
        },
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    });
};

const deleteTicket = (accessToken: string, id: number) => {
    const path = `${process.env.RADIEN_TICKET_URL}/ticket`;
    return axios.delete(path, {
        params: {
            id,
        },
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    });
};

const getUserById = (accessToken: string, userId: number): Promise<AxiosResponse<User, AxiosError>> => {
    const path = `${process.env.RADIEN_USER_URL}/user/${userId}`;
    return axios.get(path, {
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    });
};

const getTenants = (accessToken: string, userId: number): Promise<AxiosResponse<Tenant[], AxiosError>> => {
    const path = `${process.env.RADIEN_ROLE_URL}/tenantroleuser/tenants?userId=${userId}`;
    return axios.get(path, {
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    });
};

const getRolesForUserTenant = (accessToken: string, userId: number, tenantId: number): Promise<AxiosResponse<Role[], AxiosError>> => {
    const path = `${process.env.RADIEN_ROLE_URL}/tenantrole/rolesUserTenant`;
    return axios.get(path, {
        params: { userId, tenantId },
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    });
};
