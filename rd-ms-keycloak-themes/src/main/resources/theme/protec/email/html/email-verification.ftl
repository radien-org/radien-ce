<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" class="${properties.kcHtmlClass!}">

<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="robots" content="noindex, nofollow">
</head>

<body>
    <div style="width: 100%;height: 600px;">
        <div style="width: 100%;height: 300px;background-color: #e1eaea;">

        </div>
        <div style="width: 100%;min-height: 600px;background-color: #f9f9f9;">
            ${kcSanitize(msg("emailVerificationBodyHtml",link, linkExpiration, realmName, linkExpirationFormatter(linkExpiration), user.getFirstName()))?no_esc}
        </div>
        <div style="width: 400px;height: 800px;background-color: #ffffff;">

        </div>
    </div>
</body>
</html>
