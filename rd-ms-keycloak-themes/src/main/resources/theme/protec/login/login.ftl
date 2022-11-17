<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=false displayMessage=false; section>
    <#if section = "title">
        <div style="display: none;">${msg("loginTitle", "Pro:Tec")}</div>
    <#elseif section = "form">
        <#if realm.password>
            <div class="container">
                <div id="loginbox" align="center" class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
                    <div class="panel panel-info">
                        
                        <div class="panel-heading">
                            <div align="center" style="bottom:60px">
                                <img src="${url.resourcesPath}/img/ProTecSports_Logo_Wortmarke.png" class="login-logo-form"/>
                            </div>
                            <#if realm.displayName=='Keycloak'>
                            <#else>
                            </#if>
                        </div>

                        <div class="panel-body">

                            <#if message?has_content>
                                <div id="login-alert" class="alert alert-danger col-sm-12">
                                    <span class="kc-feedback-text">
                                        ${kcSanitize(message.summary)?no_esc}
                                    </span>
                                </div>
                            </#if>

                            <form 
                                id="kc-form-login" 
                                class="${properties.kcFormClass!}" 
                                onsubmit="login.disabled = true; return true;" 
                                action="${url.loginAction?keep_after('^[^#]*?://.*?[^/]*', 'r')}" 
                                method="post"
                            >
                                <div class="${properties.kcInputWrapperClass!}">
                                    <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                                    <#if usernameEditDisabled??>
                                        <input tabindex="1" id="username" class="${properties.kcInputClass!}" name="username" value="${(login.username!'')}" type="text" disabled placeholder="<#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if>"/>
                                    <#else>
                                        <input tabindex="1" id="username" class="${properties.kcInputClass!}" name="username" value="${(login.username!'')}" type="text" autofocus autocomplete="off" placeholder="<#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if>" />
                                    </#if>
                                </div>

                                <div class="${properties.kcInputWrapperClass!}">
                                    <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
                                    <input tabindex="2" id="password" class="${properties.kcInputClass!}" name="password" type="password" autocomplete="off" placeholder="${msg("password")}"/>
                                </div>

                                <div id="kc-form-options" class="${properties.kcFormOptionsClass!} login-form-option-content">
                                    <#if realm.rememberMe && !usernameEditDisabled??>
                                        <div class="form-group-checkmark">
                                            <#if login.rememberMe??>
                                                <input tabindex="3" id="checkbox" name="checkbox" type="checkbox"  tabindex="3" checked>
                                            <#else>
                                                <input tabindex="3" id="checkbox" name="checkbox" type="checkbox"  tabindex="3" >
                                            </#if>
                                            <label class="form-label-checkmark" for="checkbox">
                                                <div class="checkmark-container">
                                                    <div class="checkmark-container--inner">
                                                    </div>
                                                    <div class="checkmark-sign-container"> </div>
                                                </div>
                                                ${msg("rememberMe")}
                                            </label>
                                        </div>
                                    </#if>
                                </div>

                                <div id="kc-form-buttons" style="margin-top:10px" class="${properties.kcFormButtonsClass!}">
                                    <div class="${properties.kcFormButtonsWrapperClass!}">
                                        <input tabindex="4" class="${properties.kcButtonClass!}" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>
                                        <#if realm.password && social.providers??>
                                            <#list social.providers as p>
                                                <a href="${p.loginUrl}" id="zocial-${p.alias}" class="btn btn-primary">${msg("doLogIn")} With ${p.displayName}</a>
                                            </#list>
                                        </#if>
                                    </div>
                                </div>

                            </form>
                            <#if realm.password && realm.registrationAllowed && !usernameEditDisabled??>
                                <div class="form-group">
                                    <div class="col-md-12 control">
                                        <div class="login-registration-link up-division-line" style="font-family: Open Sans, sans-serif !important">
                                            ${msg("noAccount")}
                                            <a tabindex="6" href="${url.registrationUrl}">
                                                ${msg("doRegister")}.
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </#if>
                            <#if realm.resetPasswordAllowed>
                                <div class="link-reset-password"style="font-weight:100 !important">
                                    <a href="${url.loginResetCredentialsUrl}">
                                        ${msg("doForgotPassword")}
                                    </a>
                                </div>
                            </#if>
                        </div>
                    </div>
                </div>
            </div>
        </#if>
    </#if>
</@layout.registrationLayout>