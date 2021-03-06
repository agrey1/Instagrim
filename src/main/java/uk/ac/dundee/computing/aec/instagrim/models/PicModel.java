package uk.ac.dundee.computing.aec.instagrim.models;

/*
 * Expects a cassandra columnfamily defined as
 * use keyspace2;
 CREATE TABLE Tweets (
 user varchar,
 interaction_time timeuuid,
 tweet varchar,
 PRIMARY KEY (user,interaction_time)
 ) WITH CLUSTERING ORDER BY (interaction_time DESC);
 * To manually generate a UUID use:
 * http://www.famkruithof.net/uuid/uuidgen
 */
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import exceptions.UserNotFoundException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import static org.imgscalr.Scalr.*;
import org.imgscalr.Scalr.Method;

import uk.ac.dundee.computing.aec.instagrim.lib.*;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import uk.ac.dundee.computing.aec.instagrim.stores.PicComment;
//import uk.ac.dundee.computing.aec.stores.TweetStore;

public class PicModel
{
    Cluster cluster;
    
    public PicModel() 
    {
        
    }
    
    public PicModel(Cluster cluster)
    {
        this.cluster = cluster;
    }
    
    public void setCluster(Cluster cluster) 
    {
        this.cluster = cluster;
    }
    
    public void insertPic(byte[] b, String type, String name, String user) 
    {
        try 
        {
            Convertors convertor = new Convertors();

            String types[] = Convertors.SplitFiletype(type);
            ByteBuffer buffer = ByteBuffer.wrap(b);
            int length = b.length;
            java.util.UUID picid = convertor.getTimeUUID();
            
            //The following is a quick and dirty way of doing this, will fill the disk quickly !
            Boolean success = (new File("/var/tmp/instagrim/")).mkdirs();
            FileOutputStream output = new FileOutputStream(new File("/var/tmp/instagrim/" + picid));
            
            output.write(b);
            byte[]  thumbb = picresize(picid.toString(),types[1]);
            int thumblength = thumbb.length;
            ByteBuffer thumbbuf = ByteBuffer.wrap(thumbb);
            byte[] processedb = picdecolour(picid.toString(),types[1]);
            ByteBuffer processedbuf = ByteBuffer.wrap(processedb);
            int processedlength = processedb.length;
            Session session = cluster.connect("instagrim");

            PreparedStatement psInsertPic = session.prepare("insert into pics ( picid, image, thumb, processed, user, interaction_time,imagelength,thumblength,processedlength,type,name) values(?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement psInsertPicToUser = session.prepare("insert into userpiclist ( picid, user, pic_added) values(?,?,?)");
            BoundStatement bsInsertPic = new BoundStatement(psInsertPic);
            BoundStatement bsInsertPicToUser = new BoundStatement(psInsertPicToUser);

            Date DateAdded = new Date();
            session.execute(bsInsertPic.bind(picid, buffer, thumbbuf,processedbuf, user, DateAdded, length,thumblength,processedlength, type, name));
            session.execute(bsInsertPicToUser.bind(picid, user, DateAdded));
            session.close();

        } 
        catch (IOException ex) 
        {
            System.out.println("Error --> " + ex);
        }
    }
    
    public byte[] picresize(String picid, String type) 
    {
        try 
        {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
            BufferedImage thumbnail = createThumbnail(BI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, type, baos);
            baos.flush();
            
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } 
        catch (IOException et) 
        {

        }
        
        return null;
    }
    
    public byte[] picdecolour(String picid,String type) 
    {
        try 
        {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
            BufferedImage processed = createProcessed(BI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(processed, type, baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        }
        catch (IOException et) 
        {
            
        }
        
        return null;
    }
    
    public static BufferedImage createThumbnail(BufferedImage img) 
    {
        //Maximum and minimum thumbnail size
        int width = img.getWidth();
        int height = img.getHeight();
        
        if(width > 600) width = 600;
        else if(width < 250) width = 250;
        
        if(height > 750) height = 750;
        else if(height < 250) height = 250;
        
        return resize(img, Method.SPEED, width, height, OP_ANTIALIAS, OP_GRAYSCALE);
    }
    
    public static BufferedImage createProcessed(BufferedImage img) 
    {
        int Width = img.getWidth()-1;
        return resize(img, Method.SPEED, Width, OP_ANTIALIAS, OP_GRAYSCALE);
    }
    
    /**
     * Gets a list of picture IDs for a given user
     * @param User The user whose pictures we wish to get
     * @return A LinkedList of picture IDs.
     * @throws exceptions.UserNotFoundException
     */
    public java.util.LinkedList<Pic> getPicsForUser(String username) throws UserNotFoundException
    {
        java.util.LinkedList<Pic> Pics = new java.util.LinkedList<>();
        Session session = cluster.connect("instagrim");
        
        //Ensure that the user exists
        PreparedStatement ps = session.prepare("SELECT login FROM userprofiles WHERE login = ?");
        BoundStatement boundStatement = new BoundStatement(ps);
        ResultSet result = session.execute(boundStatement.bind(username));
        
        if(result.isExhausted())
        {
            throw new UserNotFoundException(username + "has not registered an account.");
        }
        
        ps = session.prepare("select picid, pic_added from userpiclist where user = ?");
        boundStatement = new BoundStatement(ps);
        
        ResultSet rs = session.execute(boundStatement.bind(username));
        
        if (rs.isExhausted()) 
        {
            System.out.println("No Images returned");
            return null;
        } 
        else 
        {
            for (Row row : rs) 
            {
                Pic pic = new Pic();
                java.util.UUID UUID = row.getUUID("picid");
                System.out.println("UUID" + UUID.toString());
                pic.setUUID(UUID);
                
                Date postedAt = row.getDate("pic_added");
                pic.setDatePosted(postedAt);
                
                try
                {
                    pic.setPicComments(getComments(UUID, 5)); 
                }
                catch(Exception e)
                {
                    
                }
                
                Pics.add(pic);
            }
        }
        
        return Pics;
    }
    
    public Pic getPic(int image_type, java.util.UUID picid) 
    {
        Session session = cluster.connect("instagrim");
        ByteBuffer bImage = null;
        String type = null;
        int length = 0;
        
        try 
        {
            Convertors convertor = new Convertors();
            ResultSet rs = null;
            PreparedStatement ps = null;
         
            if(image_type == Convertors.DISPLAY_IMAGE) 
            {
                ps = session.prepare("select image, imagelength, type from pics where picid = ?");
            } 
            else if(image_type == Convertors.DISPLAY_THUMB) 
            {
                ps = session.prepare("select thumb, imagelength, thumblength, type from pics where picid = ?");
            } 
            else if(image_type == Convertors.DISPLAY_PROCESSED) 
            {
                ps = session.prepare("select processed, processedlength, type from pics where picid = ?");
            }
            
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute(boundStatement.bind(picid));
            
            if (rs.isExhausted()) 
            {
                System.out.println("No Images returned");
                return null;
            } 
            else 
            {
                for (Row row : rs)
                {
                    if (image_type == Convertors.DISPLAY_IMAGE)
                    {
                        bImage = row.getBytes("image");
                        length = row.getInt("imagelength");
                    }
                    else if (image_type == Convertors.DISPLAY_THUMB)
                    {
                        bImage = row.getBytes("thumb");
                        length = row.getInt("thumblength");
                    }
                    else if (image_type == Convertors.DISPLAY_PROCESSED)
                    {
                        bImage = row.getBytes("processed");
                        length = row.getInt("processedlength");
                    }
                    
                    type = row.getString("type");
                }
            }
        }
        catch (Exception et) 
        {
            System.out.println("Can't get Pic" + et);
            return null;
        }
        
        session.close();
        Pic p = new Pic();
        p.setPic(bImage, length, type);
        
        return p;
    }
    
    public String getPublisher(java.util.UUID picid)
    {
        Session session = cluster.connect("instagrim");
        
        PreparedStatement psSelectUser = session.prepare("SELECT user FROM pics WHERE picid = ?");
        BoundStatement bsSelectUser = new BoundStatement(psSelectUser);
        
        ResultSet result = session.execute(bsSelectUser.bind(picid));
        session.close();
        
        if(result.isExhausted() == false)
        {
            return result.one().getString("user");
        }
        
        return null;
    }
    
    /**
     * Get a list of comments belonging to the specified picture, ordered by time posted
     * @param picid The picture whose comments we wish to retrieve
     * @param limit Retrieve no more than this amount of comments. 0 for no limit.
     * @return The list of PicComment objects
     * @throws ParseException
     */
    public LinkedList<PicComment> getComments(java.util.UUID picid, int limit) throws ParseException
    {
        Session session = cluster.connect("instagrim");
        
        PreparedStatement psSelectComments = session.prepare("SELECT comments FROM pics WHERE picid = ?");
        BoundStatement bsSelectComments = new BoundStatement(psSelectComments);
        
        ResultSet result = session.execute(bsSelectComments.bind(picid));
        session.close();
        
        LinkedList<PicComment> picComments = null;
        
        if(result.isExhausted() == true)
        {
            //No comments
            System.out.println("No comments");
        }
        else
        {
            //There will only be one row in the result, get the comment
            for(String commentData : result.one().getSet("comments", String.class))
            {
                if(commentData != null)
                {
                    if(picComments == null) picComments = new LinkedList<>();
                    
                    picComments.add(new PicComment(commentData));
                }
            }
            
            Collections.sort(picComments);
            
            if(limit > 0 && picComments.size() >= limit)
            {
                picComments.subList(0, picComments.size() - 5).clear();
            }
        }
        
        return picComments;
    }
    
    public void addComment(java.util.UUID picid, PicComment comment)
    {
        try (Session session = cluster.connect("instagrim")) 
        {
            String commentData = "{'" + comment.getAuthor() + "," + comment.getPostedStr() + "," + comment.getCommentText() + "'}";
            
            PreparedStatement psInsertPic = session.prepare("UPDATE pics SET comments = comments + " + commentData + " WHERE picid = ?");
            BoundStatement bsInsertPic = new BoundStatement(psInsertPic);
            
            ResultSet result = session.execute(bsInsertPic.bind(picid));
        }
    }
}
