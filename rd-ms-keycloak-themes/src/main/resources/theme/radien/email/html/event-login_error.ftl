<#import "template.ftl" as layout>
<@layout.emailLayout; section>
    <#if section = "subject">
        ${kcSanitize(msg("eventLoginErrorSubject"))?no_esc}
    <#elseif section = "body">
        ${kcSanitize(msg("eventLoginErrorBodyHtml",event.date,event.ipAddress, user.getFirstName()))?no_esc}
    </#if>
</@layout.emailLayout>