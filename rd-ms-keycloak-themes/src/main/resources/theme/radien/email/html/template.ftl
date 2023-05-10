<#macro emailLayout>
    <html lang="en">
    <head>
        <title></title>
        <style type="text/css">
            a {
                color: #5D5D9E;
            }
            a:hover {
                color: #B2B3B4;
            }
            body {
                width: 100%;
                min-height: 70px;
                max-width: 1024px;
                margin: 10px;
                display: block;
            }
            img {
                display: block; margin: auto;
            }
            #subject {
                display: block;
                text-align: center;
                font-size: 14pt;
                font-family: Segoe, 'Segoe UI', 'Helvetica Neue', sans-serif;
                font-weight: bold;
                margin-bottom: 1em;
            }
            #body {
                font-size: 12pt;
                font-family: Segoe, 'Segoe UI', 'Helvetica Neue', sans-serif;
                line-height: 1.5;
            }
            .content-wrapper {
                min-height: 70px;
                max-width: 1024px;
            }
            .footer {
                font-size: 10pt;
                font-family: Segoe, 'Segoe UI', 'Helvetica Neue', sans-serif;
                margin-top: 3em;
            }
        </style>
    </head>

        <body>
            <div class="content-wrapper">
                <img src="${properties.logoBase64!}"/>
                <br />
                <span id="subject"><#nested "subject"></span>
                <br />
                <span id="body"><#nested "body"></span>
                <center>
                    <div class="footer">&copy;&nbsp;radien.io <br /></div>
                </center>
            </div>
        </body>
    </html>
</#macro>
