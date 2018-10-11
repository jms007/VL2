package com.extant.vl2;

import java.util.Properties;
import java.io.*;
import com.extant.utilities.Strings;

/**
 * TODO    THIS CLASS MAY NOT BE USED IN VL2
 *
 * @author jms
 */
@SuppressWarnings("serial")
public class VLProps
    extends Properties
{
    public void init( String vlPropsSaved )
        throws IOException
    {
        FileInputStream in = new FileInputStream(vlPropsSaved);
        this.load(in);
    }
//    public void init(String initFilename)
//    
//    {
//        FileInputStream fis;
//        System.out.println("init("+initFilename+")");
//        
//        try
//        {
//            fis = new FileInputStream("E:\\DevelopmentTools\\stdProps.txt");
//            props = VL2.PROPERTIES;
//            VL2.load(fis);
//            fis.close();
//        }
//        catch (Exception x)
//        {
//            System.out.println("Cannot init: " + x.getMessage());
//            System.out.println(initFilename + " not found.");
//            System.exit(1);
//        }
//    }
//    
    public void setProp(String key, String value)
    {
        this.setProperty(key, value);
    }

    public String getProp(String key)
    {
        String value = this.getProperty(key);
        System.out.println("value="+value);
        if (!value.contains("+")) return value;
        String[] twoParts = value.split("\\+");
        System.out.println( "after split: " +twoParts[0] +"   "+ twoParts[1]);
        String pre = Strings.trim( twoParts[0], "[]");
        String prepend = this.getProperty(pre);
        System.out.println("prepend="+prepend);
        System.out.println("Result: " + prepend + twoParts[1]);
        return prepend + twoParts[1];   
    }

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args)
//    {
//        try
//        {
//            VLProps test01 = E:\VL2Default.properties;
//            test01.init("E:\\DevelopmentTools\\stdProps.txt");
//        }
//        catch (IOException iox)
//        {
//            System.out.println("IOException in main: "+ iox.getMessage());
//        }
//    }

}

