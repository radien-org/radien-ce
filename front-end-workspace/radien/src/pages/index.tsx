import { Inter } from '@next/font/google'
import Image from "next/image";
import React from "react";
import {useSession} from "next-auth/react";
import ServiceDashboard from "@/components/ServiceDashboard/ServiceDashboard";


const inter = Inter({ subsets: ['latin'] })

export default function Home() {
    const { data: session } = useSession();
  return (
    <main>
        { !session &&
        <div className="flex justify-center items-center">
            <div className="text-center">
                <Image className="h-[35rem] my-[25%] p-[2rem]" src='./index/radien.svg' height={350} width={100} alt="Extra large avatar" />
            </div>
        </div>  }
        { session && <ServiceDashboard />}
    </main>
  )
}
