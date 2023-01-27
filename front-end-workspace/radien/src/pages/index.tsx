import Head from 'next/head'
import { Inter } from '@next/font/google'
import React from "react";


const inter = Inter({ subsets: ['latin'] })

export default function Home() {
  return (
    <>
      <Head>
        <title>radien</title>
        <meta name="description" content="Generated by create next app" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="icon" href="/favicon.ico" />
      </Head>
        <main>
            <div className="flex min-h-screen justify-center items-center">
                <div className="text-center">
                    <img className="h-[35rem] p-[2rem]" src='./index/radien.svg' alt="Extra large avatar" />
                </div>
            </div>
        </main>
    </>
  )
}
