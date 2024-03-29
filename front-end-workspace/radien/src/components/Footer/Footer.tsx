import React, { useContext } from "react";
import { RadienContext } from "@/context/RadienContextProvider";
import Link from "next/link";
import { useRouter } from "next/router";
import Image from "next/image";

export default function Footer() {
    const { i18n } = useContext(RadienContext);
    const { locale } = useRouter();

    return (
        <footer className="p-4 rounded-lg md:px-6 md:py-8 fixed bottom-0 w-full">
            <div className="sm:flex sm:items-center sm:justify-between">
                <Image className="mx-[1rem]" src="/footer/trademark_black.svg" height={20} width={85} alt="Extra large avatar" />
                <ul className="flex items-end mb-6 text-sm text-gray-500 sm:mb-0 text-gray-400">
                    <li>
                        <Link href="/" locale={locale} className="mr-4 hover:underline md:mr-6 ">
                            {i18n?.about || "About"}
                        </Link>
                    </li>
                    <li>
                        <Link href="/" locale={locale} className="mr-4 hover:underline md:mr-6">
                            {i18n?.privacy_policy || "Privacy Policy"}
                        </Link>
                    </li>
                    <li>
                        <Link href="/" locale={locale} className="mr-4 hover:underline md:mr-6 ">
                            {i18n?.licensing || "Licensing"}
                        </Link>
                    </li>
                    <li>
                        <Link href="/" locale={locale} className="hover:underline">
                            {i18n?.contact || "Contact"}
                        </Link>
                    </li>
                </ul>
            </div>
        </footer>
    );
}
