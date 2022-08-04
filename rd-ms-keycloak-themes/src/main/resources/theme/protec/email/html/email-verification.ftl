<body>
    <div style="width: 100%;height: 600px;">
        <div style="width: 100%;height: 300px;background-color: #e1eaea;">

        </div>
        <div style="width: 100%;min-height: 600px;background-color: #f9f9f9;">
            <div align="center">
                <img 
                    src="${url.resourcesPath}/img/ProTecSports_Logo_Wortmarke.png" 
                    style="width: 100px;position: relative;bottom: 36px;"/>
                <h1 style="text-align: center;color: #416867;">Welcome</h1>
                ${kcSanitize(msg("emailVerificationBodyHtml",link, linkExpiration, realmName, linkExpirationFormatter(linkExpiration), user.getFirstName()))?no_esc}
                <p>Thanks you,</p>
                <p>Your Pro:Tec Solutions Team</p>
            </div>
        </div>
        <div style="width: 400px;height: 800px;background-color: #ffffff;">
            <div align="center">
                <table border="0">
                    <tr>
                        <td>
                            <p tyle="text-align: left;color: #416867;padding-top: 10px">WWW.PROTEC-SOLUTIONS.DE</p>
                        </td>
                        <td>
                            <img onclick="window.location.href='#'" src="${url.resourcesPath}/img/insta_icon.png" style="width: 20px;height: 20px;float: left;cursor: pointer;"/>
                            <img onclick="window.location.href='#'" src="${url.resourcesPath}/img/face_icon.png" style="width: 20px;height: 20px;float: left;cursor: pointer;"/>
                            <img onclick="window.location.href='#'" src="${url.resourcesPath}/img/thu_icon.png" style="width: 20px;height: 20px;float: left;cursor: pointer;"/>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</body>
</html>
