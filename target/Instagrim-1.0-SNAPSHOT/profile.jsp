<%-- 
    Document   : UsersPics
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/profile.css" />
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/footer.css" />
    </head>
    <body>
        <header>
            <nav>
                <a href="/Instagrim">Instagrim</a>
                <a href="/Instagrim/upload.jsp">Upload</a>
                <form>
                    <input type="text" placeholder="Search for users"/>
                </form>
                <a href="/Instagrim/edit">Account</a>
                <a href="/Instagrim/LogOut">Log Out</a>
            </nav>
        </header>
        
        <%
        java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
        if (lsPics == null)
        {
            %><p>No Pictures found</p><%
        }
        else
        {
            Iterator<Pic> iterator;
            iterator = lsPics.iterator();
            while (iterator.hasNext())
            {
                Pic p = (Pic) iterator.next();
                
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
                                <form method="POST">
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
                <%
            }
        }
        %>
        
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim">Home</a></li>
            </ul>
        </footer>
    </body>
</html>
