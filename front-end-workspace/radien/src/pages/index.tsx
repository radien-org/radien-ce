import { Inter } from "@next/font/google";
import Image from "next/image";
import React from "react";
import { useSession } from "next-auth/react";
import ServiceDashboard from "@/components/ServiceDashboard/ServiceDashboard";
import { Spinner } from "@cloudscape-design/components";

const inter = Inter({ subsets: ["latin"] });

export default function Home() {
    const { data: session, status } = useSession();
    if (status === "loading") {
        return <Spinner />;
    }
    return (
        <main>
            {!session && (
                <div className="flex justify-center items-center">
                    <div className="text-center">
                        <Image className="my-[25%] p-[2rem]" src="./index/radien.svg" height={800} width={600} alt="Extra large avatar" />
                    </div>
                </div>
            )}
            {session && <ServiceDashboard />}
        </main>
    );
}
