import React from 'react';

interface CardProps {
    title: string,
    description: string,
    href: string
}

export default function Card(props : CardProps) {
    const { title, description, href } = props;

    return (
        <div className="my-1 px-1 w-full md:w-1/2 lg:my-4 lg:px-4 lg:w-1/3">
            <a href={href}>
            <article className="overflow-hidden rounded-lg shadow-lg bg-[#faf7f7] text-[#00142b] hover:bg-[#00142b] hover:text-[#faf7f7]">
                <header className="flex items-center justify-between leading-tight p-2 md:p-4">
                    <h1 className="text-xxl font-semibold">
                            {title}
                    </h1>
                </header>
                <footer className="flex items-center justify-between leading-none md:p-4">
                    <p className="text-sm">
                        {description}
                    </p>
                </footer>
            </article>
            </a>
        </div>
);
}