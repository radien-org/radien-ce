import '@/styles/globals.css'
import '@/styles/Loader.styles.scss'
import "@cloudscape-design/global-styles/index.css"
import type { AppProps } from 'next/app'
import {SessionProvider} from "next-auth/react";
import Header from "@/components/Header/Header";
import Footer from "@/components/Footer/Footer";
import {QueryClient, QueryClientProvider} from "react-query";
import React, {useState} from "react";
import {handleErrorMessage, handleInfoMessage, handleSuccessMessage, handleWarningMessage, FlashbarContext} from "@/context/FlashbarContext";
import {FlashbarProps} from "@cloudscape-design/components";
import FlashbarComponent from "@/components/Flashbar/FlashbarComponent";


export default function App({ Component, pageProps: { session, ...pageProps} }: AppProps) {
    const queryClient: QueryClient = new QueryClient();
    const [messages, setMessages] = useState<FlashbarProps.MessageDefinition[]>([]);

    const addSuccessMessage = (message: string) => {
        handleSuccessMessage(messages, setMessages, message);
    }
    const addInfoMessage = (message: string) => {
        handleInfoMessage(messages, setMessages, message);
    }
    const addWarningMessage = (message: string) => {
        handleWarningMessage(messages, setMessages, message);
    }
    const addErrorMessage = (message: string) => {
        handleErrorMessage(messages, setMessages, message);
    }

  return (
      <SessionProvider session={session} >
          <QueryClientProvider client={queryClient}>
              <FlashbarContext.Provider value={{values: messages, addSuccessMessage, addInfoMessage, addWarningMessage, addErrorMessage}}>
                <Header />
                <FlashbarComponent />
                <Component {...pageProps} />
                <Footer />
              </FlashbarContext.Provider>
          </QueryClientProvider>
      </SessionProvider>
    )
}
