<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "header">
        ${msg("emailLinkIdpTitle", idpDisplayName)}
    <#elseif section = "form">
        <div id="loginbox">
            <div>
                <img src="${url.resourcesPath}/img/ProTecSports_Logo_Wortmarke.png" class="login-logo-form"/>
            </div>
            <div id="instruction1" class="instruction generic-paragraph">
                ${msg("emailLinkIdp1", idpDisplayName, brokerContext.username, realm.displayName)}
            </div>
            <br>
            <br>
            <div id="instruction2" class="instruction generic-paragraph">
                <p>${msg("emailLinkIdp2")}</p>
                <p><a href="${url.loginAction}">${msg("doClickHereVerif")}</a> ${msg("emailLinkIdp3")}</p>
            </div>
            <br>
            <br>
            <div id="kc-form-options" class="${properties.kcFormOptionsClass!} login-registration-link up-division-line">
                <div class="${properties.kcFormOptionsWrapperClass!}">
                    <span class="form-msg">${kcSanitize(msg("backToLogin"))?no_esc}<a href="${url.loginUrl}">${kcSanitize(msg("doLogIn"))?no_esc}</a></span>
                </div>
            </div>
        </div>

    </#if>
</@layout.registrationLayout>