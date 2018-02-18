/*
 * PrintChart.java
 * Produces a listing of the Chart of Accounts in .pdf format.
 *
 * Created on May 10, 2003, 2:48 PM
 */

package com.extant.vl2;
import com.extant.utilities.*;
//import com.extant.utilities.Console;
//import com.extant.utilities.Strings;
//import com.extant.utilities.Julian;
//import com.extant.utilities.LogFile;
////import com.extant.utilities.VLException;
//import com.extant.utilities.Clip;
//import com.extant.utilities.ViewFile;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;

/**
 *
 * @author  jms
 */
public class PrintChart
{
    // Local variables
    XProperties props;
    String workDir;
    String glFilename;
    Chart chart;
    String outfileName;
    LogFile logger;
    long fakeAmount = 100;
    StatementPDF statement;
    boolean showacctSave;
    StmtTable stmtTable;

    public void printPDF( XProperties props, Chart chart, String outfileName, LogFile logger )
    throws VLException
    {
        this.logger = logger;
        if (logger == null) throw new VLException(VLException.NULL_LOGGER, "printPDF");
        // For debugging:
        //logger.setLogAll(true);

        this.props = props;
        this.chart = chart;
        workDir = props.getProperty( "WorkDir" );
        glFilename = props.getProperty("GLFile");
        if ( outfileName.contains( File.separator ) ) this.outfileName = outfileName;
        else this.outfileName = workDir + outfileName;
        showacctSave = chart.setShowacct( false );
        int reportLevel = chart.getMaxLevel();
        Julian begin = new Julian( "1/1/20" + new Julian().toString( "yy" ) );
        Julian end = new Julian();
        statement = new StatementPDF( props, chart, outfileName, logger );
        stmtTable = statement.initialize( reportLevel, outfileName );
        statement.levelTotals = new long[10];
        for (int i=0; i<statement.levelTotals.length; ++i)
            statement.levelTotals[i] = 0L;
    }

    public void doit()
    throws IOException, VLException, BadElementException, DocumentException
    {
        Enumeration elements = chart.chartElements();
        while ( elements.hasMoreElements() )
        {
            ChartElement element = (ChartElement)elements.nextElement();
            logger.logDebug( "[PrintChart.doit] element: " + element.toString() );
            // Do not print the out-of-balance warnings
            if ( element.getAttribute( "title" ).startsWith( "*****" ) ) continue;
            if ( element.name.equals( "account" ) || element.name.equals( "total" ) )
            {
                int level = element.getLevel();
                if ( element.name.equals( "total" ) ) --level;
                Account account = new Account( element.getAttribute( "no" ), Strings.format( level, "00" ),
                    element.getAttribute( "type" ), element.getAttribute( "title" ) );

                account.zeroBalances();
                String acctType = account.getType();
                if ( Strings.validateChar( acctType.charAt( 0 ), "AE" ) )
                    account.addToBeginBal( fakeAmount );
                else account.addToBeginBal( -fakeAmount );
                statement.printAmountLine( element, account );
            }
            else statement.printTextLine( element );
        }
        stmtTable.finish();
        chart.setShowacct( showacctSave );
    }
}

/**
 * @param args the command line arguments
 * ini=<iniFileName>
 * dir=<workDirectory>
 * o=<output file name>
 */
/***** FOR TESTING *****
    public static void main(String[] args)
    {   
        try
        {
            PrintChart printChart = new PrintChart();
            Clip clip = new Clip( args, new String[]
                { "ini=E:\\ACCOUNTING\\EXTANT\\EXTANT.properties"
                , "dir=E:\\ACCOUNTING\\EXTANT\\GL17\\"
                , "o=CHART.PDF"
                } );
            XProperties props = new XProperties( clip.getParam( "ini" ), "VL" );
            String workDir = clip.getParam( "dir" );
            props.setProperty( "WorkDirectory", workDir );
            Chart chart = new Chart();
            chart.init( props.getString( "ChartFile"), logger );
            String filename = workDir + clip.getParam( "o" );
            printChart.printPDF( props, chart, filename, logger );
            printChart.doit();
            Console.println( "Output is in file " + filename );
            new ViewFile( filename, logger ).setVisible( true );
        }
        catch (Exception x)
        {   Console.println( x.getMessage() );
            x.printStackTrace();
        }
    }
}

*****/

