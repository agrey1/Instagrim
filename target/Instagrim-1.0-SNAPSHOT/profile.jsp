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
                    <a href="/Instagrim/Image/<%=p.getSUUID()%>" ><img src="/Instagrim/Thumb/<%=p.getSUUID()%>"></a>
                    <%
                    
                    LinkedList<PicComment> picComments = p.getPicComments();
                    if(picComments != null)
                    {
                        %>
                        <div class="comments">
                            <%
                            for(PicComment comment : picComments)
                            {
                                %>
                                <p><a href="/Instagrim/<%=comment.getAuthor()%>"<%=comment.getAuthor()%></a> Test</p>
                                <%
                                System.out.println("1");
                                System.out.println("2");
                                System.out.println("3");
                            }
                            %>
                        </div>
                        <%
                    }
                
                %> </article> <%
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
