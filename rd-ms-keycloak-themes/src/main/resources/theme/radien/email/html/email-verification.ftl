<html>
<body>
${kcSanitize(msg("rd_emailVerificationBodyHtml",link, linkExpiration, realmName, linkExpirationFormatter(linkExpiration), user.getFirstName()))?no_esc}
</body>
</html>
