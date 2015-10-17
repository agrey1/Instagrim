<%-- 
    Document   : search
    Created on : 17-Oct-2015, 14:14:17
    Author     : Alexander Grey
--%>

<%@page import="java.util.LinkedList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
LinkedList<String> results = (LinkedList<String>)request.getAttribute("results");
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/footer.css" />
        <title>Instagrim - Search results</title>
    </head>
    <body>
        <a href="/Instagrim">Home</a>
        <h3>Search results</h3>
        
        <%
            if(results.isEmpty())
            {
                %>
                <p>There were no usernames matching your search.</p>
                <%
            }
            else
            {
                for(String user : results)
                {
                    %>
                    <a href="/Instagrim/<%=user%>"><%=user%></a><br>
                    <%
                }
            }
        %>
    </body>
</html>