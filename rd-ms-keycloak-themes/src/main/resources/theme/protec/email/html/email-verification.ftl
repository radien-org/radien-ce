<!DOCTYPE html>
<html>
    <meta http-equiv="Content-Type" content="text/html charset=UTF-8" />
    <head>
        <link target="_blank" href="https://fonts.googleapis.com/css2?family=Dosis:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    </head>
    <body>
        <div style="box-sizing: border-box; background: #ECE9EB; border-radius: 2px; display: flex; flex-direction: column; align-items: center; padding-bottom: 3rem;">
            <div style="box-sizing: border-box; width: 100%; background: #E3EAEA; height: 10rem; display: flex; flex-direction: row; align-items: center; justify-content: center;">
                <img src="${url.resourcesPath}/img/ProTecSports_Logo_Wortmarke.png" style="box-sizing: border-box; width: 5rem; height: 5rem;  margin-top: -2rem;"/>
            </div>
            <div  style="box-sizing: border-box; margin-top: -2rem; background-color: white; border-radius: 2px; max-width: 500px; width: 100%; min-height: 20rem; padding: 2rem; font: normal normal normal 18px/24px Open Sans; letter-spacing: 0px; color: #3C3C3B; display: flex; flex-direction: column; align-items: flex-start;">
                <div style="box-sizing: border-box; align-self: center; font: normal normal bold 28px/36px Dosis; letter-spacing: 8.4px; color: #4A6767; text-transform: uppercase; opacity: 1;">
                    Welcome
                </div>
                <br style="box-sizing: border-box;">
                <p style="box-sizing: border-box;">
                    ${kcSanitize(msg("emailVerificationBodyHtml",link, linkExpiration, realmName, linkExpirationFormatter(linkExpiration), user.getFirstName()))?no_esc}
                </p>

                <br style="box-sizing: border-box;">
                <br style="box-sizing: border-box;">
                <p style="box-sizing: border-box;">Thank you, <br style="box-sizing: border-box;"> protect team</p>
            </div>
            <div style="box-sizing: border-box; max-width: 500px; width: 100%; display: flex; flex-direction: row; padding: 16px; align-items: center; justify-content: space-between;">
                <a href="www.protec-solution.de" style="box-sizing: border-box; text-decoration: none; font: normal normal bold 12px/15px Dosis; letter-spacing: 3.6px; color: #4A6767; text-transform: uppercase; opacity: 1;">
                    www.protec-solution.de
                </a>
                <li class="email-media" style="box-sizing: border-box; list-style: none; display: flex; flex-direction: row; align-items: center; gap: 5px;">
                    <img onclick="window.location.href='https://www.instagram.com/deine_unfallrente/'" src="${url.resourcesPath}/img/insta_icon.png" style="box-sizing: border-box; width: 20px; height: 20px; transition: all .2s; cursor: pointer;"/>
                    <img onclick="window.location.href='http://sssfacebook.com/DeineUnfallrente'" src="${url.resourcesPath}/img/face_icon.png" style="box-sizing: border-box; width: 20px; height: 20px; transition: all .2s; cursor: pointer;"/>
                    <img onclick="window.location.href='http://www.tiktok.com/@deine_unfallrente'" src="${url.resourcesPath}/img/thu_icon.png" style="box-sizing: border-box; width: 20px; height: 20px; transition: all .2s; cursor: pointer;"/>
                </li>
            </div>
        </div>
    </body>
</html>

