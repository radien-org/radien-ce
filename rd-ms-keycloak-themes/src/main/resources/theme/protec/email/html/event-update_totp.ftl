<html>
<body>
${kcSanitize(msg("eventUpdateTotpBodyHtml",event.date, event.ipAddress, user.getFirstName()))?no_esc}
</body>
</html>
