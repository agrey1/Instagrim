package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
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
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

/**
 * Servlet implementation class Profile
 */
@WebServlet(urlPatterns = 
{
    "/Profile",
    "/Profile/*"
})

public class Profile extends HttpServlet 
{
    private Cluster cluster;
    private HashMap CommandsMap = new HashMap();
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Profile() 
    {
        super();
        CommandsMap.put("Profile", 1);
    }
    
    public void init(ServletConfig config) throws ServletException 
    {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        // TODO Auto-generated method stub
        String args[] = Convertors.SplitRequestPath(request);
        int command;
        
        try
        {
            command = (Integer) CommandsMap.get(args[1]);
        }
        catch (Exception et)
        {
            error("Bad Operator", response);
            return;
        }
        
        switch (command)
        {
            case 1:
            {
                if(args.length > 2 && args[2].equals("edit"))
                {
                    request.getRequestDispatcher("/settings.jsp").forward(request, response);
                }
                else
                {
                    displayProfile(args[2], request, response);
                }
                
                break;
            }
            default:
            {
                error("Bad Operator", response);
            }
        }
    }
    
    private void displayProfile(String user, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(user);
        RequestDispatcher rd = request.getRequestDispatcher("/profile.jsp");
        request.setAttribute("Pics", lsPics);
        
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
        boolean loggedIn = false;
        if(lg != null)
        {
            loggedIn = lg.getlogedin();
        }
        
        request.setAttribute("username", lg.getUsername());
        request.setAttribute("profile", user);
        request.setAttribute("loggedIn", loggedIn);
        
        rd.forward(request, response);
    }
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        processRequest(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        processRequest(request, response);
    }
    
    private void error(String mess, HttpServletResponse response) throws ServletException, IOException 
    {
        try (PrintWriter out = new PrintWriter(response.getOutputStream())) 
        {
            out.println("<h1>You have an error in your input</h1>");
            out.println("<h2>" + mess + "</h2>");
        }
    }
}
