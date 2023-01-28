import React from 'react';
import TopNavigation, {TopNavigationProps} from "@cloudscape-design/components/top-navigation";
import {signIn, signOut, useSession} from "next-auth/react";
import axios, {AxiosResponse} from "axios";
import {ButtonDropdownProps} from "@cloudscape-design/components";
import {ActiveTenant, Tenant} from "radien";
import ItemOrGroup = ButtonDropdownProps.ItemOrGroup;
import {Session} from "next-auth";
import Utility = TopNavigationProps.Utility;
import useAvailableTenants from '@/hooks/useAvailableTenants';
import useActiveTenant from "@/hooks/useActiveTenant";
import useCheckPermissions from "@/hooks/useCheckPermissions";

React.useLayoutEffect = React.useEffect;

interface LoggedInProps {
    session: Session,
}

const logout = async(): Promise<void> => {
    const { data: { path }} = await axios.get('/api/auth/logout');
    await signOut({redirect: false});
    window.location.href = path;
}

const itemClicked = async (event: CustomEvent<ButtonDropdownProps.ItemClickDetails>, userId?: number) => {
    if(event.detail.id === 'signout') {
        await logout();
    }
}

const updateActiveTenant = (tenantId: number, activeTenant: ActiveTenant) => {
    if(activeTenant) {
        return axios.get(`/api/tenant/activeTenant/setActiveTenant?tenantId=${tenantId}&activeTenantId=${activeTenant.id}`);
    }
    return axios.get(`/api/tenant/activeTenant/setActiveTenant?tenantId=${tenantId}`);
}

const i18nStrings = {
    searchIconAriaLabel: "Search",
    searchDismissIconAriaLabel: "Close search",
    overflowMenuTriggerText: "More",
    overflowMenuTitleText: "All",
    overflowMenuBackIconAriaLabel: "Back",
    overflowMenuDismissIconAriaLabel: "Close menu"
}

const checkPermission = (userId: number, resource: string, action: string, tenantId: number) => {
    const path = `/api/role/tenantrolepermission/hasPermission?userId=${userId}&resource=${resource}&action=${action}&tenantId=${tenantId}`;
    return axios.get(path);
}

