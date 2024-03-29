import { NextApiRequest, NextApiResponse } from "next";
import axios, { AxiosResponse } from "axios";

export default async function getTranslationsByLanguage(req: NextApiRequest, res: NextApiResponse) {
    const { language } = req.query;

    const path = `${process.env.RADIEN_CMS_URL}/i18n/properties/${language}`;
    const result: AxiosResponse | undefined = await axios
        .get(path, {
            params: {
                application: "radien",
            },
        })
        .catch((e) => {
            console.log(e);
            return undefined;
        });

    res.status(200).json(result?.data);
}
