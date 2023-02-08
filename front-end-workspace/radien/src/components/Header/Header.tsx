import React, { useContext } from "react";
import TopNavigation, { TopNavigationProps } from "@cloudscape-design/components/top-navigation";
import { signIn, signOut, useSession } from "next-auth/react";
import axios, { AxiosResponse } from "axios";
import { ButtonDropdownProps } from "@cloudscape-design/components";
import { ActiveTenant, Tenant, User } from "radien";
import ItemOrGroup = ButtonDropdownProps.ItemOrGroup;
import Utility = TopNavigationProps.Utility;
import useAssignedTenants from '@/hooks/useAssignedTenants';
import useCheckPermissions from "@/hooks/useCheckPermissions";
import { QueryClient } from "react-query";
import { QueryKeys } from "@/consts";
import { RadienContext } from "@/context/RadienContextProvider";
import ThemeToggle from "@/components/Header/ThemeToggle";
import { NextRouter, useRouter } from "next/router";

React.useLayoutEffect = React.useEffect;

interface LoggedOutProps {
    i18n: any;
    router: NextRouter;
}

interface LoggedInProps {
    i18n: any;
    locale?: string;
    locales?: string[];
    radienUser: User;
    router: NextRouter;
}

const queryClient: QueryClient = new QueryClient();

const logout = async (): Promise<void> => {
    const {
        data: { path },
    } = await axios.get("/api/auth/logout");
    await signOut({ redirect: false });
    await queryClient.invalidateQueries({ queryKey: QueryKeys.ME });
    window.location.href = path;
};

const itemClicked = async (event: CustomEvent<ButtonDropdownProps.ItemClickDetails>, userId?: number) => {
    if (event.detail.id === "signout") {
        await logout();
    }
};

const updateActiveTenant = (userId: number, tenantId: number, activeTenant?: ActiveTenant) => {
    if (activeTenant) {
        return axios.get(`/api/tenant/activeTenant/setActiveTenant?userId=${userId}&tenantId=${tenantId}&activeTenantId=${activeTenant.id}`);
    }
    return axios.get(`/api/tenant/activeTenant/setActiveTenant?userId=${userId}&tenantId=${tenantId}`);
};

const i18nStrings = {
    searchIconAriaLabel: "Search",
    searchDismissIconAriaLabel: "Close search",
    overflowMenuTriggerText: "More",
    overflowMenuTitleText: "All",
    overflowMenuBackIconAriaLabel: "Back",
    overflowMenuDismissIconAriaLabel: "Close menu",
};

export default function Header() {
    const { data: session } = useSession();
    const router = useRouter();
    const { userInSession, isLoadingUserInSession, i18n } = useContext(RadienContext);

    if (!session || isLoadingUserInSession) {
        return <LoggedOutHeader i18n={i18n} router={router} />;
    } else {
        return <LoggedInHeader radienUser={userInSession!} i18n={i18n} router={router} />;
    }
}

function LoggedOutHeader({ i18n, router }: LoggedOutProps) {
    const localeClicked = async (event: CustomEvent<ButtonDropdownProps.ItemClickDetails>) => {
        const { pathname, asPath, query } = router;
        await router.push({ pathname, query }, asPath, { locale: event.detail.id });
    };

    return (
        <>
            <TopNavigation
                identity={{
                    href: `${router.locale ? "/" + router.locale : ""}/`,
                    logo: { src: "/top-navigation/trademark.svg", alt: "ra'di'en" },
                }}
                utilities={[
                    {
                        type: "button",
                        title: i18n?.log_in || "Log In",
                        text: i18n?.log_in || "Log In",
                        ariaLabel: i18n?.log_in || "Log In",
                        onClick: () => signIn("keycloak"),
                    },
                    {
                        type: "menu-dropdown",
                        iconName: "flag",
                        ariaLabel: router.locale,
                        text: router.locale,
                        title: router.locale,
                        onItemClick: async (event) => localeClicked(event),
                        items: router.locales
                            ? router.locales.map((locale: string) => {
                                  return { id: `${locale}`, text: locale };
                              })
                            : [],
                    },
                    { type: "button", text: "", iconSvg: ThemeToggle() },
                ]}
                i18nStrings={i18nStrings}
            />
        </>
    );
}

