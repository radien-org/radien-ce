import React from 'react';
import TopNavigation, {TopNavigationProps} from "@cloudscape-design/components/top-navigation";
import {signIn, signOut, useSession} from "next-auth/react";
import axios, {AxiosResponse} from "axios";
import {ButtonDropdownProps} from "@cloudscape-design/components";
import {ActiveTenant, Tenant, User} from "radien";
import ItemOrGroup = ButtonDropdownProps.ItemOrGroup;
import Utility = TopNavigationProps.Utility;
import useAvailableTenants from '@/hooks/useAvailableTenants';
import useActiveTenant from "@/hooks/useActiveTenant";
import useCheckPermissions from "@/hooks/useCheckPermissions";
import {useUserInSession} from "@/hooks/useUserInSession";
import {QueryClient} from "react-query";
import {QueryKeys} from "@/consts";

React.useLayoutEffect = React.useEffect;

interface LoggedInProps {
    radienUser: User
}

const queryClient: QueryClient = new QueryClient();


const logout = async(): Promise<void> => {
    const { data: { path }} = await axios.get('/api/auth/logout');
    await signOut({redirect: false});
    await queryClient.invalidateQueries({queryKey: QueryKeys.ME});
    window.location.href = path;
}

const itemClicked = async (event: CustomEvent<ButtonDropdownProps.ItemClickDetails>, userId?: number) => {
    if(event.detail.id === 'signout') {
        await logout();
    }
}

const updateActiveTenant = (userId: number, tenantId: number, activeTenant?: ActiveTenant) => {
    if(activeTenant) {
        return axios.get(`/api/tenant/activeTenant/setActiveTenant?userId=${userId}&tenantId=${tenantId}&activeTenantId=${activeTenant.id}`);
    }
    return axios.get(`/api/tenant/activeTenant/setActiveTenant?userId=${userId}&tenantId=${tenantId}`);
}

const i18nStrings = {
    searchIconAriaLabel: "Search",
    searchDismissIconAriaLabel: "Close search",
    overflowMenuTriggerText: "More",
    overflowMenuTitleText: "All",
    overflowMenuBackIconAriaLabel: "Back",
    overflowMenuDismissIconAriaLabel: "Close menu"
}

export default function Header() {
    const { data: session } = useSession();
    const { userInSession, isLoadingUserInSession, isLoadingUserInSessionError } = useUserInSession();

    if(!session || isLoadingUserInSession || isLoadingUserInSessionError) {
        return <LoggedOutHeader />
    } else {
        return <LoggedInHeader radienUser={userInSession!.data}/>
    }
}

function LoggedOutHeader() {
    return (
            <TopNavigation
                identity={{
                    href: "/",
                    logo: {src: '/top-navigation/trademark.svg', alt: 'ra\'di\'en'}
                }}
                utilities={[{
                    type: "button",
                    title: "Log In",
                    text: "Log In",
                    ariaLabel: "Log In",
                    onClick: () => signIn("keycloak")
                }]}
                i18nStrings={i18nStrings}/>
    );
}

