package com.extant.vl2;

import com.extant.utilities.MsgBox;
import com.extant.utilities.XProperties;
import com.extant.utilities.Four11;
import com.extant.utilities.Strings;
import com.extant.utilities.Julian;
//import com.extant.utilities.MyPanel;
import com.extant.utilities.LogFile;
import com.extant.utilities.ViewFile;
import com.extant.utilities.UtilitiesException;
import com.extant.utilities.DisplayTree;
import com.extant.utilities.TreeClimber;
import org.w3c.dom.Document;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import java.awt.Container;
//import java.io.FileInputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
//import java.net.*;
//import java.awt.Image;
//import java.awt.MediaTracker;

/**
 *
 * @author jms
 */
public class VL2
    extends JFrame
    implements ActionListener
{
    // Global variables
	static final String ONEDRIVE="C:\\Users\\jms\\OneDrive\\ACCOUNTING\\";
    static String entityName=null;
    static String entityLongName=null;
    //static String entityDir=null;
    //static String entityRemoteDir=null;
    static String entityPropsFilename=null;
    static String yy=null;
    static String workDir;
    static com.extant.utilities.XProperties props;
    static String chartFilename=null;
    static Chart chart=null;
    static ChartTree chartTree=null;
    static JTree chartJTree=null;
    static String glFilename=null;
    static GLCheck glCheck=null;
    static Julian EarliestDate;
    static Julian LatestDate;
    static String cashAcctNo;
    static LogFile logger=null;
    
    XProperties aprops;   // accounting props

    JFrame VL2MenuFrame = this;

    public void VL2Init(String args[])
    {
        System.out.println("Enter VL2Init ONEDRIVE=" + ONEDRIVE);
        
//        {   // Display Logo only on initial call
//            try
//            {
//                Image logo=null;
//                System.out.println("Logo section ...");
//                Toolkit toolkit = Toolkit.getDefaultToolkit();
//                URL logoSrc = new URL("http://extantinvestments.com/img/Extant4Small.jpg");
//                logo = toolkit.getImage( logoSrc );
//                if ( logo == null ) throw new Exception( "logo image is null" );
//                MediaTracker mediaTracker = new MediaTracker( this );
//                if ( mediaTracker == null ) throw new Exception( "mediaTracker is null!");
//                mediaTracker.addImage( toolkit.getImage(logoSrc), 1);
//                mediaTracker.waitForID( 0, 10000 );
//                MyPanel logoPanel = new MyPanel( logo, MyPanel.CENTER );
//                Container contentPane = getContentPane();
//                contentPane.add( logoPanel, 0 );
//                logoPanel.setVisible(true);
//            }
//            catch (Exception x)
//            {   System.out.println( "cannot display logo: " + x.getMessage() ); }
//            System.out.println( "finished with logo" );
//        }
//
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        VL2MenuFrame.setLocation(
            (((int)screenSize.getWidth()  - 550)/2),
            (((int)screenSize.getHeight() - 450)/2)
            );
        VL2MenuFrame.setPreferredSize(new Dimension(550, 450));
        if (VL2MenuFrame == null) 
        {
            System.out.println("Entering VL2Init: VL2MenuFrame is null");
            System.exit(106);
        }
        
        // Locate the accountingRoot & accounting.properties file
        // accountingRoot is:
        //     a top-level folder named 'ACCOUNTING' on some disk
        //     which contains a file named 'accounting.properties'
        // We expect this to be on ONEDRIVE
        String accountingRoot = ONEDRIVE;
/*****        
//        String trial;
//        for (int i=0; i<Strings.ALPHA_UPPER.length(); ++i)
//        {
//            String disk = Strings.ALPHA_UPPER.substring( i, i+1 );
//            if ( new File( disk + ":\\ACCOUNTING" ).exists() )
//            {
//                trial = disk + ":\\ACCOUNTING\\";
//                if (new File( trial + "accounting.properties").exists())
//                    if (accountingRoot != null)
//                    {
//                        System.out.println("multiple accounting roots:");
//                        System.out.println("    " + accountingRoot + "and " + trial);
//                        System.exit(102);
//                    }
//                    else
//                    {
//                        accountingRoot = trial;
//                        System.out.println("accountingRoot="+accountingRoot);
//                        try { aprops = new XProperties(trial+"accounting.properties"); }
//                        catch (IOException iox)
//                        { 
//                            System.out.println("unable to open " + trial + "accounting.properties");
//                            System.exit(101); // Cannot open accounting.properties
//                        }
//                    }
//                else if (secondaryAccountingRoot != null)
//                {
//                    System.out.println("multiple secondary accounting roots");
//                    System.exit(104);
//                }
//                else
//                {
//                    secondaryAccountingRoot = trial;
//                    System.out.println("secondaryAccountingRoot="+trial);
//                }
//
//                if ((primaryAccountingRoot != null) && (secondaryAccountingRoot != null))
//                    break;
//            }
//            }
//        }
 *****/

        try { aprops = new XProperties(accountingRoot + "accounting.properties"); }
        catch (IOException iox)
            {
                System.out.println("Error opening " + accountingRoot+"accounting.properties: "+ iox.getMessage());
                System.exit(105);
            }

        // Initialize LogFile (to append)
        String logFilename = accountingRoot + "VL2.log";
        logger = new LogFile(logFilename, true);
        logger.log("Log File " + logFilename + " opened");
        logger.log("***** STARTING VL2 " + new Julian().toString("") + " *****");
        
        // For Debugging
        //logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

        MsgBox msgBox = new MsgBox
            ( VL2MenuFrame
            , "Entity Name"
            , "Enter Entity Short Name"
            , ""
            , new String[] {"OK", "Cancel"}
            );
        if (msgBox.getCommand().equals("Cancel"))
        	logger.logFatal("User Cancel (Enter Entity Name)");
        entityName = msgBox.getResponse().toUpperCase();
        String entityRoot;
        entityRoot = accountingRoot + entityName + "\\";
        
        // Set properties file name for this entity
        entityPropsFilename = entityRoot + entityName + ".properties";
        try { props = new XProperties( entityPropsFilename ); }
        catch (IOException iox)
        {  	logger.logFatal("Could not open " + entityPropsFilename); }

        msgBox = new MsgBox
            ( VL2MenuFrame
            , "Year"
            , "Enter the year (yy)"
            , ""
            , new String[] {"OK", "Cancel"}
            );
        if (msgBox.getCommand().equals("Cancel"))
        	logger.logFatal("User Cancel (Enter year)");
        yy = msgBox.getResponse();
        
/*****
        // Get entity props & initialize

        logger.logDebug("entityPropertiesFile="+entityPropertiesFile);
        try { props = new XProperties(entityPropertiesFile); }
        catch (IOException iox)
        { logger.logFatal("Cannot open entity XProperties File (" + 
                entityPropertiesFile + ") " + iox.getMessage()); }
        props.setProperty("PropertiesFile", entityPropertiesFile);
*****/
        // Find working directory & entity files for the selected entity
        cashAcctNo = props.getString("CashAcctNo");
        if (cashAcctNo == null) logger.logFatal("cashAcctNo is null!");
        if (cashAcctNo.length() < 4)
        	logger.logFatal("Invalid CashAcctNo: " + cashAcctNo);
        workDir = entityRoot + "GL" + yy + "\\";
        if (!new File( workDir ).exists())
            logger.logFatal("Unable to find workDir: " + workDir);
        props.setProperty("WorkDir", workDir);
        props.setProperty("yy", yy);
        props.setProperty("GLFile", workDir + "GL0010.DAT");
        props.setProperty("CustomerList", workDir + "CUSTOMER.LST");
        props.setProperty("VendorList", workDir + "VENDOR.LST");
        props.setProperty("ContactsList", workDir + "CONTACTS.LST");
        props.setProperty("ChartXML", workDir + "CHART.XML");
        props.setProperty("GSNFile", workDir + "gsnNVFile.txt");

        // Make a progress report
        //logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
        if (logger.getLogLevel() >= LogFile.DEBUG_LOG_LEVEL) 
            logger.whereAreWe(new Error());
        logger.logDebug("Reporting:");
        logger.logDebug("accounting.props="+accountingRoot + "accounting.properties");
        logger.logDebug("entityName="+entityName);
        logger.logDebug("EntityLongName="+props.getString("VL.EntityLongName"));
        logger.logDebug("entityPropsFilename="+entityPropsFilename);
        logger.logDebug("entityRoot="+entityRoot);
        logger.logDebug("WorkDir="+props.getString("WorkDir"));
        //logger.logDebug("CashAcctNo="+props.getString("VL.CashAcctNo"));
        logger.logDebug("test retrieving from props: EntityLongName=" + props.getProperty("EntityLongName"));
        
        VL2MenuFrame.setTitle(props.getString("EntityLongName"));

        // Set Initial GSN
        new GSNMan().init(props, logger);
        logger.logInfo("GSN Initial Value: " + GSNMan.getGSN());
        
        // Report
        //logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
        logger.logDebug("entityName=" + entityName);
        logger.logDebug("year=" + yy);
        logger.logDebug("workDir=" + workDir);
        logger.logDebug("entityPropsFilename=" + entityPropsFilename);

        try
        {
            logger.logInfo( "Checking Files ..." );
            //logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
            
            // Check Chart, Build & Display Chart Tree
            chartFilename = workDir + "CHART.XML";
            logger.logDebug("Checking Chart file: " + chartFilename);
            if ( !new File( chartFilename ).exists() )
                logger.logFatal( "Unable to find " + chartFilename );
            chart = new Chart();
            chart.init( chartFilename, logger );  // throws IOException & VLException
            //chartTree = new ChartTree(props, chart, logger);
        }
        catch (IOException iox)
        {   logger.logFatal("IO error checking chart: " + iox.getMessage()); }
        catch (VLException vlx)
        { logger.logFatal("VL Error checking chart: "+ vlx.getMessage()); }
//            catch (ParserConfigurationException pcx)
//        {	logger.logFatal("ParserConfigurationError: " + pcx.getMessage()); }
//          catch (SAXException sax)
//      {	logger.logFatal("SAX Exception: " + sax.getMessage()); }
        
        // Build ChartTree (initially invisible, location & size not specified)
        try { chartTree = new ChartTree(props, chart, logger); }
        catch (ParserConfigurationException|SAXException|IOException|VLException x)
        { logger.logFatal("ChartTree build: "+ x.getMessage()); }
        
        logger.logInfo("Chart initialized without error");
            
//            //Create a tree that allows one node selection at a time.
//            chartTree.getSelectionModel().setSelectionMode
//                (TreeSelectionModel.SINGLE_TREE_SELECTION);
// 
//        // Add implements TreeSelectionListener
//        // Listen for a change of selected node
//        // (This listener must be in each of the classes that use the 
//        //  ChartTree to select an account, instead of a comboBox.)
//        // It DOES NOT belong here in VL2!
//        chartTree.addTreeSelectionListener(this);
//       
//        chartTree.addTreeSelectionListener(new TreeSelectionListener()
//            {
//                public void valueChanged(TreeSelectionEvent e)
//                {
//                    DefaultMutableTreeNode node =
//                        (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
//                }
//            )

            // Point is (x,y)
            // Dimension is (width, height)
            Point location = this.getLocation();
            location.x += (this.getWidth() + 375); // why 375?  because it works.
            // location.y is unchanged
            Dimension size = new Dimension(500, 370);
            chartJTree = chartTree.getJTree();
            displayTree = new DisplayTree("Chart of Accounts", chartJTree, location, size);
            displayTree.setVisible(true);
            
            logger.logInfo("Chart tree initialization & display complete.");

            // Check gl file
            glFilename = workDir + "GL0010.DAT";
            logger.logDebug("Checking GL File " + glFilename);
            if ( !new File( glFilename ).exists() )
                logger.logFatal( "Unable to find " + glFilename );
            glCheck = new GLCheck();
            glCheck.glCheck(glFilename, chart, "token", props, logger);
            if ( glCheck.getNErrors() > 0 )
            {
                System.out.println( "GL File Error Report:" );
                System.out.print( glCheck.getReport() );
                logger.logFatal("Error(s) in " + glFilename);
            }
            else logger.logInfo( "No errors found in " + glFilename );

            if (glCheck.getNEntries() > 0)
            {
                // Properties file dates have been updated by GLCheck
                //logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
                //logger.logDebug("(before update) EarliestDate="+props.getString("EarliestDate"));
                //logger.logDebug("(before update) LatestDate="+props.getString("LatestDate"));
                //props.setProperty("EarliestDate", glCheck.earliestDate.toString("yymmdd"));
                //props.setProperty("LatestDate", glCheck.latestDate.toString("yymmdd"));
                //logger.logDebug("(after update) EarliestDate="+props.getString("EarliestDate"));
                //logger.logDebug("(after update) LatestDate="+props.getString("LatestDate"));
//                try
//                {
//                    String tempFilename = props.getString("EntityRoot"+"propsCopy.txt")cd g:
//                    cdg;
//                    props.store(tempFilename, "for reload");
//                    String propsFilename = props.getString("PropertiesFile");
//                    logger.logDebug("propsFilename="+propsFilename);
//                    FileInputStream fis = new FileInputStream(new File(tempFilename));
//                    props.load(fis);
//                    fis.close();
//                }
//                catch (IOException iox)
//                {   logger.logFatal( "VL2: error found in GL File" + iox.getMessage()); }
                
                logger.logInfo("All Checks Complete - No Errors Detected.");
            }
    }

    public void VL2Start(String[] args)
    {
        logger.logInfo("Enter VL2Start");
        //if (VL2MenuFrame == null) logger.logDebug("Entering VL2Start: VL2MenuFrame is null!");
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        int screenWidth = (int)screenSize.getWidth();
//        int screenHeight = (int)screenSize.getHeight();
//        int xCenter = screenWidth/2;
//        int yCenter = screenHeight/2;

        // Create the main menu bar
        JMenuBar greenMenuBar = new JMenuBar();
    
        JMenu muFile = new JMenu("File");
//            JMenuItem muChangeDir = new JMenuItem("Change Dir");
//            muChangeDir.addActionListener(this);
//            muFile.add(muChangeDir);
            
            JMenuItem muViewDir = new JMenuItem("Directory Tree");
            muViewDir.addActionListener(this);
            muFile.add(muViewDir);
            
//            JMenuItem muViewChart = new JMenuItem("Chart Tree");
//            muViewChart.addActionListener(this);
//            muFile.add(muViewChart);
            
            JMenuItem muPrintCheck = new JMenuItem("Print Check");
            muPrintCheck.addActionListener(this);
            muFile.add(muPrintCheck);
            
            JMenuItem muExit = new JMenuItem("Exit");
            muExit.addActionListener(this);
            muFile.add(muExit);
            
        greenMenuBar.add(muFile);

        JMenu muTools = new JMenu("Tools");
            JMenuItem muEditVendor = new JMenuItem("Edit Vendor List");
            muTools.add(muEditVendor);
            muEditVendor.addActionListener(this);
            
            JMenuItem muEditCust = new JMenuItem("Edit Customer List");
            muTools.add(muEditCust);
            muEditCust.addActionListener(this);

            JMenuItem muListProps = new JMenuItem("List Properties");
            muTools.add(muListProps);
            muListProps.addActionListener(this);
            
            JMenuItem muClimbChartTree = new JMenuItem("Climb Chart Tree");
            muTools.add(muClimbChartTree);
            muClimbChartTree.addActionListener(this);
            
        greenMenuBar.add(muTools);

        JMenu muTrans = new JMenu("Enter Transactions");
            JMenuItem muReceipts = new JMenuItem("Receipts");
            muTrans.add(muReceipts);
            muReceipts.addActionListener(this);
            
            JMenuItem muDisb = new JMenuItem("Disbursements");
            muTrans.add(muDisb);
            muDisb.addActionListener(this);
            
            JMenuItem muJE = new JMenuItem("Journal Entries");
            muTrans.add(muJE);
            muJE.addActionListener(this);
            
        greenMenuBar.add(muTrans);

        JMenu muAudit = new JMenu("Audit");
            JMenuItem muBal = new JMenuItem("Account Balance");
            muAudit.add(muBal);
            muBal.addActionListener(this);
            
            JMenuItem muAnalyze = new JMenuItem("Analyze");
            muAudit.add(muAnalyze);
            muAnalyze.addActionListener(this);
            
            JMenuItem muValidate = new JMenuItem("Validate");
            muAudit.add(muValidate);
            muValidate.addActionListener(this);
            
//            JMenuItem muSearch = new JMenuItem("Search");
//            muAudit.add(muSearch);
//            muSearch.addActionListener(this);
            
        greenMenuBar.add(muAudit);
            
        JMenu muReports = new JMenu("Reports");
            JMenuItem muTextStmt = new JMenuItem("Text Statement");
            muReports.add(muTextStmt);
            muTextStmt.addActionListener(this);
            
            JMenuItem muPDFStmt = new JMenuItem("PDF Statement");
            muReports.add(muPDFStmt);
            muPDFStmt.addActionListener(this);
            
            JMenuItem muTransSummary = new JMenuItem("Transaction Summary");
            muReports.add(muTransSummary);
            muTransSummary.addActionListener(this);

            JMenuItem muTransDetail = new JMenuItem("Transaction Details");
            muReports.add(muTransDetail);
            muTransDetail.addActionListener(this);
            
            JMenuItem muPrintChart = new JMenuItem("Print Chart");
            muReports.add(muPrintChart);
            muPrintChart.addActionListener(this);
            
        greenMenuBar.add(muReports);

        greenMenuBar.setOpaque(true);
        greenMenuBar.setBackground(new Color(154, 165, 127));
        greenMenuBar.setPreferredSize(new Dimension(500, 30));

        // Set the menu bar
        VL2MenuFrame.setJMenuBar(greenMenuBar);
        // Display the frame
        VL2MenuFrame.pack();
        VL2MenuFrame.setVisible(true);
    }
    
        //Process main menu clicks
        JTree tree=null;
        DisplayTree displayTree = null;
        
        @Override
        public void actionPerformed(ActionEvent evt)
        {
            String command = evt.getActionCommand();
            logger.log("MainMenu command: "+ command);
            
        // File
            if (command.equals("Directory Tree"))
            {
                try
                {
                    Four11 four11 = new Four11("C:\\Users\\jms\\OneDrive\\ACCOUNTING");
                    four11.showTree();
                }
                catch (UtilitiesException ux)
                {
                    logger.log("Directory Tree failed: "+ ux.getMessage());
                }
            }

            else if (command.equals("Print Check"))
            {
                CheckPrinter2 cp2 = new CheckPrinter2();
                cp2.CheckPrinter2();
                cp2.setSize(800,600);
                cp2.setLocation(600,200);
                cp2.pack();
                cp2.setVisible(true);
            }

            else if (command.equals("Exit"))
            {
                VL2MenuFrame.dispose();
                System.exit(0);
            }
            
        // Tools
            else if (command.equals("Options"))
            {
                listProps(props);
            }
            
            else if (command.equals("Edit Vendor List"))
            {
                Container contentPane;
                contentPane = this.getContentPane();
                contentPane.setPreferredSize(new Dimension(600, 450));
                contentPane = new ListMan4(props, workDir+"VENDOR.LST");
                VL2MenuFrame.pack();
                contentPane.setVisible(true);
            }
            else if (command.equals("Edit Customer List"))
            {
                Container contentPane;
                contentPane = new ListMan4(props, workDir+"CUSTOMER.LST", false);
                contentPane.setPreferredSize(new Dimension(500, 450));
                VL2MenuFrame.pack();
                contentPane.setVisible(true);
            }
            else if (command.equals("List Properties"))
                listProps(props);

            else if (command.equals("Climb Chart Tree"))
            {
                TreeClimber treeClimber = new TreeClimber();
                try
                { 
                    Document document = treeClimber.buildDocument(chartFilename, logger, rootPaneCheckingEnabled);
                    treeClimber.exploreTree(document);
                }
                catch (UtilitiesException ux)
                {
                    System.out.println("treeClimber failed: " + ux.getMessage());
                }
            }
            
        // Enter Transactions
            else if (command.equals("Receipts") || command.equals("Disbursements"))
            {
                JPanel contentPanel=null;
                try { contentPanel = new EnterTransactionPanel(chart, logger, command, props); }
                catch (IOException iox) { logger.logFatal(iox.getMessage()); }
                contentPanel.setPreferredSize(new Dimension(500, 450));
                VL2MenuFrame.setContentPane(contentPanel);
                VL2MenuFrame.pack();
                VL2MenuFrame.setVisible(true);
            }
            else if (command.equals("Journal Entries"))
            {
                JPanel contentPanel;
                contentPanel = new EnterJournalTransaction(chart, props, logger);
                contentPanel.setPreferredSize(new Dimension(500, 450));
                VL2MenuFrame.setContentPane(contentPanel);
                VL2MenuFrame.pack();
                VL2MenuFrame.setVisible(true);
            }

        // Audit
            else if (command.equals("Account Balance"))
                new ShowBal(VL2.chart, chartTree, props);
            else if (command.equals("Analyze"))
                new Analyze(this, true, VL2.chart, props.getString("GLFile"));
            else if (command.equals("Validate"))
                ;
            else if (command.equals("Search"))
                new Search(props);

        // Reports
            else if (command.equals("Text Statement"))
                startTextStmt();
            else if (command.equals("PDF Statement"))
                startPDFStmt();
            else if (command.equals("Transaction Summary"))
                startTranReport(TranReport.SUMMARY, props, logger);
            else if (command.equals("Transaction Details"))
                startTranReport(TranReport.DETAIL, props, logger);
            else if (command.equals("Print Chart"))
                startPrintChart();


            else System.out.println("This command (" + command + ") is not implemented");
        }

        private void startTextStmt()
        {
            //logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
            logger.logDebug("Starting StatementTXT");
            logger.logDebug("GLFile="+props.getString("GLFile"));
            logger.logDebug("Entity: "+ props.getString("EntityLongName"));
            try
            {   
                StatementTXT statement = new StatementTXT
                    ( props
                    , VL2.chart
                    , props.getString("GLFile")
                    , new Julian(props.getString("EarliestDate"))
                    , new Julian(props.getString("LatestDate"))
                    , 0
                    , workDir+"Stmt.txt"
                    , VL2.logger
                    );
                statement.makeStatement();
                String fileName = workDir+"Stmt.txt";
                logger.logInfo("Statement is in "+fileName);
                new ViewFile(fileName, logger);
            }
            catch (VLException vlx)
            {   logger.log("Cannot initialize StatementTXT: "+vlx.getMessage());
                vlx.printStackTrace();
                return;
            }
            catch (UtilitiesException ux)
            {
                logger.log("Cannot initialize ViewFile: "+ux.getMessage());
                return;
            }
        }

        private void listProps(XProperties props)
        {
            if (props == null) System.out.println("props is null");
            else System.out.println("props is not null");
            if (logger == null) System.out.println("logger is null!");
            else System.out.println("logger is not null");
            if (chart == null) System.out.println("chart is null!");
            else System.out.println("chart is not null");
            props.list(System.out);
        }

        private void startPDFStmt()
        {
            try
            {
                logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
                logger.logDebug("Starting PDFStmt");
                logger.logDebug("GLFile="+props.getString("GLFile"));
                String outFilename = workDir+"StmtPDF.pdf";
                StatementPDF statement = new StatementPDF
                       ( props
                       , VL2.chart
                       , props.getString("GLFile")
                       , new Julian(props.getString("EarliestDate"))
                       , new Julian(props.getString("LatestDate"))
                       , 0             // report level
                       , outFilename   // outfile name
                       , VL2.logger
                       );
                statement.makeStatement();
                
                logger.logDebug("Statement is in file "+outFilename);
                new ViewFile(outFilename, logger);
            }
            catch (VLException vlx)
            {  logger.log("Failure in StatementPDF: " + vlx.getMessage());}
            catch (UtilitiesException ux)
            {   logger.log("Cannot initialize ViewFile: " + ux.getMessage()); }
        }

        private void startTranReport(int reportType, XProperties props, LogFile logger)
        {
            String outFilename = null;
            try
            {
                logger.logDebug("TranReport.DETAIL="+TranReport.DETAIL);
                if (reportType == TranReport.DETAIL) outFilename = workDir+"DetailTranReport.txt";
                else if (reportType == TranReport.SUMMARY) outFilename = workDir+"SummaryTranReport.txt";
                TranReport tranReport = new TranReport(reportType, chart, props, logger);
            }
            catch (IOException iox)
                { logger.log("@startTranReport: " + iox.getMessage()); }
            catch (VLException vlx)
                {logger.log("@startTranReport: " + vlx.getMessage()); }
        
            try { ViewFile viewFile = new ViewFile(outFilename, logger);
                viewFile.setVisible(true); }
            catch (UtilitiesException ux) { logger.log("Internal Error: startTranReport@512 ("+ ux.getMessage()+")"); }
        }
        
        public void startPrintChart()
        {
            System.out.println("enter startPrintChart");
            PrintChart printChart = new PrintChart();
            //try
            //{
                //printPDF( XProperties props, Chart chart, String outfileName, LogFile logger )
            //    printChart.printPDF(props, VL2.chart, props.getString("WorkDir")+"Chart.pdf", logger);
            //}
            // catch (VLException vlx)
            //{
            //    logger.log("Unable to print chart: " + vlx.getMessage());
            //}
            try
            {
                // For debugging:
                logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

                String outFilename = workDir + "Chart.pdf";
                StatementPDF statementPDF = new StatementPDF
                    (props, VL2.chart, glFilename, outFilename, logger);
            }
            catch (VLException vlx)
            {
                logger.log("VL2.startPrintChart failed: " + vlx.getMessage());
            }
        }

    public static void main(String[] args)
    {
        VL2 vl2 = new VL2();
        vl2.VL2Init(args);
        vl2.VL2Start(args);
    }
}