function LoggedInHeader(props: LoggedInProps) {
    const { radienUser, i18n, router } = props;
    const { isLoading: loadingAssignedTenants, data: assignedTenants } = useAssignedTenants(radienUser.id);
    const {
        activeTenant: { data: activeTenant, refetch: refetchActiveTenant, isLoading: activeTenantLoading },
    } = useContext(RadienContext);
    const [
        { isLoading: isLoadingRoles, data: rolesViewPermission },
        { isLoading: isLoadingUser, data: usersViewPermission },
        { isLoading: isLoadingPermission, data: permissionViewPermission },
        { isLoading: isLoadingTenant, data: tenantViewPermission },
    ] = useCheckPermissions(radienUser.id!, activeTenant?.tenantId!);
    const tenantClicked = async (event: CustomEvent<ButtonDropdownProps.ItemClickDetails>) => {
        const response: AxiosResponse = await updateActiveTenant(radienUser.id!, Number(event.detail.id), activeTenant);
        if (response.status === 200) {
            await refetchActiveTenant();
        }
    };
    const localeClicked = async (event: CustomEvent<ButtonDropdownProps.ItemClickDetails>) => {
        const { pathname, asPath, query } = router;
        await router.push({ pathname, query }, asPath, { locale: event.detail.id });
    };

    let utilities: TopNavigationProps.Utility[];
    if (loadingAssignedTenants || activeTenantLoading) {
        utilities = [
            {
                type: "menu-dropdown",
                ariaLabel: i18n?.loading || "Loading...",
                text: i18n?.loading || "Loading...",
                title: i18n?.loading || "Loading...",
                items: [],
            },
            {
                type: "menu-dropdown",
                text: `${radienUser.firstname} ${radienUser.lastname}`,
                description: radienUser.userEmail,
                iconName: "user-profile",
                onItemClick: (event) => itemClicked(event, radienUser.id),
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
                onItemClick: async (event) => localeClicked(event),
                items: router.locales
                    ? router.locales.map((locale: string) => {
                          return { id: `${locale}`, text: locale };
                      })
                    : [],
            },
        ];
    } else {
        utilities = [
            {
                type: "menu-dropdown",
                iconName: "multiscreen",
                ariaLabel:
                    assignedTenants?.results.find((t: Tenant) => t.id == activeTenant?.tenantId)?.name ||
                    i18n?.active_tenant_no_active_tenant ||
                    "No tenant selected....",
                text:
                    assignedTenants?.results.find((t: Tenant) => t.id == activeTenant?.tenantId)?.name ||
                    i18n?.active_tenant_no_active_tenant ||
                    "No tenant selected....",
                title:
                    assignedTenants?.results.find((t: Tenant) => t.id == activeTenant?.tenantId)?.name ||
                    i18n?.active_tenant_no_active_tenant ||
                    "No tenant selected....",
                onItemClick: (event) => tenantClicked(event),
                items: assignedTenants ? assignedTenants.results.map((tenant: Tenant) => {
                          return { id: `${tenant.id}`, text: tenant.name };
                      })
                    : [],
            },
            {
                type: "menu-dropdown",
                text: `${radienUser.firstname} ${radienUser.lastname}`,
                description: radienUser.userEmail,
                iconName: "user-profile",
                onItemClick: (event) => itemClicked(event, radienUser.id),
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
                onItemClick: async (event) => localeClicked(event),
                items: router.locales
                    ? router.locales.map((locale: string) => {
                          return { id: `${locale}`, text: locale };
                      })
                    : [],
            },
        ];
    }
    let systemMenus: Utility = {
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
    if (!isLoadingRoles && rolesViewPermission) {
        const item: ItemOrGroup = {
            id: "roleManagement",
            text: i18n?.system_role_management || "Role Management",
            href: `${router.locale ? "/" + router.locale : ""}/system/roleManagement`,
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
    utilities.splice(0, 0, systemMenus);

    return (
        <>
            <TopNavigation
                identity={{
                    href: `${router.locale ? "/" + router.locale : ""}/`,
                    logo: { src: "/top-navigation/trademark.svg", alt: "ra'di'en" },
                }}
                utilities={utilities}
                i18nStrings={i18nStrings}
            />
        </>
    );
}
