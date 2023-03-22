<#import "template.ftl" as layout>
<@layout.emailLayout; section>
    <#if section = "subject">
        ${kcSanitize(msg("emailVerificationSubject"))?no_esc}
    <#elseif section = "body">
        ${kcSanitize(msg("emailVerificationBodyCodeHtml",code, user.getFirstName()))?no_esc}
    </#if>
</@layout.emailLayout>