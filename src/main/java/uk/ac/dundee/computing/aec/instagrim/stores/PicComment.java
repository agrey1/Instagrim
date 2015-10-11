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
    DateFormat format = new SimpleDateFormat(this.dateFormat);
    
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
        String[] parts = commentData.split(",", 3);
        
        this.author = parts[0];
        
        try
        {
            this.posted = format.parse(parts[1]);
        }
        catch(Exception e)
        {
            
        }
        
        this.commentText = parts[2];
    }
    
    public String getAuthor()
    {
        return this.author;
    }
    
    public Date getPosted()
    {
        return this.posted;
    }
    
    public String getPostedStr()
    {
        return format.format(this.posted);
    }
    
    public String getCommentText()
    {
        return this.commentText;
    }
}