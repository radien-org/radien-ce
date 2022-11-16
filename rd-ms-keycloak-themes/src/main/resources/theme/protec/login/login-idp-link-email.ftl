<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "header">
        ${msg("emailLinkIdpTitle", idpDisplayName)}
    <#elseif section = "form">
        <div id="loginbox" align="center">
            <div align="center">
                <img src="${url.resourcesPath}/img/ProTecSports_Logo_Wortmarke.png" class="login-logo-form"/>
            </div>
            <p id="instruction1" class="instruction">
                ${msg("emailLinkIdp1", idpDisplayName, brokerContext.username, realm.displayName)}
            </p>
            <p id="instruction2" class="instruction">
                ${msg("emailLinkIdp2")} <a href="${url.loginAction}">${msg("doClickHere")}</a> ${msg("emailLinkIdp3")}
            </p>
            <p id="instruction3" class="instruction">
                ${msg("emailLinkIdp4")} <a href="${url.loginAction}">${msg("doClickHere")}</a> ${msg("emailLinkIdp5")}
            </p>
        </div>

    </#if>
</@layout.registrationLayout>