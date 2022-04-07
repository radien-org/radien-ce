<html>
<body>
${kcSanitize(msg("emailVerificationBodyHtml",link, linkExpiration, realmName, linkExpirationFormatter(linkExpiration), user.getFirstName()))?no_esc}
</body>
</html>
