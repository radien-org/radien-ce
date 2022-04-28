<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "header">
        ${msg("emailVerifyTitle")}
    <#elseif section = "form">
        <p class="instruction">${msg("emailVerifyInstruction1")}</p>
    <#elseif section = "info">
        <p class="instruction">
            ${msg("emailVerifyInstruction2")} <a href="javascript:void(0)" onClick="window.location.reload()">${msg("doClickHere")}</a> ${msg("emailVerifyInstruction3")}
        </p>
    </#if>
</@layout.registrationLayout>