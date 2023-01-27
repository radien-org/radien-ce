/** @type {import('next').NextConfig} */
const withTranspileModules = require('next-transpile-modules');
const withPlugins = require('next-compose-plugins');

const nextConfig = {
  reactStrictMode: true,
}

module.exports = withPlugins([
    withTranspileModules(['@cloudscape-design/components']),
  ], nextConfig);
