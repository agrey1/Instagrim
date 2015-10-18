<%-- 
    Document   : settings
    Created on : 14-Oct-2015, 16:34:22
    Author     : Alexander Grey
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%

String error = null;
String style = null;
error = (String)request.getAttribute("error");

if(error != null)
{
    style = "style=\"display:initial\"";
}
else
{
    style = "";
}

String first = null;
String last = null;
String picture = null;

first = (String)request.getAttribute("first");
last = (String)request.getAttribute("last");
picture = (String)request.getAttribute("picture");

if(first == null) first = "";
if(last == null) last = "";
if(picture == null) picture = "";

%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/footer.css" />
        <title>Instagrim - Preferences</title>
    </head>
    <body>
        <h3>Profile settings</h3>
        <form method="POST">
            <p class="error" <%=style%>><%=error%><br></p>
            <label for="first">First name</label>
            <input type="text" name="first" value="<%=first%>"/><br>
            <label for="last">Last name</label>
            <input type="text" name="last" value="<%=last%>"/><br>
            <label for="picture">Profile picture</label>
            <input type="text" name="picture" value="<%=picture%>"/><br>
            <input type="submit" name="save" value="Save"/>
        </form>
        
        <p>Click <a href="/Instagrim/DeleteAccount">here</a> to delete your profile. This action cannot be reversed.</p>
        <a href="/Instagrim">Return to home</a>
    </body>
</html>