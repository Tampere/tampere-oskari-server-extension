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
    <link rel="icon" type="image/x-icon" href="${clientDomain}/Oskari/favicon.ico">
    <title>Oskari - Tampere login</title>
    <style>
        :root {
            /* Tampere Brand Colors */
            --warm-red: #eb5e58;
            --dark-warm-red: #c83e36;
            --steel-blue: #29549a;
            --dark-blue: #22437b;
            --light-petrol: #0074a4;
            --light-sky-blue: #88bce7;
            --aqua-blue: #39a7d7;
            --light-sand: #f1eeeb;
            --deep-gray: #3f3e3e;
            --dark-gray: #686872;
            --white: #ffffff;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: linear-gradient(135deg, var(--steel-blue) 0%, var(--dark-blue) 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .content {
            background: var(--white);
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
            width: 100%;
            max-width: 420px;
        }

        .logo-container {
            text-align: center;
            margin-bottom: 30px;
        }

        .logo-container img {
            max-width: 120px;
            height: auto;
        }

        h2 {
            color: var(--deep-gray);
            margin-bottom: 30px;
            text-align: center;
            font-size: 24px;
            font-weight: 600;
        }

        .oauth-link {
            display: block;
            text-align: center;
            padding: 14px 24px;
            background: var(--warm-red);
            color: var(--white);
            text-decoration: none;
            border-radius: 4px;
            font-weight: 600;
            font-size: 15px;
            transition: background 0.2s ease;
            margin-bottom: 20px;
        }

        .oauth-link:hover {
            background: var(--dark-warm-red);
        }

        .divider {
            display: flex;
            align-items: center;
            text-align: center;
            margin: 25px 0;
            color: var(--dark-gray);
        }

        .divider::before,
        .divider::after {
            content: '';
            flex: 1;
            border-bottom: 1px solid var(--light-sand);
        }

        .divider span {
            padding: 0 15px;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .login-form {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        label {
            font-size: 14px;
            font-weight: 600;
            color: var(--deep-gray);
        }

        input[type="text"],
        input[type="password"] {
            padding: 12px 16px;
            border: 2px solid var(--light-sand);
            border-radius: 4px;
            font-size: 15px;
            transition: all 0.2s ease;
            background: var(--white);
        }

        input[type="text"]:focus,
        input[type="password"]:focus {
            outline: none;
            border-color: var(--steel-blue);
            box-shadow: 0 0 0 3px rgba(41, 84, 154, 0.1);
        }

        button[type="submit"] {
            padding: 14px 24px;
            background: var(--steel-blue);
            color: var(--white);
            border: none;
            border-radius: 4px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.2s ease;
            margin-top: 10px;
        }

        button[type="submit"]:hover {
            background: var(--dark-blue);
        }

        .screenreader {
            position: absolute;
            left: -10000px;
            width: 1px;
            height: 1px;
            overflow: hidden;
        }

        @media (max-width: 480px) {
            .content {
                padding: 30px 20px;
            }
        }
    </style>
</head>
<body>

<div class="content">
    <div class="logo-container">
        <img src="${pageContext.request.contextPath}/Oskari/logo/Vaakuna_Tampere.png" alt="Tampereen vaakuna"/>
    </div>

    <h2>Kirjaudu sisään</h2>

    <a href="${pageContext.request.contextPath}/oauth2" class="oauth-link">
        Kirjaudu TRE tunnuksilla
    </a>

    <div class="divider">
        <span>tai</span>
    </div>

    <form class="login-form" method="post" action="${pageContext.request.contextPath}/j_security_check">
        <div class="form-group">
            <label for="j_username">
                <spring:message code="username" text="Käyttäjätunnus"/>
            </label>
            <input type="text" id="j_username" name="j_username" required autofocus/>
        </div>

        <div class="form-group">
            <label for="j_password">
                <spring:message code="password" text="Salasana"/>
            </label>
            <input type="password" id="j_password" name="j_password" required/>
        </div>

        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <button type="submit" class="primary">Kirjaudu</button>
    </form>
</div>

</body>
</html>
