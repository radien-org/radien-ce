import '@/styles/globals.css'
import "@cloudscape-design/global-styles/index.css"
import type { AppProps } from 'next/app'
import {SessionProvider} from "next-auth/react";
import Header from "@/components/Header/Header";
import Footer from "@/components/Footer/Footer";
import {QueryClient, QueryClientProvider} from "react-query";
import React from "react";

export default function App({ Component, pageProps: { session, ...pageProps} }: AppProps) {
    const queryClient: QueryClient = new QueryClient();
  return (
      <SessionProvider session={session} >
          <QueryClientProvider client={queryClient}>
            <Header />
            <Component {...pageProps} />
            <Footer />
          </QueryClientProvider>
      </SessionProvider>
    )
}
