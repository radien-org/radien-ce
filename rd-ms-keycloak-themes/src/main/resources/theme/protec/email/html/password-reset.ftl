<html>
<body>
${kcSanitize(msg("passwordResetBodyHtml",link, linkExpiration, realmName, linkExpirationFormatter(linkExpiration),user.getFirstName()))?no_esc}
</body>
</html>