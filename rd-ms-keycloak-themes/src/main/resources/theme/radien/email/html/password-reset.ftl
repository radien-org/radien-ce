<#import "template.ftl" as layout>
<@layout.emailLayout; section>
    <#if section = "subject">
        ${kcSanitize(msg("passwordResetSubject"))?no_esc}
    <#elseif section = "body">
        ${kcSanitize(msg("passwordResetBodyHtml",link, linkExpiration, realmName, linkExpirationFormatter(linkExpiration),user.getFirstName()))?no_esc}
    </#if>
</@layout.emailLayout>