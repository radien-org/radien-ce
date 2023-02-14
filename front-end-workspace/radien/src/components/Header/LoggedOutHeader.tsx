import { useRouter } from "next/router";
import React, { useContext } from "react";
import { RadienContext } from "@/context/RadienContextProvider";
import { signIn } from "next-auth/react";
import ThemeToggle from "@/components/Header/ThemeToggle";
import TopNavigation from "@cloudscape-design/components/top-navigation";
import { HeaderProps } from "@/components/Header/Header";

export default function LoggedOutHeader({ topNavigationProps, localeClicked, i18nStrings }: HeaderProps) {
    const router = useRouter();
    const { i18n } = useContext(RadienContext);

    topNavigationProps.utilities = [
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
            onItemClick: async (event) => localeClicked(router, event),
            items: router.locales
                ? router.locales.map((locale: string) => {
                      return { id: `${locale}`, text: locale };
                  })
                : [],
        },
        { type: "button", text: "", iconSvg: ThemeToggle() },
    ];

    return (
        <>
            <TopNavigation identity={topNavigationProps.identity} utilities={topNavigationProps.utilities} i18nStrings={i18nStrings} />
        </>
    );
}
