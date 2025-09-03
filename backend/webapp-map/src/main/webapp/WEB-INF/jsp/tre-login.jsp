<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="format-detection" content="telephone=no"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Oskari - Tampere login</title>
</head>
<body>

<div>
    <a href="${pageContext.request.contextPath}/oauth2">Kirjaudu TRE tunnuksilla</a>
</div>
<hr/>

<div class="content">
    <form class="login-form" method="post" action="${pageContext.request.contextPath}/j_security_check">
        <h2>Please sign in</h2>

        <p>
            <label for="j_username" class="screenreader"><spring:message code="username" text="Username"/></label>
            <input type="text" id="j_username" name="j_username" required autofocus/>
        </p>
        <p>
            <label for="j_password" class="screenreader"><spring:message code="password" text="Password"/></label>
            <input type="password" id="j_password" name="j_password" required/>
        </p>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <button type="submit" class="primary">Kirjaudu</button>
    </form>

</div>
</body>
</html>
