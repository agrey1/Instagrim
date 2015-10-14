/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import exceptions.InvalidPasswordException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;
import exceptions.InvalidUsernameException;
import exceptions.UserExistsException;
import exceptions.PasswordMismatchException;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;

/**
 *
 * @author Administrator
 */
public class User 
{
    Cluster cluster;
    String username, password;
    
    public User()
    {
        
    }
    
    /**
     * Initialise a user with the given username and password
     * 
     * @param username The username to set.
     * @param password The password to set.
     */
    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
    
    /**
     * Initialise a user from a request
     * @param request The request to derive the information from.
     */
    public User(HttpServletRequest request)
    {
        this.username = request.getParameter("username");
        this.password = request.getParameter("password");
    }
    
    /**
     * Initialise a user from a request with double data entry (passwords)
     * @param request The request to derive the information from.
     * @param checkPasswords Both password fields will be checked when supplied
     * @throws exceptions.PasswordMismatchException
     */
    public User(HttpServletRequest request, boolean checkPasswords) throws PasswordMismatchException
    {
        this.username = request.getParameter("username");
        this.password = request.getParameter("password");
        
        if(request.getParameter("password2").equals(this.password) == false)
        {
            throw new PasswordMismatchException("The two passwords do not match.");
        }
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getUsername()
    {
        return this.username;
    }
    
    /**
     * Validates a username. Descriptive exceptions will be thrown if the name is invalid.
     * 
     * @param username The username to validate.
     * @return True if the username is between 3 and 20 characters long and is alphanumeric. 
     */
    private boolean isUsernameValid(String username) throws InvalidUsernameException
    {
        if(username.length() < 3) throw new InvalidUsernameException("Please choose a username at least three characters long.");
        if(username.length() > 20) throw new InvalidUsernameException("Please choose a username less than 20 characters long.");
        
        for(int i = 0; i < username.length(); i++)
        {
            if(Character.isLetterOrDigit(username.charAt(i)) == false)
            {
                throw new InvalidUsernameException("Your username must not contain any symbols or spaces.");
            }
        }
        
        return true;
    }
    
    /**
     * Validates a username. Descriptive exceptions will be thrown if the name is invalid.
     * 
     * @param username The username to validate.
     * @return True if the username is between 3 and 20 characters long and is alphanumeric. 
     */
    private boolean isPasswordValid(String password) throws InvalidPasswordException
    {
        if(password.length() < 5) throw new InvalidPasswordException("Please choose a password at least five characters long.");
        if(password.length() > 64) throw new InvalidPasswordException("Please choose a password less than 64 characters long.");
        
        boolean[] found = {false, false};
        for(int i = 0; i < password.length(); i++)
        {
            if(Character.isLetter(password.charAt(i)))
            {
                found[0] = true;
            }
            else if(Character.isDigit(password.charAt(i)))
            {
                found[1] = true; 
            }
        }
        
        if(found[0] == false || found[1] == false)
        {
            throw new InvalidPasswordException("Your password should contain a mix of letters and numbers.");
        }
        
        return true;
    }
    
    public boolean RegisterUser() throws InvalidUsernameException, UserExistsException, InvalidPasswordException
    {
        //Validate username and password
        isUsernameValid(this.username);
        isPasswordValid(this.password);
        
        AeSimpleSHA1 sha1handler = new AeSimpleSHA1();
        String EncodedPassword = null;
        
        try
        {
            EncodedPassword = sha1handler.SHA1(this.password);
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException et)
        {
            System.out.println("Can't check your password");
            return false;
        }
        
        Session session = cluster.connect("instagrim");
        
        //Check for an existing user with this name
        PreparedStatement ps = session.prepare("SELECT login FROM userprofiles WHERE login = ?");
        BoundStatement boundStatement = new BoundStatement(ps);
        ResultSet result = session.execute(boundStatement.bind(this.username));
        
        if(result.isExhausted() == false)
        {
            throw new UserExistsException("An account already exists for " + this.username + ".");
        }
        
        //Create the user
        ps = session.prepare("INSERT INTO userprofiles (login, password) Values(?, ?)");
        boundStatement = new BoundStatement(ps);
        session.execute(boundStatement.bind(this.username, EncodedPassword));
        
        return true;
    }
    
    public boolean IsValidUser()
    {
        AeSimpleSHA1 sha1handler = new AeSimpleSHA1();
        String EncodedPassword = null;
        
        try 
        {
            EncodedPassword = sha1handler.SHA1(this.password);
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException et)
        {
            System.out.println("Can't check your password");
            return false;
        }
        
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select password from userprofiles where login = ?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        
        // this is where the query is executed
        // here you are binding the 'boundStatement'
        rs = session.execute(boundStatement.bind(this.username));
        
        if(rs.isExhausted())
        {
            System.out.println("No Images returned");
            return false;
        }
        else 
        {
            for (Row row : rs) 
            {
                String StoredPass = row.getString("password");
                if (StoredPass.compareTo(EncodedPassword) == 0)
                {
                    return true;
                }
            }
        }
        
        return false;  
    }
    
    /**
     * Create a "remember me" cookie. Also updates the expiry time if the cookie is already set.
     * 
     * @param response The response to modify.
     */
    public void setCookie(HttpServletResponse response)
    {
        //Cookies for the "remember me" function   
        Cookie rememberMe = new Cookie("username", this.username);
        
        //1 month expiry. Note: This will be renewed with each successful login.
        rememberMe.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(rememberMe);
    }
    
    /**
     * Delete a "remember me" cookie if it exists in the request.
     * 
     * @param request The request to check for cookies.
     * @param response The response to modify.
     */
    public void removeCookie(HttpServletRequest request, HttpServletResponse response)
    {
        Cookie[] cookies;
        cookies = request.getCookies();
        
        if(cookies != null)
        {
            for(Cookie cookie : cookies)
            {
                if(cookie.getName().equals("username"))
                {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }
    
    /**
     * Check for username cookies in the visitor's request.
     * 
     * @param request The request to check for cookies.
     * 
     * @return The user's username as stored in the cookie or null if no cookie is found.
     */
    public String getCookieUsername(HttpServletRequest request)
    {
        Cookie[] cookies;
        cookies = request.getCookies();
        
        if(cookies != null)
        {
            for(Cookie cookie : cookies)
            {
                if(cookie.getName().equals("username"))
                {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
    
    public static boolean isLoggedIn(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
        if (lg != null)
        {
            return lg.getlogedin();
        }
        
        return false;
    }
    
    public void setCluster(Cluster cluster) 
    {
        this.cluster = cluster;
    }
}
