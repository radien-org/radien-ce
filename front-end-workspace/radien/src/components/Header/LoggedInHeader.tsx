import { useRouter } from "next/router";
import React, { useContext } from "react";
import { RadienContext } from "@/context/RadienContextProvider";
import useAssignedTenants from "@/hooks/useAssignedTenants";
import useCheckPermissions from "@/hooks/useCheckPermissions";
import useSetActiveTenant from "@/hooks/useSetActiveTenant";
import { QueryClient } from "react-query";
import { ButtonDropdownProps, TopNavigationProps } from "@cloudscape-design/components";
import ThemeToggle from "@/components/Header/ThemeToggle";
import { Tenant } from "radien";
import TopNavigation from "@cloudscape-design/components/top-navigation";
import { HeaderProps, logout } from "@/components/Header/Header";
import ItemOrGroup = ButtonDropdownProps.ItemOrGroup;
import MenuDropdownUtility = TopNavigationProps.MenuDropdownUtility;

export default function LoggedInHeader({ topNavigationProps, localeClicked, i18nStrings }: HeaderProps) {
    const router = useRouter();
    const {
        userInSession: radienUser,
        i18n,
        activeTenant: { data: activeTenant, isLoading: activeTenantLoading },
    } = useContext(RadienContext);
    const { isLoading: loadingAssignedTenants, data: assignedTenants } = useAssignedTenants(radienUser!.id);
    const {
        user: { isLoading: isLoadingUser, data: usersViewPermission },
        permission: { isLoading: isLoadingPermission, data: permissionViewPermission },
        tenant: { isLoading: isLoadingTenant, data: tenantViewPermission },
        tenantAdmin: { data: isTenantAdmin, isLoading: isTenantAdminLoading },
    } = useCheckPermissions(radienUser?.id!, activeTenant?.tenantId!);
    const setActiveTenant = useSetActiveTenant();
    const queryClient: QueryClient = new QueryClient();

    const itemClicked = async (event: CustomEvent<ButtonDropdownProps.ItemClickDetails>, userId?: number) => {
        if (event.detail.id === "signout") {
            await logout(queryClient);
        }
    };

    topNavigationProps.utilities = [];
    if (loadingAssignedTenants || activeTenantLoading) {
        topNavigationProps.utilities = [
            {
                type: "menu-dropdown",
                ariaLabel: i18n?.loading || "Loading...",
                text: i18n?.loading || "Loading...",
                title: i18n?.loading || "Loading...",
                items: [],
            },
            {
                type: "menu-dropdown",
                text: `${radienUser!.firstname} ${radienUser!.lastname}`,
                description: radienUser!.userEmail,
                iconName: "user-profile",
                onItemClick: (event) => itemClicked(event, radienUser!.id),
                items: [
                    { id: "profile", text: i18n?.user_profile || "Profile", href: `${router.locale ? "/" + router.locale : ""}/user/userProfile` },
                    {
                        id: "support-group",
                        text: i18n?.support_title || "Support",
                        items: [
                            {
                                id: "documentation",
                                text: i18n?.support_documentation || "Documentation",
                                href: "https://rethink.atlassian.net/wiki/spaces/RP",
                                external: true,
                                externalIconAriaLabel: " (opens in new tab)",
                            },
                            { id: "support", text: i18n?.support_title || "Support", href: "https://rethink.atlassian.net/wiki/spaces/RP", external: true },
                        ],
                    },
                    { id: "theme", text: "", iconSvg: ThemeToggle() },
                    { id: "signout", text: i18n?.log_out || "Sign out" },
                ],
            },
            {
                type: "menu-dropdown",
                iconName: "flag",
                ariaLabel: router.locale,
                text: router.locale,
                title: router.locale,
                onItemClick: async (event) => localeClicked(router, event),
                items: router.locales
                    ? router.locales.map((locale: string) => {
                          return { id: `${locale}`, text: locale };
                      })
                    : [],
            },
        ];
    } else {
        const activeTenantName =
            assignedTenants?.results.find((t: Tenant) => t.id == activeTenant?.tenantId)?.name ||
            i18n?.active_tenant_no_active_tenant ||
            "No tenant selected....";
        topNavigationProps.utilities = [
            {
                type: "menu-dropdown",
                iconName: "multiscreen",
                ariaLabel: activeTenantName,
                text: activeTenantName,
                title: activeTenantName,
                onItemClick: (event) =>
                    setActiveTenant.mutate({ userId: radienUser!.id!, tenantId: Number(event.detail.id), activeTenantId: activeTenant?.id }),
                items: assignedTenants
                    ? assignedTenants.results.map((tenant: Tenant) => {
                          return { id: `${tenant.id}`, text: tenant.name };
                      })
                    : [],
            },
            {
                type: "menu-dropdown",
                text: `${radienUser!.firstname} ${radienUser!.lastname}`,
                description: radienUser!.userEmail,
                iconName: "user-profile",
                onItemClick: (event) => itemClicked(event, radienUser!.id),
                items: [
                    { id: "profile", text: i18n?.user_profile || "Profile", href: `${router.locale ? "/" + router.locale : ""}/user/userProfile` },
                    {
                        id: "support-group",
                        text: i18n?.support_title || "Support",
                        items: [
                            {
                                id: "documentation",
                                text: i18n?.support_documentation || "Documentation",
                                href: "https://rethink.atlassian.net/wiki/spaces/RP",
                                external: true,
                                externalIconAriaLabel: " (opens in new tab)",
                            },
                            { id: "support", text: i18n?.support_title || "Support", href: "https://rethink.atlassian.net/wiki/spaces/RP", external: true },
                        ],
                    },
                    { id: "theme", text: "", iconSvg: ThemeToggle() },
                    { id: "signout", text: i18n?.log_out || "Sign out" },
                ],
            },
            {
                type: "menu-dropdown",
                iconName: "flag",
                ariaLabel: router.locale,
                text: router.locale,
                title: router.locale,
                onItemClick: async (event) => localeClicked(router, event),
                items: router.locales
                    ? router.locales.map((locale: string) => {
                          return { id: `${locale}`, text: locale };
                      })
                    : [],
            },
        ];
    }

    let systemMenus: MenuDropdownUtility = {
        type: "menu-dropdown",
        iconName: "settings",
        ariaLabel: i18n?.settings || "Settings",
        text: i18n?.settings || "Settings",
        title: i18n?.settings || "Settings",
        items: [],
    };
    if (!isLoadingPermission && permissionViewPermission) {
        const item: ItemOrGroup = {
            id: "permissionManagement",
            text: i18n?.system_permission_management || "Permission Management",
            href: `${router.locale ? "/" + router.locale : ""}/system/permissionManagement`,
        };
        systemMenus.items = [item, ...systemMenus.items];
    }

    systemMenus = generateSystemRoleMenu(systemMenus);

    if (!isTenantAdminLoading && isTenantAdmin) {
        const item: ItemOrGroup = {
            id: "tenantAdmin",
            text: i18n?.system_permission_management || "Tenant Administration",
            href: `${router.locale ? "/" + router.locale : ""}/system/tenantAdmin`,
        };
        systemMenus.items = [item, ...systemMenus.items];
    }

    if (!isLoadingTenant && tenantViewPermission) {
        const item: ItemOrGroup = {
            id: "tenantManagement",
            text: i18n?.system_tenant_management || "Tenant Management",
            href: `${router.locale ? "/" + router.locale : ""}/system/tenantManagement`,
        };
        systemMenus.items = [item, ...systemMenus.items];
    }
    if (!isLoadingUser && usersViewPermission) {
        const item: ItemOrGroup = {
            id: "userManagement",
            text: i18n?.system_user_management || "User Management",
            href: `${router.locale ? "/" + router.locale : ""}/system/userManagement`,
        };
        systemMenus.items = [item, ...systemMenus.items];
    }
    topNavigationProps.utilities = [systemMenus, ...topNavigationProps.utilities];

    return (
        <>
            <TopNavigation identity={topNavigationProps.identity} utilities={topNavigationProps.utilities} i18nStrings={i18nStrings} />
        </>
    );
}

