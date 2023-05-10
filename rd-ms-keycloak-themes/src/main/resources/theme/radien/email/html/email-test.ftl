<#import "template.ftl" as layout>
<@layout.emailLayout; section>
    <#if section = "subject">
        ${kcSanitize(msg("emailTestSubject"))?no_esc}
    <#elseif section = "body">
        ${kcSanitize(msg("emailTestBodyHtml",realmName))?no_esc}
    </#if>
</@layout.emailLayout>