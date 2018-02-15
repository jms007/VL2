/*
 * StmtTable.java
 *
 * Created on February 2, 2005, 8:47 PM
 */

package com.extant.vl2;

import java.awt.Font;
//import utilities.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
//import com.lowagie.text.pdf.PdfContentByte;
//import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

// Needed for testing only:
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
//import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author  jms
 */
public class StmtTable
{
    public StmtTable
        ( Chart chart
        , int stmtLevel
        , String outfileName
        )
    throws BadElementException, DocumentException, FileNotFoundException, IOException
    {
        this.chart = chart;
        this.stmtLevel = stmtLevel;
        this.outfileName = outfileName;
        setup();
    }

    private void setup()
    throws FileNotFoundException, BadElementException, DocumentException, IOException
    {
        Rectangle portrait  = new Rectangle( 0, 0, (float)(8.5*72), (float)(11*72) );
        Rectangle landscape = new Rectangle( 0, 0, (float)(11*72), (float)(8.5*72) );
        nCols = (stmtLevel + 1) * 2 + 2;
        if ( chart.getShowacct() ) ++nCols;
        if ( nCols > 10 ) document = new Document( landscape, 36, 36, 36, 36 );
        else document = new Document( portrait, 36, 36, 36, 36 );
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream( outfileName ));
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        document.open();
        colWidths = new int[nCols];
        int i = 0;
        //!! The colWidths are relative -- this does not ensure that the strings
        //   will fit in the cells
        if ( chart.getShowacct() ) colWidths[i++] = chart.maxAccountNo.length();
        for (int j=0; j<stmtLevel; ++j) colWidths[i++] = 2;
        colWidths[i++] = chart.maxAccountTitle.length();
        for (; i<nCols; ++i) colWidths[i] = 5;
//Console.print( "stmtLevel=" + stmtLevel + "  colWidths=[" );
//int totW = 0;
//for (i=0; i<nCols-1; ++i) { Console.print( colWidths[i] + "," ); totW += colWidths[i]; }
//Console.println( colWidths[nCols - 1] + "] = " + totW );
        table = newTable( colWidths );
    }

    private Table newTable( int colWidths[] )
    throws BadElementException, DocumentException
    {
        Table table = new Table( colWidths.length );
        table.setWidth( 100 );
        table.setAlignment( table.ALIGN_CENTER );
        table.setAutoFillEmptyCells( true );
        table.setBorder( Table.NO_BORDER );
        // For Debugging:
        table.setDefaultCellBorder( 15 );
        table.setDefaultCellBorder( Table.NO_BORDER );
        table.setWidths( colWidths );
        table.setSpacing( 0 );
        // Table rows & columns are numbered from zero
        row = 0;
        return table;
    }

    public void addRow( Vector cells )
    throws BadElementException
    {
        table.addCell( (Cell)cells.elementAt( 0 ), row++, 0 );
        for (int i=1; i<cells.size(); ++i)
            table.addCell( (Cell)cells.elementAt( i ) );
    }

    public void center( String s )
    throws BadElementException
    {
        Cell cell = new Cell( s );
        cell.setHorizontalAlignment( Cell.ALIGN_CENTER );
        cell.setColspan( colWidths.length );
        table.addCell( cell, row++, 0 );
    }

    public void blankLine()
    throws BadElementException
    {
        center( "" );
    }

    public void newPage()
    throws DocumentException
    {
        document.add( table );
        table = newTable( colWidths );
        document.newPage();
        
    }

    public void finish()
    throws DocumentException
    {
        document.add( table );
        document.close();
    }

    public void setFont( Font font )
    {
        this.font = font;
    }

    public void setDateFormat( String format )
    {
        dateFormat = format;
    }

    public void setDollarFormat( String dollarFormat )
    {
        this.dollarFormat = dollarFormat;
    }

    /**
     * @param args the command line arguments
     */
    /***** FOR TESTING *****
    public static void main(String[] args)
    {
        try
        {
            LogFile logger = new LogFile();
            Cell amount;
            Chart chart = new Chart();
            chart.init( "E:\\ACCOUNTING\\JMS\\GL04\\CHART.DAT" );
            Julian begin = new Julian( "1-1-04" );
            Julian end = new Julian( "12-31-04" );
            String outfileName = "C:\\Test\\StmtTest.pdf";
            VLUtil.extractBalances( "E:\\ACCOUNTING\\JMS\\GL04\\GL0010.DAT", chart,
                begin, end, logger );
            StmtTable stmtTable = new StmtTable
                ( chart
                , chart.getMaxLevel()
                , outfileName
                );
            int row = 0;
            Enumeration accounts = chart.acctsByChart();
            while ( accounts.hasMoreElements() )
            {
//                Account account = (Account)accounts.nextElement();
//                
//                if ( account.getEndBal() == 0L ) continue;
//                table.addCell( new Cell( account.getNormalizedAcctNo() ), row, 0 );
//                table.addCell( new Cell( account.getDescr() ), row, 1 );
//                if ( account.getAcctClass().equals( "L" ) || account.getAcctClass().equals( "I" ) )
//                    amount = new Cell( Strings.formatPennies( -account.getEndBal(), "," ) );
//                else amount = new Cell( Strings.formatPennies( account.getEndBal(), "," ) );
//                amount.setHorizontalAlignment( Cell.ALIGN_RIGHT );
//                amount.setColspan( nCols - 2 - account.getLevel() );
//                table.addCell( amount );
//                ++row;
            }
            //document.add( table );
            //document.close();
            stmtTable.finish();
            Console.println( "Output is in " + outfileName );
        }
        catch (BadElementException bex)
        {   Console.println( bex.getMessage() ); }
        catch (DocumentException dx)
        {   Console.println( dx.getMessage() ); }
        catch (IOException iox)
        {   Console.println( iox.getMessage() ); }
        catch (VLException vlx)
        {   Console.println( vlx.getMessage() ); }
    }
    /*****/

    Chart chart;
    int stmtLevel;
    private Table table;
    String outfileName;
    Font font;
    Document document;
    int colWidths[];
    String dateFormat = "mmmm dd, yyyy"; // Default
    String dollarFormat = ",("; // Default
    int row;
    long levelTotals[] = new long[10];
    int nCols;
}

