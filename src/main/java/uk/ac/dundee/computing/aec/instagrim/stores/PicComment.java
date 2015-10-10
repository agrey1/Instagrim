/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.stores;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Alexander Grey
 */
public class PicComment
{
    String author;
    String commentText;
    Date posted; //Time that the comment was created
    final String dateFormat = "yyyy/MM/dd HH:mm:ss";
    
    public PicComment(String author, String commentText)
    {
        this.author = author;
        this.commentText = commentText;
        posted = new Date();
    }
    
    /**
     * Initialise a PicComment from a data string
     * @param commentData The data as stored in the database
     */
    public PicComment(String commentData)
    {
        this.author = commentData.substring(0, commentData.indexOf(","));
        commentData = commentData.substring(commentData.indexOf(","), commentData.length() - commentData.indexOf(","));
        
        String dateStr = commentData.substring(0, commentData.indexOf(","));
        DateFormat format = new SimpleDateFormat(this.dateFormat);
        
        try
        {
            this.posted = format.parse(dateStr);
        }
        catch(Exception e)
        {
            
        }
        
        commentData = commentData.substring(commentData.indexOf(","), commentData.length() - commentData.indexOf(","));
        this.commentText = commentData.substring(0, commentData.indexOf(","));
    }
    
    public String getAuthor()
    {
        return this.author;
    }
    
    public Date getPosted()
    {
        return this.posted;
    }
    
    public String getCommentText()
    {
        return this.commentText;
    }
}