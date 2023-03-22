<#import "template.ftl" as layout>
<@layout.emailLayout; section>
    <#if section = "subject">
        ${kcSanitize(msg("eventUpdatePasswordSubject"))?no_esc}
    <#elseif section = "body">
        ${kcSanitize(msg("eventUpdatePasswordBodyHtml",event.date, event.ipAddress, user.getFirstName()))?no_esc}
    </#if>
</@layout.emailLayout>