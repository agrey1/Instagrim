/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

/**
 *
 * @author Alexander Grey
 */

@WebServlet(name = "Image", urlPatterns = {"/Image", "/Image/*", "/Thumb/*"})
@MultipartConfig
public class Image extends HttpServlet 
{
    private Cluster cluster;
    private HashMap CommandsMap = new HashMap();
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Image() 
    {
        super();
        CommandsMap.put("Image", 1);
        CommandsMap.put("Thumb", 2);
    }

    public void init(ServletConfig config) throws ServletException 
    {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }
    
    private void DisplayImage(int type, String Image, HttpServletResponse response) throws ServletException, IOException 
    {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        
        Pic p = tm.getPic(type, java.util.UUID.fromString(Image));
        
        OutputStream out = response.getOutputStream();
        
        response.setContentType(p.getType());
        response.setContentLength(p.getLength());
        //out.write(Profile);
        InputStream is = new ByteArrayInputStream(p.getBytes());
        BufferedInputStream input = new BufferedInputStream(is);
        byte[] buffer = new byte[8192];
        
        for (int length = 0; (length = input.read(buffer)) > 0;) 
        {
            out.write(buffer, 0, length);
        }
        
        out.close();
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        // TODO Auto-generated method stub
        String args[] = Convertors.SplitRequestPath(request);
        int command;
        
        try
        {
            command = (Integer) CommandsMap.get(args[1]);
            
            for(String test : args)
            {
                System.out.println("2134: " + test);
            }
        } 
        catch (Exception et) 
        {
            error("Bad Operator", response);
            return;
        }
        
        switch(command)
        {
            case 1:
            {
                DisplayImage(Convertors.DISPLAY_PROCESSED, args[2], response);
                break;
            }
            case 2:
            {
                DisplayImage(Convertors.DISPLAY_THUMB, args[2], response);
                break;
            }
            default:
            {
                error("Bad Operator", response);
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
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
        
        String username = "";
        if (lg.getlogedin())
        {
            username = lg.getUsername();
        }
        
        //Uploading an image
        for (Part part : request.getParts()) 
        {
            System.out.println("Part Name " + part.getName());

            String type = part.getContentType();
            String filename = part.getSubmittedFileName();

            InputStream is = request.getPart(part.getName()).getInputStream();
            int i = is.available();

            if (i > 0) 
            {
                byte[] b = new byte[i + 1];
                is.read(b);
                System.out.println("Length : " + b.length);
                PicModel tm = new PicModel();
                tm.setCluster(cluster);
                tm.insertPic(b, type, filename, username);

                is.close();
            }

            response.sendRedirect("/Instagrim");
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
    
    private void error(String mess, HttpServletResponse response) throws ServletException, IOException 
    {
        PrintWriter out = null;
        out = new PrintWriter(response.getOutputStream());
        out.println("<h1>You have an error in your input</h1>");
        out.println("<h2>" + mess + "</h2>");
        out.close();
        
        return;
    }
}
