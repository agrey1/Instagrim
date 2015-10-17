<%-- 
    Document   : settings
    Created on : 14-Oct-2015, 16:34:22
    Author     : Alexander Grey
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
            <label for="first">First name</label>
            <input type="text" name="first"/><br>
            <label for="last">Last name</label>
            <input type="text" name="last"/><br>
            <label for="picture">Profile picture</label>
            <input type="text" name="picture"/><br>
            <input type="submit" name="save" value="Save"/>
        </form>
        
        <a href="/Instagrim">Return to home</a>
    </body>
</html>
