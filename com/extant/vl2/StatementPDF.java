/*
 * StatementPDF.java
 * Provides the methods for producing financial statments and chart listings 
 * in .pdf format.
 * Updated to handle variable-length GL data and XML Chart specification.
 *
 * Created on February 20, 2005, 11:02 PM
 */

package com.extant.vl2;
import com.extant.utilities.*;
//import com.extant.utilities.XProperties;
//import com.extant.utilities.Strings;
//import com.extant.utilities.Julian;
//import com.extant.utilities.LogFile;
//import com.extant.utilities.VLException;
import java.io.*;
import java.util.Vector;
import com.lowagie.text.*;
//import static VL2.props;

/**
 *
 * @author  jms
 */
public class StatementPDF
extends AbstractStatement
{
    public StatementPDF()
    { }

    // Use this constructor to make a statement
    public StatementPDF
        ( XProperties props
        , Chart chart
        , String glFilename
        , Julian begin
        , Julian end
        , int reportLevel
        , String outfileName
        , LogFile logger 
        )
    throws VLException
    {
        makingChart = false;
        makingStatement = true;
        setup( props, chart, makingStatement, begin, end, reportLevel, outfileName, logger );
    }

    // Use this constructor to make a chart listing
    //(props, VL2.chart, glFilename, outFilename, logger);
    
    public StatementPDF
        ( XProperties props
        , Chart chart
        , String glFilename
        , String outFilename
        , LogFile logger
        )
    throws VLException
    {
        makingChart = true;
        makingStatement = false;
        setup( props, chart, makingStatement, begin, end, 0, outFilename, logger );
    }

    public StmtTable initialize( int reportLevel, String outfileName )
    throws VLException
    {
        try
        {
            if (Strings.contains(outfileName, ".pdf"))
            {
                stmtTable = new StmtTable( chart, reportLevel, outfileName );
                return stmtTable;
            }
            else return null;
        }
        catch (IOException iox)
        {   throw new VLException( VLException.IOX, iox.getMessage() ); }
        catch (DocumentException dx)
        {   throw new VLException( VLException.DOCUMENT, dx.getMessage() ); }
    }
    
    void printTextLine( ChartElement element )
    throws VLException
    {   // Builds a line which contains text only (no amount)
        try
        {
            String format = element.getAttribute( "format" );
            if ( format.contains( "newpage" ) ) stmtTable.newPage();
            if ( format.contains( "center" ) )  stmtTable.center( element.getAttribute( "title" ) );
            else addTextLine( element );
            if (begin != null && end != null)
            {
                if ( format.contains( "ending" ))
                    stmtTable.center( end.toString( dateFormat ) );
                if ( format.contains( "period" ) )
                    stmtTable.center( begin.toString( dateFormat ) + " - " + end.toString( dateFormat ) );
            }
            if ( format.contains( "skip" ) )
                stmtTable.center( "" );
        }
        catch (BadElementException bex)
        {   throw new VLException( VLException.BAD_ELEMENT, bex.getMessage() ); }
        catch (DocumentException dx)
        {   throw new VLException( VLException.DOCUMENT, dx.getMessage() ); }
    }

    void addTextLine( ChartElement element )
    throws BadElementException
    {   // Adds a line which contains text only
        Vector <Cell> cells = new Vector <Cell> ( reportLevel * 2 );
        int level = element.getLevel();
        if ( element.name.equals( "total" ) ) --level;
        int colSpan = reportLevel - level + 1;
        Cell acctNo = new Cell( "" );
        cells.addElement( acctNo );
        Cell descr = new Cell( element.getAttribute( "title" ) );
        for (int i=0; i<level; ++i) cells.addElement( new Cell( "" ) );
        descr.setHorizontalAlignment( Cell.ALIGN_LEFT );
        descr.setColspan( colSpan );
        cells.addElement( descr );
        stmtTable.addRow( cells );
    }

    public void printAmountLine( ChartElement element, Account account )
    throws VLException
    {
        // Do not print amountlines when printing the chart
        if (makingChart) return;
        
        // Adds a line containing an amount
        try
        {
            Vector <Cell> cells = new Vector <Cell> ( reportLevel * 2 );
            int accountLevel = account.getLevel();
            if ( account.getType().equals( "T" ) ) --accountLevel;
            int colSpan = reportLevel - accountLevel + 1;
        
            Cell acctNo = new Cell( "" );
            if ( chart.getShowacct() && account.getType().equalsIgnoreCase( "A" ) )
            {
                acctNo = new Cell( account.getAccountNo() );
                acctNo.setHorizontalAlignment( Cell.ALIGN_LEFT );
            }
            cells.addElement( acctNo );

            Cell title = new Cell( account.getTitle() );
            for (int i=0; i<accountLevel; ++i) cells.addElement( new Cell( "" ) );
            title.setHorizontalAlignment( Cell.ALIGN_LEFT );
            title.setColspan( colSpan );
            cells.addElement( title );

            long bal = account.getEndBal();
            if ( account.getType().equalsIgnoreCase( "L" ) ||
                 account.getType().equalsIgnoreCase( "I" ) ||
                 account.getType().equalsIgnoreCase( "R" ) )
                bal = -bal;
            for (int i=0; i<reportLevel - accountLevel; ++i) cells.addElement( new Cell( "" ) );
            Cell amount = new Cell( Strings.formatPennies( bal, dollarFormat ) );
            amount.setHorizontalAlignment( Cell.ALIGN_RIGHT );
            amount.setColspan( 3 );
            cells.addElement( amount );
            stmtTable.addRow( cells );
        }
        catch (BadElementException bex)
        {   throw new VLException( VLException.BAD_ELEMENT, bex.getMessage() ); }
    }

//    void printNetWorth( ChartElement element, Account thisAccount )
//    throws VLException
//    {
//        System.out.println(element.toString());
//        String netWorth;
//        Account pl = new Account( thisAccount.getAccountNo(),
//            Strings.format( thisAccount.getLevel()+1, "#" ),
//            thisAccount.getType(), "Net Worth at " + begin.toString( dateFormat ) );
//        pl.addToBeginBal( thisAccount.getBeginBal() );
//        cPrint( element, pl, false );   // net worth at beginning of period
//        pl.setDescr( "Change in Net Worth" );
//        pl.zeroBalances();
//        pl.addToDeltaBal( thisAccount.getDeltaBal() );
//        //printAmountLine( element, pl );  // p&l during the period
//        cPrint( element, pl, false );
//        //thisAccount.setDescr( "Net Worth at " + end.toString( dateFormat ) );
//        pl.addToBeginBal( thisAccount.getBeginBal() );
//        pl.setLevel( pl.getLevel() - 1 );
//        pl.setDescr( "Net Worth at " + end.toString( dateFormat ) );
//        cPrint( element, pl, false );
//    }

    public void close()
    throws VLException
    {
        logger.log( logger.DEBUG, "[" + this.getClass().getSimpleName() + "] close()" );
        try { stmtTable.finish(); }
        catch (DocumentException dx)
        {   throw new VLException( VLException.DOCUMENT, dx.getMessage() ); }
        logger.logDebug("StatementPDF.close - Normal Exit");
    }

    /**
     * @param args the command line arguments
     * dir=<work directory>
     * c=<chart file name>
     * gl=<GL file name>
     * o=<output file name>
     */
    /***** FOR TESTING *****
    public static void main(String[] args)
    {
        try
        {
            Clip clip = new Clip( args, new String[]
                { "dir=E:\\ACCOUNTING\\EXTANT\\GL17\\"
                , "c=CHART.XML"
                , "gl=GL0010.DAT"
                , "o=StmtOOP.pdf"
                } );
            LogFile logger = new LogFile();
            String workDir = clip.getParam( "dir" );
            if ( !workDir.endsWith( File.separator ) ) workDir += File.separator;
            Chart chart = new Chart();
            chart.init( workDir + clip.getParam( "c" ) );
            String glFilename = clip.getParam( "gl" );
            
//            glCheck
//            ( String glFilename
//            , String chartFilename
//            , String fileType  // "fixed" or "token"
//            , LogFile loggerParm
//            )
            GLCheck glCheck = new GLCheck();
            glCheck(clip.getParam("gl"), clip.getParam("c"), "token", logger);
            if ( glFilename.indexOf( File.separator ) < 0 ) glFilename = workDir + glFilename;
            glCheck.checkTokenFile( glFilename );
            Julian begin = glCheck.getEarliestDate();
            Julian end = glCheck.getLatestDate();
            String outfileName = clip.getParam( "o" );
            if ( outfileName.indexOf( File.separator ) < 0 )
                outfileName = workDir + outfileName;
            // For Debugging:
            //logger.setLogLevel( logger.DEBUG_LOG_LEVEL );

            StatementPDF statementPDF = new StatementPDF
                ( workDir
                , chart
                , glFilename
                , begin
                , end
                , 0     // reportLevel
                , outfileName
                , logger
                );
            statementPDF.makeStatement();
            com.extant.utilities.Console.println( "Output is in " + outfileName );
        }
        catch (Exception x)
        {   x.printStackTrace(); }
    }
    /*****/

    StmtTable stmtTable;
    boolean makingChart;
    boolean makingStatement;
}

