<%-- 
    Document   : comments
    Created on : 12-Oct-2015, 20:36:51
    Author     : Alexander Grey
--%>

<%@page import="java.util.LinkedList"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.PicComment"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.Pic"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/profile.css" />
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/footer.css" />
        <title>Image Comments</title>
    </head>
    <body>
        <%
        Pic p = (Pic)request.getAttribute("picture");
        %>
        
        <article>
            <div class="imageContainer">
                <a href="/Instagrim/Image/<%=p.getSUUID()%>" ><img src="/Instagrim/Thumb/<%=p.getSUUID()%>"></a>
                <div class="comments">
                <%
                LinkedList<PicComment> picComments = p.getPicComments();
                if(picComments != null)
                {
                    for(PicComment comment : picComments)
                    {
                        %>
                        <p><a href="/Instagrim/<%=comment.getAuthor()%>"><%=comment.getAuthor()%></a> <%=comment.getCommentText()%></p>
                        <%
                    }
                }
                %>
                </div>
                <div class="postComment">
                    <%
                    if((boolean)request.getAttribute("loggedIn"))
                    {
                        %>
                        <form action="/Instagrim/<%=(String)request.getAttribute("author")%>" method="POST">
                            <input type="text" name="comment" placeholder="Post a comment"/>
                            <input type="hidden" name="picid" value="<%=p.getSUUID()%>"/>
                            <input type="submit" value="Post"/>
                        </form>
                        <%
                    }
                    else
                    {
                        %>
                        <p><a href="/Instagrim">Log in</a> to post a comment.</p>
                        <%
                    }
                    %>
                </div>
            </div>
        </article>
    </body>
</html>