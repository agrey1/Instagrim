/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author Alexander Grey
 */
public class InvalidImageException extends Exception
{
    public InvalidImageException(String message)
    {
        super(message);
    }
}