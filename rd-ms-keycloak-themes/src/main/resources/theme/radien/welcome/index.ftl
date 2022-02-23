<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
 
<html>
<head>
    <title>Radien SSO</title>

    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="robots" content="noindex, nofollow">

    <link rel="shortcut icon" href="${resourcesPath}/img/favicon.ico" />

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
</head>

<body>
<div class="container-fluid">
  <div class="row">
    <div class="col-sm-10 col-sm-offset-1 col-md-8 col-md-offset-2 col-lg-8 col-lg-offset-2">
      <div class="welcome-header">
        <img src="${resourcesPath}/logo.png" alt="Radien" border="0" />
        <h1>Welcome to Central Admin Page of <strong>Radien Single Sign On</strong></h1>
      </div>
      <div class="row">
        <div class="col-xs-12 col-sm-4">
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
            <div class="welcome-primary-link">
              <h3><a href="${adminUrl}"><img src="welcome-content/user.png">Admin Console <i class="fa fa-angle-right link" aria-hidden="true"></i></a></h3>
                <h3><a href="https://radien.atlassian.net/wiki/spaces/DA/pages/459407361/05+Keycloak"><img class="doc-img" src="welcome-content/admin-console.png">Documentation Area <i class="fa fa-angle-right link" aria-hidden="true"></i></a></h3>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
