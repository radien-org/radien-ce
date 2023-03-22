<#outputformat "plainText">
<#assign requiredActionsText><#if requiredActions??><#list requiredActions><#items as reqActionItem>${msg("requiredAction.${reqActionItem}")}<#sep>, </#sep></#items></#list></#if></#assign>
</#outputformat>

<#import "template.ftl" as layout>
<@layout.emailLayout; section>
    <#if section = "subject">
        ${kcSanitize(msg("executeActionsSubject"))?no_esc}
    <#elseif section = "body">
        ${kcSanitize(msg("executeActionsBodyHtml",link, linkExpiration, realmName, requiredActionsText, linkExpirationFormatter(linkExpiration), user.getFirstName()))?no_esc}
    </#if>
</@layout.emailLayout>