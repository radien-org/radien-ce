import { Html, Head, Main, NextScript } from 'next/document'

export default function Document() {
  return (
      <Html lang="en">
          <Head />
          <head>
              <title>ra'di'en</title>
              <meta name="description" content="A rapid digitalization framework" />
              <meta name="viewport" content="width=device-width, initial-scale=1" />
              <link rel="icon" href="/index/radien.svg" />
          </head>
          <body>
              <Main />
              <NextScript />
          </body>
      </Html>
  )
}
