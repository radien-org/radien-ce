import React, {useContext} from 'react';
import {RadienContext} from "@/context/RadienContextProvider";

export default function Header() {
    const { i18n } = useContext(RadienContext);

    return <footer className="p-4 rounded-lg md:px-6 md:py-8 fixed bottom-0 w-full">
        <div className="sm:flex sm:items-center sm:justify-between">
            <img className="mx-[1rem] max-h-[2rem]" src='/footer/trademark_black.svg' alt="Extra large avatar" />
            <ul className="flex flex-wrap items-center mb-6 text-sm text-gray-500 sm:mb-0 text-gray-400">
                <li>
                    <a href="#" className="mr-4 hover:underline md:mr-6 ">{i18n?.about || "About"}</a>
                </li>
                <li>
                    <a href="#" className="mr-4 hover:underline md:mr-6">{i18n?.privacy_policy || "Privacy Policy"}</a>
                </li>
                <li>
                    <a href="#" className="mr-4 hover:underline md:mr-6 ">{i18n?.licensing || "Licensing"}</a>
                </li>
                <li>
                    <a href="#" className="hover:underline">{i18n?.contact || "Contact"}</a>
                </li>
            </ul>
        </div>
    </footer>
}