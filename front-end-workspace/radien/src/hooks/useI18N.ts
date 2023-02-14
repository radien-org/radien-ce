import { useEffect, useState } from "react";
import { useRouter } from "next/router";
import { useQuery } from "react-query";
import { QueryKeys } from "@/consts";
import axios, { AxiosError } from "axios";

async function getTranslations(locale: string) {
    return await axios.get(`/api/i18n/getPropertiesByLanguage/${locale}`);
}

export default function useI18N() {
    const { locale } = useRouter();

    const { data, isLoading } = useQuery([QueryKeys.I18N, locale], async () => (await getTranslations(locale!)).data, { enabled: !!locale });

    return { data, isLoading };
}
