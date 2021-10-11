<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>welcome to radien micro service</title>
</head>
<body>
<script>
    function getContextPath() {
        return window.location.pathname.substring(0,
            window.location.pathname.indexOf("/", 2));
    }

    window.location.replace(getContextPath() + '/public/home');
</script>
</body>
</html>