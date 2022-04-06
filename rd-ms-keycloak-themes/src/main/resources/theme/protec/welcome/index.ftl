<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
    <head>
        <title>Radien SSO</title>

        <meta charset="utf-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="robots" content="noindex, nofollow">

        <!--
            <link rel="shortcut icon" href="${resourcesPath}/img/favicon.ico" />
        -->

        <#if properties.stylesCommon?has_content>
            <#list properties.stylesCommon?split(' ') as style>
                <link href="${resourcesCommonPath}/${style}" rel="stylesheet" />
            </#list>
        </#if>
        <#if properties.styles?has_content>
            <#list properties.styles?split(' ') as style>
                <link href="${resourcesPath}/${style}" rel="stylesheet" />
            </#list>
        </#if>

        <link href="${resourcesPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet" />
        <script src="${resourcesPath}/bootstrap/js/bootstrap.bundle.min.js"></script>
    </head>

    <body>
        <div class="container">
            <div class="row index-data-content">
                
                <div class="col-sm-12 col-md-2 col-lg-2" style="padding-left: 0px;">

                    <div class="index-side-bar-container scrollDiv">
                        <div align="center">
                            <img src="${resourcesPath}/ProTecSports_Logo_Wortmarke.png" alt="Radien" border="0" style="width: 100px;" />
                        </div>
                        <div class="welcome-primary-link">
                            <h3 class="index-sidebar-menu-link">
                                <a href="${adminUrl}">
                                    <img class="index-sidebar-menu-img-icon" src="welcome-content/user.png">Admin Console
                                </a>
                            </h3>
                            <h3 class="index-sidebar-menu-link">
                                <a href="https://radien.atlassian.net/wiki/spaces/DA/pages/459407361/05+Keycloak" >
                                    <img class="index-sidebar-menu-img-icon" src="welcome-content/admin-console.png">Documentation Area
                                </a>
                            </h3>
                        </div>
                    </div>

                </div>

                <div class="col-sm-12 col-md-10 col-lg-10">

                    <div class="index-content-container scrollDiv">

                        <div class="index-header-title-bar">
                            <div class="welcome-header">
                                <h1 class="index-header-title">
                                    Welcome to Central Admin Page of <strong>Protec Single Sign On</strong>
                                </h1>
                            </div>
                        </div>
    
                        <div class="row">
                            <div class="col-lg-12 col-sm-12">
                                <div class="card-pf h-l" style="min-height: 100px;"></div>
                            </div>
                            <div class="col-lg-8 col-sm-12">
                                <div class="card-pf h-l"></div>
                            </div>
                            <div class="col-lg-4 col-sm-12">
                                <div class="card-pf h-l"></div>
                            </div>
                            <div class="col-lg-6 col-sm-12">
                                <div class="card-pf h-l">
                                    <#if successMessage?has_content>
                                        <p class="alert success">${successMessage}</p>
                                    <#elseif errorMessage?has_content>
                                        <p class="alert error">${errorMessage}</p>
                                        <h3><img src="welcome-content/user.png">Administration Console</h3>
                                    <#elseif bootstrap>
                                        <#if localUser>
                                            <h3><img src="welcome-content/user.png">Administration Console</h3>
                                            <p>Please create an initial admin user to get started.</p>
                                        <#else>
                                            <p class="welcome-message">
                                                <img src="welcome-content/alert.png">You need local access to create the initial admin user. <br><br>Open <a href="http://localhost:8080/auth">http://localhost:8080/auth</a>
                                                <br>or use the add-user-keycloak script.
                                            </p>
                                        </#if>
                                    </#if>
                        
                                    <#if bootstrap && localUser>
                                        <form method="post" class="welcome-form">
                                            <p>
                                                <label for="username">Radien Username</label>
                                                <input id="username" name="username" />
                                            </p>
                        
                                            <p>
                                                <label for="password">Password</label>
                                                <input id="password" name="password" type="password" />
                                            </p>
                        
                                            <p>
                                                <label for="passwordConfirmation">Password confirmation</label>
                                                <input id="passwordConfirmation" name="passwordConfirmation" type="password" />
                                            </p>
                        
                                            <input id="stateChecker" name="stateChecker" type="hidden" value="${stateChecker}" />
                        
                                            <button id="create-button" type="submit" class="btn btn-primary">Create</button>
                                        </form>
                                    </#if>
                                    
                                </div>
                            </div>
                            <div class="col-lg-6 col-sm-12">
                                <div class="card-pf h-l"></div>
                            </div>
                        </div>

                    </div>

                </div>

            </div>
        </div>
        <script>
            
        </script>
    </body>

</html>
