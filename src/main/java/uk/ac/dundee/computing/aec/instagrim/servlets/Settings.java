/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import exceptions.InvalidImageException;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;

/**
 *
 * @author Alexander Grey
 */
@WebServlet(name = "Settings", urlPatterns = {"/Settings"})
public class Settings extends HttpServlet 
{
    Cluster cluster = null;
    @Override
    public void init(ServletConfig config) throws ServletException 
    {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
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
        if(User.isLoggedIn(request))
        {
            HttpSession session = request.getSession();
            LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
            User user = new User();
            user.setCluster(cluster);
            user.setUsername(lg.getUsername());
            
            request.setAttribute("first", user.getFirstName());
            request.setAttribute("last", user.getLastName());
            
            java.util.UUID profilePicture = user.getProfilePicture();
            if(profilePicture != null)
            {
                request.setAttribute("picture", profilePicture.toString());
            }
            
            request.getRequestDispatcher("settings.jsp").forward(request, response);
        }
        else
        {
            response.sendRedirect("/Instagrim");
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
        String first = request.getParameter("first");
        String last = request.getParameter("last");
        String picture = request.getParameter("picture");
        
        if(User.isLoggedIn(request))
        {
            HttpSession session = request.getSession();
            LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
            User user = new User();
            user.setCluster(cluster);
            user.setUsername(lg.getUsername());
            
            try
            {
                if(picture != null)
                {
                    user.setProfilePicture(java.util.UUID.fromString(picture));
                }
                
                if(first != null)
                {
                    user.setFirstName(first);
                }
                
                if(last != null)
                {
                    user.setLastName(last);
                }
            }
            catch(IllegalArgumentException e)
            {
                request.setAttribute("error", "Invalid picture ID. Please specify a valid picture ID for your profile picture.");
            }
            catch(InvalidImageException e)
            {
                request.setAttribute("error", e.getMessage());
            }
            
            request.getRequestDispatcher("settings.jsp").forward(request, response);
        }
        else
        {
            request.getRequestDispatcher("/Instagrim").forward(request, response);
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
