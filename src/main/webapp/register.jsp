<%-- 
    Document   : register.jsp
    Created on : Sep 28, 2014, 6:29:51 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%

String registerError = null;
registerError = (String)request.getAttribute("registerError");
String registerErrorShow = "";

if(registerError != null)
{
    registerErrorShow = "style=\"display:initial\"";
}
else
{
    registerError = "";
}

%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/footer.css" />
        <title>Instagrim</title>
    </head>
    <body>
        <header>
        <h1>InstaGrim!</h1>
        <h2>Your world in Black and White</h2>
        </header>
        
        <article>
            <h3>Register as user</h3>
            <form method="POST"  action="Register">
                <ul>
                    <p id="loginError" <%= registerErrorShow%>><%= registerError%><br></p>
                    <li>User Name <input type="text" name="username" required></li>
                    <li>Password <input type="password" name="password" required></li>
                    <li>Confirm Password <input type="password" name="password2" required></li>
                </ul>
                <br/>
                <input type="submit" value="Register"> 
            </form>

        </article>
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim">Home</a></li>
            </ul>
        </footer>
    </body>
</html>
