<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "header">
        ${msg("emailLinkIdpTitle", idpDisplayName)}
    <#elseif section = "form">
        <div id="loginbox">
            <div>
                <img src="${url.resourcesPath}/img/ProTecSports_Logo_Wortmarke.png" class="login-logo-form"/>
            </div>
            <p id="instruction1" class="instruction generic-paragraph">
                ${msg("emailLinkIdp1", idpDisplayName, brokerContext.username, realm.displayName)}
            </p>
            <p id="instruction2" class="instruction generic-paragraph">
                ${msg("emailLinkIdp2")} <a href="${url.loginAction}">${msg("doClickHere")}</a> ${msg("emailLinkIdp3")}
            </p>
            <p id="instruction3" class="instruction generic-paragraph">
                ${msg("emailLinkIdp4")} <a href="${url.loginAction}">${msg("doClickHere")}</a> ${msg("emailLinkIdp5")}
            </p>
            <br/>
            <div id="kc-form-options" class="${properties.kcFormOptionsClass!} login-registration-link up-division-line">
                <div class="${properties.kcFormOptionsWrapperClass!}">
                    <span class="form-msg">${kcSanitize(msg("backToLogin"))?no_esc}<a href="${url.loginUrl}">${kcSanitize(msg("doLogIn"))?no_esc}</a></span>
                </div>
            </div>
        </div>

    </#if>
</@layout.registrationLayout>