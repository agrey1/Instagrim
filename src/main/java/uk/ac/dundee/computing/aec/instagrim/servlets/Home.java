/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.PicComment;

/**
 *
 * @author Alexander Grey
 */
@WebServlet(name = "Home", urlPatterns = {"", "/"})

public class Home extends HttpServlet
{
    Cluster cluster = null;
    User user;

    public void init(ServletConfig config) throws ServletException 
    {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
        user = new User();
    }
    
    /**
     * Remember a user's username if their cookie exists
     * @param request The request to modify 
     */
    private void rememberMe(HttpServletRequest request)
    {
        String username = user.getCookieUsername(request); 
        if(username != null)
        {
            request.setAttribute("username", username);
        }
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        System.out.println("Home GET");
        System.out.println(request.getServletPath());
        
        String profile = request.getServletPath().trim().toLowerCase();
        if(profile.length() > 1) //The user is requesting a profile
        {
            profile = profile.substring(1, profile.length());
            
            request.getRequestDispatcher("/Profile/" + profile).forward(request, response);
        }
        else
        {
            if(User.isLoggedIn(request) == true)
            {
                String username = ((LoggedIn)request.getSession().getAttribute("LoggedIn")).getUsername();
                request.getRequestDispatcher("/Profile/" + username).forward(request, response);
            }
            else
            {
                rememberMe(request);
                
                //When referred from a profile page, the user should be sent back to this profile.
                String referrer = request.getHeader("referer");
                
                if(referrer != null)
                {
                    String[] referrerParts = referrer.split("/");
                    referrer = referrerParts[referrerParts.length - 1];
                    
                    if(referrer.equals("logout.jsp") || referrer.equals("Register") || referrer.equals("Instagrim"))
                    {
                        referrer = "";
                    }
                }
                else
                {
                    referrer = "";
                }
                
                request.setAttribute("referrer", referrer);
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        String profile = null;
        String args[] = Convertors.SplitRequestPath(request);
        if(args.length > 1)
        {
            profile = args[1];
        }
        
        String commentText = request.getParameter("comment");
        if(commentText != null) //The user is posting a comment
        {
            HttpSession session = request.getSession();
            LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
            
            //Users must be logged in to post a comment.
            //If the comment is more than 100 characters long, we ignore the request.
            //This would indicate that the HTML element has been modified by the visitor.
            if(User.isLoggedIn(request) == true && commentText.length() <= 100)
            {
                PicComment comment = new PicComment(lg.getUsername(), commentText);
                PicModel picModel = new PicModel(cluster);
                picModel.addComment(java.util.UUID.fromString(request.getParameter("picid")), comment);
                
                if(profile == null) profile = lg.getUsername();
            }
            
            request.getRequestDispatcher("/Profile/" + profile).forward(request, response);
        }
        else
        {
            System.out.println("Home POST");
            
            user.setUsername(request.getParameter("username"));
            user.setPassword(request.getParameter("password"));
            user.setCluster(cluster);
            
            HttpSession session = request.getSession(true);
            System.out.println("BEFORE LOGIN " + session);
            
            if(user.IsValidUser())
            {
                //Successful login
                LoggedIn lg = new LoggedIn();
                lg.setLogedin();
                lg.setUsername(user.getUsername());
                System.out.println("Successful login");
                //request.setAttribute("LoggedIn", lg);
                session.setAttribute("LoggedIn", lg);
                System.out.println("LOGGED IN " + session);
                System.out.println("Username: " + user.getUsername());

                if(request.getParameter("remember") != null)
                {
                    user.setCookie(response); //Remember me
                }
                else
                {
                    //Remove any existing cookies if the user does not wish to be remembered
                    user.removeCookie(request, response);
                }

                //Send the user to the correct profile if they came from someone else's profile
                String sendTo = user.getUsername();
                if(profile != null)
                {
                    sendTo = profile;
                }

                request.getRequestDispatcher("/Profile/" + sendTo).forward(request, response);
            }
            else
            {
                //Login failed
                request.setAttribute("loginFailed", "1");
                rememberMe(request);
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() 
    {
        return "Short description";
    }// </editor-fold>
}