<#import "template.ftl" as layout>

<div class="card-pf">
    <div id="kc-content">
        <div id="kc-content-wrapper">

            <div class="container">

                <div id="loginbox">
    
                    <div class="panel-heading">
                        <div align="center">
                            <img src="${url.resourcesPath}/img/bg.png" class="login-logo-form"/>
                        </div>
                    </div>
    
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
    
                </div>
    
                <div id="kc-form-options" class="${properties.kcFormOptionsClass!} login-registration-link up-division-line">
                    <div class="${properties.kcFormOptionsWrapperClass!}">
                        <span><a href="${url.loginUrl}">${kcSanitize(msg("backToLogin"))?no_esc}</a></span>
                    </div>
                </div>
    
            </div>

        </div>
    </div>
</div>
<script>
    window.onload = function() {
        document.getElementById('kc-locale').style.display = 'none';
    };
</script>