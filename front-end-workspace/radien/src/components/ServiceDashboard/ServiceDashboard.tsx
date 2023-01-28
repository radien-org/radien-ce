import React, {useEffect} from 'react';
import Card from "@/components/Card/Card";
import useCheckPermissions from "@/hooks/useCheckPermissions";
import {useSession} from "next-auth/react";
import useActiveTenant from "@/hooks/useActiveTenant";

export default function ServiceDashboard() {
    const { data: session } = useSession();
    const { data: activeTenantData } = useActiveTenant(session!.radienUser.id!);
    const [
        { data: rolesViewPermission },
        { data: usersViewPermission },
        { data: permissionViewPermission },
    ] = useCheckPermissions(session!.radienUser.id!, activeTenantData?.tenantId!);

    const cards = [
        {
            title: "User Management",
            description: "This service allows you to see and check your users data as so as delete them.",
            href: "/system/userManagement",
            hasPermission: usersViewPermission
        },
        {
            title: "Role Management",
            description: "In here you can create and assign roles to a specific user in a specific tenant.",
            href: "/system/roleManagement",
            hasPermission: rolesViewPermission
        },
        {
            title: "Permission Management",
            description: "Which permissions are going to be allowed? In here you can define them.",
            href: "/system/permissionManagement",
            hasPermission: permissionViewPermission
        }
    ];


    return (
        <div className="container my-12 mx-auto px-4 md:px-12">
            <div className="flex flex-wrap -mx-1 lg:-mx-4">

                {cards.filter(c => c.hasPermission).map(c =>  <Card key={c.title} title={c.title} description={c.description} href={c.href} /> ) }

            </div>
        </div>
);
}