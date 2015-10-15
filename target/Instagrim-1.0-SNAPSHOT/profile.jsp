<%-- 
    Document   : UsersPics
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>

<%
    
String profile = (String)request.getAttribute("profile");

String username = (String)request.getAttribute("username");
if(username == null)
{
    username = "Your Account";
}
    
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%
        if(profile == null)
        {
            %>
            <title>Instagrim</title>
            <%
        }
        else
        {
            %>
            <title>Instagrim - <%=profile%></title>
            <%
        }
        %>
        
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/profile.css" />
        <link rel="stylesheet" type="text/css" href="/Instagrim/css/footer.css" />
        <script type="text/javascript" src="js/jquery-2.1.4.js" ></script>
        <script type="text/javascript" src="js/comments.js" ></script>
    </head>
    <body>
        <header>
            <nav>
                <a href="/Instagrim">Instagrim</a>
                <a href="/Instagrim/upload.jsp">Upload</a>
                <form>
                    <input type="text" placeholder="Search for users"/>
                </form>
                <%
                if((boolean)request.getAttribute("loggedIn"))
                {
                    %>
                    <a href="/Instagrim/Edit"><%=username%></a>
                    <a href="/Instagrim/LogOut">Log Out</a>
                    <%
                }
                else
                {
                    %>
                    <a href="/Instagrim">Login</a>
                    <%
                }
                %>
            </nav>
        </header>
        
        <%
        
        java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
        if(profile == null)
        {
            %><p>This account has not been registered.</p><%
        }
        else if (lsPics == null)
        {
            %><p>This user has not uploaded any images.</p><%
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
                        <p>Added by <a href="/Instagrim/<%=profile%>"><%=profile%></a> at <%=p.getDatePostedStr()%></p>
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
                            
                            if(picComments.size() >= 5)
                            {
                                %>
                                <p><a href="/Instagrim/Image/<%=p.getSUUID()%>/Comments">See more comments</a></p>
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
                                    <span class="charCount"></span>
                                    <input type="text" maxlength="100" name="comment" class="postComment" placeholder="Post a comment"/>
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
