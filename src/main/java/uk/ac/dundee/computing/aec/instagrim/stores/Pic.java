/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.stores;

import com.datastax.driver.core.utils.Bytes;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 *
 * @author Administrator
 */
public class Pic 
{
    private ByteBuffer bImage = null;
    private int length;
    private String type;
    private java.util.UUID UUID = null;
    private LinkedList<PicComment> picComments = null;
    Date posted = null;
    
    public void Pic() 
    {

    }
    
    public void setPicComments(LinkedList<PicComment> picComments)
    {
        this.picComments = picComments;
    }
    
    public LinkedList<PicComment> getPicComments()
    {
        return this.picComments;
    }
    
    public void setUUID(java.util.UUID UUID)
    {
        this.UUID = UUID;
    }
    
    public String getSUUID()
    {
        return UUID.toString();
    }
    
    public void setPic(ByteBuffer bImage, int length, String type) 
    {
        this.bImage = bImage;
        this.length = length;
        this.type = type;
    }

    public ByteBuffer getBuffer() 
    {
        return bImage;
    }

    public int getLength() 
    {
        return length;
    }
    
    public String getType()
    {
        return type;
    }

    public void setDatePosted(Date datePosted)
    {
        this.posted = datePosted;
    }
    
    public String getDatePostedStr()
    {
        final String dateFormat = "HH:mm MM/dd/yyyy";
        DateFormat df = new SimpleDateFormat(dateFormat);

        return df.format(this.posted);
    }
    
    public byte[] getBytes() 
    {
        byte image[] = Bytes.getArray(bImage);
        return image;
    }
}
