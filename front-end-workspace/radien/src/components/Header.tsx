import React, {useEffect, useState} from 'react';
import TopNavigation, {TopNavigationProps} from "@cloudscape-design/components/top-navigation";
import {signIn, signOut, useSession} from "next-auth/react";
import axios, {AxiosResponse} from "axios";
import {ButtonDropdownProps} from "@cloudscape-design/components";
import {ActiveTenant, Tenant} from "radien";
import ItemOrGroup = ButtonDropdownProps.ItemOrGroup;
import {Session} from "next-auth";
import Utility = TopNavigationProps.Utility;

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

const getAvailableTenants = (userId: number)  => {
    return axios.get(`/api/role/tenantroleuser/getTenants?userId=${userId}`);
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
    const { session } = props;
    const [availableTenants, setAvailableTenants] = useState<ItemOrGroup[]>([]);
    const [activeTenant, setActiveTenant] = useState<ActiveTenant>();

    const [roleViewPermission, setRoleViewPermission] = useState<boolean>();
    const [userViewPermission, setUserViewPermission] = useState<boolean>();
    const [permissionViewPermission, setPermissionViewPermission] = useState<boolean>();

    let utilities: TopNavigationProps.Utility[];

    const tenantClicked = async (event: CustomEvent<ButtonDropdownProps.ItemClickDetails>) => {
        const response: AxiosResponse = await updateActiveTenant(Number(event.detail.id), activeTenant!);
        if(response.status === 200) {
            setActiveTenant(
                (await axios.get(`/api/tenant/activeTenant/getActiveTenant?userId=${session.radienUser.id}`)).data[0]
            )
        }
    }

    useEffect(() => {
        getAvailableTenants(session.radienUser.id!)
            .then(result => {
                const resultData: Tenant[] = result.data;
                setAvailableTenants(resultData.map(tenant => {
                        return {
                            id: `${tenant.id}`,
                            text: tenant.name,
                        }
                    })
                );
            });
        axios.get(`/api/tenant/activeTenant/getActiveTenant?userId=${session.radienUser.id}`)
            .then(result => {
                const tenantId = result.data[0].tenantId;
                const rolesPath = `/api/role/tenantrolepermission/hasPermission?userId=${session.radienUser.id}&resource=Roles&action=Read&tenantId=${tenantId}`;
                const userPath = `/api/role/tenantrolepermission/hasPermission?userId=${session.radienUser.id}&resource=User&action=Read&tenantId=${tenantId}`;
                const permissionPath = `/api/role/tenantrolepermission/hasPermission?userId=${session.radienUser.id}&resource=Permission&action=Read&tenantId=${tenantId}`;
                if(!activeTenant) {
                    setActiveTenant(result.data[0]);
                }
                axios.get(rolesPath)
                    .then(result => {
                        setRoleViewPermission(result.data);
                    });
                axios.get(userPath)
                    .then(result => {
                        setUserViewPermission(result.data);
                    });
                axios.get(permissionPath)
                    .then(result => {
                        setPermissionViewPermission(result.data);
                    });
            }).catch(e => console.log("Not active tenant available"));
    }, [activeTenant]);

    utilities = [
        {
            type: "menu-dropdown",
            ariaLabel: activeTenant ? availableTenants.find(t => t.id === String(activeTenant.tenantId))?.text : 'No tenant selected..',
            text: activeTenant ? availableTenants.find(t => t.id === String(activeTenant.tenantId))?.text : 'No tenant selected..',
            title: activeTenant ? availableTenants.find(t => t.id === String(activeTenant.tenantId))?.text : 'No tenant selected..',
            onItemClick: (event) => tenantClicked(event),
            items: availableTenants
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
    let systemMenus: Utility = {
        type: "menu-dropdown",
        iconName: "security",
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
    if(roleViewPermission) {
        const item: ItemOrGroup = {
            id: "roleManagement",
            text: "Role Management",
            href: "/system/roleManagement"
        };
        systemMenus.items = [item, ...systemMenus.items];
    }
    if(userViewPermission) {
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