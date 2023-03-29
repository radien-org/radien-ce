const withTM = require("next-transpile-modules")(["@cloudscape-design/components"]);

/** @type {import('next').NextConfig} */
const nextConfig = {
    reactStrictMode: true,
    swcMinify: true,
    i18n: {
        localeDetection: true,
        defaultLocale: "en",
        locales: ["en", "de", "pt"],
    },
};

const buildConfig = (_phase) => {
    const plugins = [withTM];
    const config = plugins.reduce((acc, next) => next(acc), {
        ...nextConfig,
    });
    return { ...config, output: "standalone" };
};

module.exports = buildConfig();
