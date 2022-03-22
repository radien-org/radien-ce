<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=false displayMessage=false; section>
    <#if section = "title">
        <!--${msg("rd_loginTitle","Radien")}-->
    <#elseif section = "form">
        <#if realm.password>
            <div class="container">
                <div id="loginbox" style="background-color: #ffffff;box-shadow: 0 3px 9px #c1c1c1;border-radius: 10px;padding: 20%;margin-top: 30%;" align="center" class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
                    <div class="panel panel-info" >
                        <div class="panel-heading">
                            <div align="center">
                                <img src="/auth/resources/dq5ui/welcome/radien/bg.png" style="width: 100px;position: relative;bottom: 36px;">
                            </div>
                            <#if realm.displayName=='Keycloak'>
                                <div class="panel-title" style="font-size: 24px;padding-bottom: 20px;text-align: center;">
                                    Admin Access
                                </div>
                            <#elseif realm.displayName?lower_case?contains("radien")>
                                <div class="panel-title" style="font-size: 24px;padding-bottom: 20px;text-align: center;">
                                    Radien Client Login Test
                                </div>
                            </#if>
                            <#if realm.resetPasswordAllowed>
                                <div style="float:right; font-size: 80%; position: relative; top:-10px"><a href="${url.loginResetCredentialsUrl}">${msg("rd_doForgotPassword")}</a></div>
                            </#if>
                        </div>

                        <div class="panel-body" >
                            <#if message?has_content>
                                <div id="login-alert" class="alert alert-danger col-sm-12">
                                    <span class="kc-feedback-text">${kcSanitize(message.summary)?no_esc}</span>
                                </div>
                            </#if>

                            <form id="kc-form-login" class="${properties.kcFormClass!}" onsubmit="login.disabled = true; return true;" action="${url.loginAction?keep_after('^[^#]*?://.*?[^/]*', 'r')}" method="post">
                                <div class="${properties.kcInputWrapperClass!}">
                                    <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                                    <#if usernameEditDisabled??>
                                        <input style="padding: 5px;border: none;border-radius: 5px;margin-bottom: 20px;" tabindex="1" id="username" class="${properties.kcInputClass!}" name="username" value="${(login.username!'')}" type="text" disabled placeholder="<#if !realm.loginWithEmailAllowed>${msg("rd_username")}<#elseif !realm.registrationEmailAsUsername>${msg("rd_usernameOrEmail")}<#else>${msg("rd_email")}</#if>"/>
                                    <#else>
                                        <input style="padding: 5px;border: none;border-radius: 5px;margin-bottom: 20px;" tabindex="1" id="username" class="${properties.kcInputClass!}" name="username" value="${(login.username!'')}" type="text" autofocus autocomplete="off" placeholder="<#if !realm.loginWithEmailAllowed>${msg("rd_username")}<#elseif !realm.registrationEmailAsUsername>${msg("rd_usernameOrEmail")}<#else>${msg("rd_email")}</#if>" />
                                    </#if>
                                </div>

                                <div class="${properties.kcInputWrapperClass!}">
                                    <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
                                    <input style="padding: 5px;border: none;border-radius: 5px;margin-bottom: 20px;" tabindex="2" id="password" class="${properties.kcInputClass!}" name="password" type="password" autocomplete="off" placeholder="${msg("rd_password")}"/>
                                </div>

                                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                                    <#if realm.rememberMe && !usernameEditDisabled??>
                                        <div class="checkbox">
                                            <label>
                                                <#if login.rememberMe??>
                                                    <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox" tabindex="3" checked> ${msg("rd_rememberMe")}
                                                <#else>
                                                    <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox" tabindex="3"> ${msg("rd_rememberMe")}
                                                </#if>
                                            </label>
                                        </div>
                                    </#if>
                                </div>

                                <div id="kc-form-buttons" style="margin-top:10px" class="${properties.kcFormButtonsClass!}">
                                    <div class="${properties.kcFormButtonsWrapperClass!}">
                                        <input style="width: 100%;height: 30px;border: none;border-radius: 5px;background-color: #E7411D;color: #ffffff;opacity: 0.7;max-width: 180px;" tabindex="4" class="${properties.kcButtonClass!}" name="login" id="kc-login" type="submit" value="${msg("rd_doLogIn")}"/>
                                        <#if realm.password && social.providers??>
                                            <#list social.providers as p>
                                                <a href="${p.loginUrl}" id="zocial-${p.alias}" class="btn btn-primary">${msg("rd_doLogIn")} With ${p.displayName}</a>
                                            </#list>
                                        </#if>
                                    </div>
                                </div>

                                <#if realm.password && realm.registrationAllowed && !usernameEditDisabled??>
                                    <div class="form-group">
                                        <div class="col-md-12 control">
                                            <div style="border-top: 1px solid#888; padding-top:15px;" >
                                                ${msg("noAccount")}
                                                <a tabindex="6" href="${url.registrationUrl}" style="font-weight: bold;">
                                                    ${msg("rd_signup")}
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </#if>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </#if>
    </#if>
</@layout.registrationLayout>