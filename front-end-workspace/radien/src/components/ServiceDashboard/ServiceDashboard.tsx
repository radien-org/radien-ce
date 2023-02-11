import React, { useContext } from "react";
import Card from "@/components/Card/Card";
import useCheckPermissions from "@/hooks/useCheckPermissions";
import { RadienContext } from "@/context/RadienContextProvider";
import { useRouter } from "next/router";

export default function ServiceDashboard() {
    const {
        userInSession: radienUser,
        activeTenant: { data: activeTenantData },
        i18n,
    } = useContext(RadienContext);
    const { locale } = useRouter();
    const [{ data: rolesViewPermission }, { data: usersViewPermission }, { data: permissionViewPermission }, { data: tenantViewPermission }] =
        useCheckPermissions(radienUser?.id!, activeTenantData?.tenantId!);

    const cards = [
        {
            title: i18n?.user_management_title || "User Management",
            description: i18n?.user_management_description || "This service allows you to see and check your users data as so as delete them.",
            href: `/system/userManagement`,
            hasPermission: usersViewPermission,
            locale,
        },
        {
            title: i18n?.role_management_title || "Role Management",
            description: i18n?.role_management_description || "In here you can create and assign roles to a specific user in a specific tenant.",
            href: `/system/roleManagement`,
            hasPermission: rolesViewPermission,
            locale,
        },
        {
            title: i18n?.permission_management_title || "Permission Management",
            description: i18n?.permission_management_description || "Which permissions are going to be allowed? In here you can define them.",
            href: `/system/permissionManagement`,
            hasPermission: permissionViewPermission,
            locale,
        },
        {
            title: i18n?.tenant_management_title || "Tenant Management",
            description: i18n?.tenant_management_description || "Which tenant are going to exist.",
            href: `/system/tenantManagement`,
            hasPermission: tenantViewPermission,
            locale,
        },
    ];

    return (
        <div className="container my-12 mx-auto px-4 md:px-12">
            <div className="flex flex-wrap -mx-1 lg:-mx-4">
                {cards
                    .filter((c) => c.hasPermission)
                    .map((c) => (
                        <Card key={c.title} title={c.title} description={c.description} href={c.href} />
                    ))}
            </div>
        </div>
    );
}
