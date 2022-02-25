<html>
<body>
${kcSanitize(msg("rd_passwordResetBodyHtml",link, linkExpiration, realmName, linkExpirationFormatter(linkExpiration),user.getFirstName()))?no_esc}
</body>
</html>