<%-- 
    Document   : index
    Created on : Sep 28, 2014, 7:01:44 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>

<%

String username = (String)request.getAttribute("username");
String checked = "checked";
String referrer = (String)(request.getAttribute("referrer"));

if(username == null) 
{
    username = "";
    checked = "";
}

String loginFailed = null;
loginFailed = (String)request.getAttribute("loginFailed");

if(loginFailed != null)
{
    if(loginFailed.equals("1"))
    {
        loginFailed = "style=\"display:initial\"";
    }
}
else
{
    loginFailed = "";
}

%>

<!DOCTYPE html>

<html>
    <head>
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/home.css" />
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/footer.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    
    <body>
        <div id="outerContainer">
            <div id="wrapper">
                <img src="/Instagrim/images/tree.jpg" alt="tree"/>
                <div id="loginContainer">
                    <h1>Instagrim</h1>
                    <form action="/Instagrim/<%=referrer%>" method="POST">
                        <p class="error" <%= loginFailed%>>Incorrect username or password, please try again.<br></p>
                        <label for="username">Username</label>
                        <input type="text" name="username" value="<%= username%>"/><br>
                        
                        <label for="password">Password</label>
                        <input type="password" name="password"/><br>
                        
                        <input type="submit" name="login" value="Login"/>
                        <input type="checkbox" name="remember" <%= checked%>/>
                        <label for="remember">Remember me</label>
                        
                        <p>No account? Click <a href="/Instagrim/Register">here</a> to register.</p>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>