export default function Header() {
    const { data: session } = useSession();

    if(!session) {
        return <LoggedOutHeader />
    } else {
        return <LoggedInHeader session={session}/>
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
    const permissions = {
        roles: {resource: "Roles", action: "Read"},
        user: {resource: "User", action: "Read"},
        permission: {resource: "Permission", action: "Read"},
    }

    const { session } = props;
    const { isLoading: loadingAvailableTenants, data: availableTenants } = useAvailableTenants(session.radienUser!.id!);
    const { isLoading: activeTenantLoading, data: activeTenant, refetch: refetchActiveTenant } = useActiveTenant(session.radienUser!.id!);
    const [
        { isLoading: isLoadingRoles, data: rolesViewPermission, refetch: refetchRoles },
        { isLoading: isLoadingUser, data: usersViewPermission, refetch: refetchUser },
        { isLoading: isLoadingPermission, data: permissionViewPermission, refetch: refetchPermission },
    ] = useCheckPermissions(session!.radienUser.id!, activeTenant?.tenantId!);
    const tenantClicked = async (event: CustomEvent<ButtonDropdownProps.ItemClickDetails>) => {
        const response: AxiosResponse = await updateActiveTenant(Number(event.detail.id), activeTenant!);
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
                text: `${session.radienUser.firstname} ${session.radienUser.lastname}`,
                description: session.radienUser.userEmail,
                iconName: "user-profile",
                onItemClick: (event) => itemClicked(event, session.radienUser.id),
                items: [
                    { id: "profile", text: "Profile" },
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
                ariaLabel: availableTenants?.find((t: Tenant) => t.id == activeTenant?.tenantId)?.name || "No tenant selected....",
                text: availableTenants?.find((t: Tenant) => t.id == activeTenant?.tenantId)?.name || "No tenant selected....",
                title: availableTenants?.find((t: Tenant) => t.id == activeTenant?.tenantId)?.name || "No tenant selected....",
                onItemClick: (event) => tenantClicked(event),
                items: availableTenants!.map((tenant: Tenant) => {
                    return { id: `${tenant.id}`, text: tenant.name }
                })
            },
            {
                type: "menu-dropdown",
                text: `${session.radienUser.firstname} ${session.radienUser.lastname}`,
                description: session.radienUser.userEmail,
                iconName: "user-profile",
                onItemClick: (event) => itemClicked(event, session.radienUser.id),
                items: [
                    { id: "profile", text: "Profile" },
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

/*
const { session } = props;
    const [ activeTenant, setActiveTenant ] = useState<ActiveTenant>();
    const [ selectedTenant, setSelectedTenant ] = useState<string>("No tenant selected...");
    const { isLoading, data } = useAvailableTenants(session.radienUser!.id!);
    const [
        { isLoading: isLoadingRoles, data: rolesViewPermission, refetch: refetchRoles },
        { isLoading: isLoadingUser, data: usersViewPermission, refetch: refetchUser },
        { isLoading: isLoadingPermission, data: permissionViewPermission, refetch: refetchPermission },
    ] = useCheckPermissions(session.radienUser.id!, activeTenant?.id!);

    let utilities: TopNavigationProps.Utility[];

    const tenantClicked = async (event: CustomEvent<ButtonDropdownProps.ItemClickDetails>) => {
        const response: AxiosResponse = await updateActiveTenant(Number(event.detail.id), activeTenant!);
        console.log(response);
        if(response.status === 200) {
            const at: ActiveTenant = (await axios.get(`/api/tenant/activeTenant/getActiveTenant?userId=${session.radienUser.id}`)).data[0]
            setActiveTenant(at);

            setSelectedTenant(data.find((t: Tenant) => t.id == activeTenant?.tenantId).name);

            refetchPermission();
            refetchRoles();
            refetchUser();
        }
    }

    useEffect(() => {
        axios.get(`/api/tenant/activeTenant/getActiveTenant?userId=${session.radienUser.id}`)
            .then(result => {
                setActiveTenant(result.data[0]);
                if(!isLoading && activeTenant?.tenantId) {
                    setSelectedTenant(data.find((t: Tenant) => t.id == activeTenant?.tenantId).name);
                }
            });
        }, [session.radienUser.id, data, isLoading, activeTenant?.tenantId]);

    if(isLoading) {
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
                text: `${session.radienUser.firstname} ${session.radienUser.lastname}`,
                description: session.radienUser.userEmail,
                iconName: "user-profile",
                onItemClick: (event) => itemClicked(event, session.radienUser.id),
                items: [
                    { id: "profile", text: "Profile" },
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
    } else {
    utilities = [
        {
            type: "menu-dropdown",
            iconName: "multiscreen",
            ariaLabel: selectedTenant,
            text: selectedTenant,
            title: selectedTenant,
            onItemClick: (event) => tenantClicked(event),
            items: data.map((tenant: Tenant) => {
                        return { id: `${tenant.id}`, text: tenant.name }
                    })
        },
        {
            type: "menu-dropdown",
            text: `${session.radienUser.firstname} ${session.radienUser.lastname}`,
            description: session.radienUser.userEmail,
            iconName: "user-profile",
            onItemClick: (event) => itemClicked(event, session.radienUser.id),
            items: [
                { id: "profile", text: "Profile" },
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
        items: [
            {
                id: "settings-org",
                text: "Organizational settings"
            },
            {
                id: "settings-project",
                text: "Project settings"
            }
        ]
    };
    if(permissionViewPermission) {
        const item: ItemOrGroup = {
            id: "permissionManagement",
            text: "Permission Management",
            href: "/system/permissionManagement"
        };
        systemMenus.items = [item, ...systemMenus.items];
    }
    if(rolesViewPermission) {
        const item: ItemOrGroup = {
            id: "roleManagement",
            text: "Role Management",
            href: "/system/roleManagement"
        };
        systemMenus.items = [item, ...systemMenus.items];
    }
    if(usersViewPermission) {
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
 */