const generateSystemRoleMenu = (systemMenus: MenuDropdownUtility) => {
    const router = useRouter();
    const {
        userInSession: radienUser,
        i18n,
        activeTenant: { data: activeTenant },
    } = useContext(RadienContext);
    const {
        roles: { isLoading: isLoadingRoles, data: rolesViewPermission },
        tenantRoles: { isLoading: isLoadingTenantRoles, data: tenantRolesViewPermission },
    } = useCheckPermissions(radienUser?.id!, activeTenant?.tenantId!);
    const updatedSystemMenus = { ...systemMenus };

    const roleGroup: ItemOrGroup = {
        id: "rolesManagement",
        text: i18n?.system_role_management || "Role Management",
        items: [],
    };
    if (!isLoadingRoles && rolesViewPermission) {
        const item: ItemOrGroup = {
            id: "roleManagement",
            text: i18n?.system_role_management || "Role Management",
            href: `${router.locale ? "/" + router.locale : ""}/system/roleManagement`,
        };
        roleGroup.items = [...roleGroup.items, item];
    }
    if (!isLoadingTenantRoles && tenantRolesViewPermission) {
        const item: ItemOrGroup = {
            id: "tenantRoleManagement",
            text: i18n?.system_tenant_role_management || "Tenant Role Management",
            href: `${router.locale ? "/" + router.locale : ""}/system/tenantRoleManagement`,
        };
        roleGroup.items = [...roleGroup.items, item];
    }
    if (roleGroup.items.length > 0) {
        updatedSystemMenus.items = [roleGroup, ...systemMenus.items];
    }
    return updatedSystemMenus;
};
