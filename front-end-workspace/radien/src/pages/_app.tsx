import '@/styles/globals.css'
import "@cloudscape-design/global-styles/index.css"
import type { AppProps } from 'next/app'
import {SessionProvider} from "next-auth/react";
import Header from "@/components/Header";
import Footer from "@/components/Footer/Footer";

export default function App({ Component, pageProps: { session, ...pageProps} }: AppProps) {
  return (
      <SessionProvider session={session} >
        <Header />
        <Component {...pageProps} />
        <Footer />
      </SessionProvider>
    )
}
