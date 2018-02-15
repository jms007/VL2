/*
 * ParseDocument.java
 *
 * Created on September 4, 2006, 9:33 PM
 */

package com.extant.vl2;
import com.extant.utilities.LogFile;
import com.extant.utilities.Clip;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.util.Vector;

//import ChartElement;

/**
 *
 * @author jms
 */
public class ParseDocument
extends DefaultHandler
{

    public ParseDocument( String inFilename )
    throws ParserConfigurationException, SAXException, IOException
    {
        this( inFilename, null );
    }

    public ParseDocument( String inFilename, LogFile logger )
    throws ParserConfigurationException, SAXException, IOException
    {
        if ( logger == null ) this.logger = new LogFile();
        else this.logger = logger;
        elementList = new Vector <ChartElement> ( 100, 100 );

        // Use the default (non-validating) parser
        //SAXParserFactory factory = SAXParserFactory.newInstance();

        // Use the validating parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);

        // Parse the input
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse( new File( inFilename ), this );
    }

    public void startElement(String namespaceURI,
                             String sName, // simple name (localName)
                             String qName, // qualified name
                             Attributes attrs)
    throws SAXException
    {
        String eName = sName; // element name
        if ("".equals(eName)) eName = qName; // namespaceAware = false
        /***** FOR TESTING *****/
        String logMessage = "[" + level + "] ELEMENT: " + eName;
        if (attrs != null)
        {
            for (int i = 0; i < attrs.getLength(); i++)
            {
                String aName = attrs.getLocalName(i); // Attr name
                if ("".equals(aName)) aName = attrs.getQName(i);
                logMessage += "   " + aName + "=" + attrs.getValue(i);
            }
        }
        logger.logDebug( logMessage );
        /***** END OF TEST *****/

//        // Build entry:  <element name>;<attr name>=<attr value>|...;level
//        String element = eName + ";";
//        for (int i=0; i<attrs.getLength(); ++i)
//        {
//            String aName = attrs.getLocalName(i);
//            if ( aName.equals("") ) aName = attrs.getQName(i);
//            if ( i > 0 ) element += "|";
//            element += aName + "=" + attrs.getValue( i );
//        }
//        element += ";" + Strings.format( level, "00" );
//        elementList.addElement( element );

        // We use ChartElements, but they are sufficiently general for this purpose
        ChartElement chartElement = new ChartElement( eName, level, 0 );
        for (int i=0; i<attrs.getLength(); ++i)
        {
            String aName = attrs.getLocalName( i );
            if ( aName.equals("") ) aName = attrs.getQName( i );
            chartElement.putAttribute( aName, attrs.getValue( i ) );
        }
        elementList.addElement( chartElement );

        ++level;
    }

    public void endElement(String namespaceURI,
                           String sName, // simple name
                           String qName  // qualified name
                          )
    {
        --level;
    }

    public ChartElement[] getElementList()
    {
          ChartElement answer[] = new ChartElement[elementList.size()];
//        for (int i=0; i<answer.length; ++i)
//            answer[i] = (ChartElement)elementList.elementAt( i );
//        return answer;
//        ChartElement answer[];
        elementList.copyInto(answer);
        return answer;
    }

    // treat validation errors as fatal
    public void error(SAXParseException e)
    throws SAXParseException
    {
        throw e;
    }

    // dump warnings too
    public void warning(SAXParseException err)
    throws SAXParseException
    {
        logger.logInfo("** Warning"
            + ", line " + err.getLineNumber()
            + ", uri " + err.getSystemId());
        logger.logInfo("   " + err.getMessage());
    }

    public static void main(String[] args)
    {
        try
        {
            Clip clip = new Clip( args, new String[] { "in=E:\\ACCOUNTING\\JMS\\GL06\\CHART.XML" } );
            String infile = clip.getParam( "in" );
            ParseDocument parseDocument = new ParseDocument( infile );
            ChartElement elementList[] = parseDocument.getElementList();
            for (int i=0; i<elementList.length; ++i)
                com.extant.utilities.Console.println( elementList[i].toString() );
        }
        catch (Throwable x)
        {   com.extant.utilities.Console.println( x.getMessage() ); }
        System.exit( 0 );
    }

    LogFile logger;
    int level = 0;
    Vector <ChartElement> elementList;
}

