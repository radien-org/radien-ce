import React, { useContext } from "react";
import { TopNavigationProps } from "@cloudscape-design/components/top-navigation";
import { useSession } from "next-auth/react";
import { ButtonDropdownProps } from "@cloudscape-design/components";
import { RadienContext } from "@/context/RadienContextProvider";
import { NextRouter, useRouter } from "next/router";
import LoggedOutHeader from "@/components/Header/LoggedOutHeader";
import LoggedInHeader from "@/components/Header/LoggedInHeader";

React.useLayoutEffect = React.useEffect;

export interface HeaderProps {
    topNavigationProps: TopNavigationProps;
    localeClicked: (router: NextRouter, event: CustomEvent<ButtonDropdownProps.ItemClickDetails>) => void;
    i18nStrings: TopNavigationProps.I18nStrings;
}

const i18nStrings = {
    searchIconAriaLabel: "Search",
    searchDismissIconAriaLabel: "Close search",
    overflowMenuTriggerText: "More",
    overflowMenuTitleText: "All",
    overflowMenuBackIconAriaLabel: "Back",
    overflowMenuDismissIconAriaLabel: "Close menu",
};

const localeClicked = async (router: NextRouter, event: CustomEvent<ButtonDropdownProps.ItemClickDetails>) => {
    const { pathname, asPath, query } = router;
    await router.push({ pathname, query }, asPath, { locale: event.detail.id });
};

export default function Header() {
    const { data: session } = useSession();
    const router = useRouter();
    const { isLoadingUserInSession } = useContext(RadienContext);

    const topNavigationProps: TopNavigationProps = {
        identity: {
            href: `${router.locale ? "/" + router.locale : ""}/`,
            logo: { src: "/top-navigation/trademark.svg", alt: "ra'di'en" },
        },
        i18nStrings,
    };

    if (!session || isLoadingUserInSession) {
        return <LoggedOutHeader topNavigationProps={topNavigationProps} localeClicked={localeClicked} i18nStrings={i18nStrings} />;
    } else {
        return <LoggedInHeader topNavigationProps={topNavigationProps} localeClicked={localeClicked} i18nStrings={i18nStrings} />;
    }
}
