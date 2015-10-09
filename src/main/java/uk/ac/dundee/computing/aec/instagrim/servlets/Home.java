/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;

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
     * Displays the user's profile when they are authenticated
     */
    
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
            //profile = profile.substring(0, profile.indexOf("/"));
            
            request.getRequestDispatcher("/Profile/" + profile).forward(request, response);
        }
        else
        {
            HttpSession session = request.getSession(true);
            LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
            if (lg != null)
            {
                if (lg.getlogedin())
                {
                    String username = lg.getUsername();
                    request.getRequestDispatcher("/Profile/" + username).forward(request, response);
                }
            }
            else
            {
                rememberMe(request);
                
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
            
            request.getRequestDispatcher("/Profile/" + user.getUsername()).forward(request, response);
        }
        else
        {
            //Login failed
            request.setAttribute("loginFailed", "1");
            rememberMe(request);
            request.getRequestDispatcher("index.jsp").forward(request, response);
            //response.sendRedirect("/");
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