function LoggedInHeader(props: LoggedInProps) {
    const { radienUser } = props;
    const { isLoading: loadingAvailableTenants, data: availableTenants } = useAvailableTenants(radienUser.id);
    const { isLoading: activeTenantLoading, data: activeTenant, refetch: refetchActiveTenant } = useActiveTenant(radienUser.id);
    const [
        { isLoading: isLoadingRoles, data: rolesViewPermission },
        { isLoading: isLoadingUser, data: usersViewPermission },
        { isLoading: isLoadingPermission, data: permissionViewPermission },
        { isLoading: isLoadingTenant, data: tenantViewPermission },
    ] = useCheckPermissions(radienUser.id!, activeTenant?.tenantId!);
    const tenantClicked = async (event: CustomEvent<ButtonDropdownProps.ItemClickDetails>) => {
        const response: AxiosResponse = await updateActiveTenant(radienUser.id!, Number(event.detail.id), activeTenant);
        if(response.status === 200) {
            await refetchActiveTenant();
        }
    }

    let utilities: TopNavigationProps.Utility[];
    if(loadingAvailableTenants || activeTenantLoading) {
        utilities = [
            {
                type: "menu-dropdown",
                ariaLabel: 'Loading...',
                text: 'Loading...',
                title: 'Loading...',
                items: []
            },
            {
                type: "menu-dropdown",
                text: `${radienUser.firstname} ${radienUser.lastname}`,
                description: radienUser.userEmail,
                iconName: "user-profile",
                onItemClick: (event) => itemClicked(event, radienUser.id),
                items: [
                    { id: "profile", text: "Profile", href: "/user/userProfile" },
                    {
                        id: "support-group",
                        text: "Support",
                        items: [
                            {
                                id: "documentation",
                                text: "Documentation",
                                href: "https://rethink.atlassian.net/wiki/spaces/RP",
                                external: true,
                                externalIconAriaLabel:
                                    " (opens in new tab)"
                            },
                            { id: "support", text: "Support", href: "https://rethink.atlassian.net/wiki/spaces/RP", external: true },
                        ]
                    },
                    { id: "signout", text: "Sign out" }
                ]
            }
        ];
    }
    else {
        utilities = [
            {
                type: "menu-dropdown",
                iconName: "multiscreen",
                ariaLabel: availableTenants?.results.find((t: Tenant) => t.id == activeTenant?.tenantId)?.name || "No tenant selected....",
                text: availableTenants?.results.find((t: Tenant) => t.id == activeTenant?.tenantId)?.name || "No tenant selected....",
                title: availableTenants?.results.find((t: Tenant) => t.id == activeTenant?.tenantId)?.name || "No tenant selected....",
                onItemClick: (event) => tenantClicked(event),
                items: availableTenants ? availableTenants.results.map((tenant: Tenant) => { return { id: `${tenant.id}`, text: tenant.name }}) : []
            },
            {
                type: "menu-dropdown",
                text: `${radienUser.firstname} ${radienUser.lastname}`,
                description: radienUser.userEmail,
                iconName: "user-profile",
                onItemClick: (event) => itemClicked(event, radienUser.id),
                items: [
                    { id: "profile", text: "Profile", href: "/user/userProfile" },
                    {
                        id: "support-group",
                        text: "Support",
                        items: [
                            {
                                id: "documentation",
                                text: "Documentation",
                                href: "https://rethink.atlassian.net/wiki/spaces/RP",
                                external: true,
                                externalIconAriaLabel:
                                    " (opens in new tab)"
                            },
                            { id: "support", text: "Support", href: "https://rethink.atlassian.net/wiki/spaces/RP", external: true },
                        ]
                    },
                    { id: "signout", text: "Sign out" }
                ]
            }
        ];
    }
        let systemMenus: Utility = {
            type: "menu-dropdown",
            iconName: "settings",
            ariaLabel: "Settings",
            text: "Settings",
            title: "Settings",
            items: []
        };
        if(!isLoadingPermission && permissionViewPermission) {
            const item: ItemOrGroup = {
                id: "permissionManagement",
                text: "Permission Management",
                href: "/system/permissionManagement"
            };
            systemMenus.items = [item, ...systemMenus.items];
        }
        if(!isLoadingRoles && rolesViewPermission) {
            const item: ItemOrGroup = {
                id: "roleManagement",
                text: "Role Management",
                href: "/system/roleManagement"
            };
            systemMenus.items = [item, ...systemMenus.items];
        }
        if(!isLoadingTenant && tenantViewPermission) {
            const item: ItemOrGroup = {
                id: "tenantManagement",
                text: "Tenant Management",
                href: "/system/tenantManagement"
            };
            systemMenus.items = [item, ...systemMenus.items];
        }
        if(!isLoadingUser && usersViewPermission) {
            const item: ItemOrGroup = {
                id: "userManagement",
                text: "User Management",
                href: "/system/userManagement"
            };
            systemMenus.items = [item, ...systemMenus.items];
        }
        utilities.splice(0, 0, systemMenus);

    return (
        <TopNavigation
            identity={{
                href: "/",
                logo: {src: '/top-navigation/trademark.svg', alt: 'ra\'di\'en'}
            }}
            utilities={utilities}
            i18nStrings={i18nStrings}/>
    );
}