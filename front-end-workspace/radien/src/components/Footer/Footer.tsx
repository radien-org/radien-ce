import React from 'react';

export default function Header() {
    return <footer className="p-4 rounded-lg md:px-6 md:py-8 fixed bottom-0 w-full">
        <div className="sm:flex sm:items-center sm:justify-between">
            <img className="mx-[1rem] max-h-[2rem]" src='/footer/trademark_black.svg' alt="Extra large avatar" />
            <ul className="flex flex-wrap items-center mb-6 text-sm text-gray-500 sm:mb-0 text-gray-400">
                <li>
                    <a href="#" className="mr-4 hover:underline md:mr-6 ">About</a>
                </li>
                <li>
                    <a href="#" className="mr-4 hover:underline md:mr-6">Privacy Policy</a>
                </li>
                <li>
                    <a href="#" className="mr-4 hover:underline md:mr-6 ">Licensing</a>
                </li>
                <li>
                    <a href="#" className="hover:underline">Contact</a>
                </li>
            </ul>
        </div>
    </footer>
}