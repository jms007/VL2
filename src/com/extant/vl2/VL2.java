package com.extant.vl2;

import com.extant.utilities.MsgBox;
import com.extant.utilities.XProperties;
import com.extant.utilities.Four11;
import com.extant.utilities.Julian;
import com.extant.utilities.MyPanel;
import com.extant.utilities.LogFile;
import com.extant.utilities.ViewFile;
import com.extant.utilities.UtilitiesException;
import com.extant.utilities.DisplayTree;
import com.extant.utilities.TreeClimber;
import com.extant.utilities.Strings;
import com.extant.utilities.UsefulFile;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import org.w3c.dom.Document;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Container;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.net.*;
import java.awt.Image;
import java.awt.MediaTracker;

/**
 *
 * @author jms
 */
public class VL2 extends JFrame implements ActionListener
{
	public VL2()
	{
	}

	public static VL2Config vl2Config;

	// Global variables
	static final String BRANCH = "Post_07-23-18";
	static String ACCOUNTING_DIR;
	static String entityName = null;
	static String entityLongName = null;
	static String entityPropsFilename = null;
	static String yy = null;
	static String workDir;
	static String chartFilename = null;
	static Chart chart = null;
	static ChartTree chartTree = null;
	static JTree chartJTree = null;
	static GLChecker glChecker = null;
	static Julian earliestDate;
	static Julian latestDate;
	static int maxGSN;
	static String cashAcctNo;
	static LogFile logger;

	XProperties aprops; // accounting props

	JFrame VL2MenuFrame = this;

	/**
	 * Main Method for application VL2
	 * 
	 * @param args
	 *            Currently not used
	 * @throws IOException
	 */
	public void VL2Init(String args[]) throws IOException
	{
		DisplayTree displayTree;

		System.out.println("Enter VL2Init");

		{ // Display Logo only on initial call
			try
			{
				Image logo = null;
				System.out.println("Logo section ...");
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				URL logoSrc = new URL("http://extantinvestments.com/img/Extant4Small.jpg");
				logo = toolkit.getImage(logoSrc);
				if (logo == null)
					throw new Exception("logo image is null");
				MediaTracker mediaTracker = new MediaTracker(this);
				// if (mediaTracker == null)
				// throw new Exception("mediaTracker is null!");
				mediaTracker.addImage(toolkit.getImage(logoSrc), 1);
				mediaTracker.waitForID(0, 10000);
				MyPanel logoPanel = new MyPanel(logo, MyPanel.CENTER);
				Container contentPane = getContentPane();
				contentPane.add(logoPanel, 0);
				logoPanel.setVisible(true);
			} catch (Exception x)
			{
				System.out.println("cannot display logo: " + x.getMessage());
			}
			// System.out.println("finished with logo");
		}

		Strings.printVersion();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		VL2MenuFrame.setLocation((((int) screenSize.getWidth() - 550) / 2), (((int) screenSize.getHeight() - 450) / 2));
		VL2MenuFrame.setPreferredSize(new Dimension(550, 450));
		if (VL2MenuFrame == null)
		{
			System.out.println("VL2[106]: VL2MenuFrame is null");
			System.exit(106);
		}

		String logFilename = VL2Config.getAccountingDataDirectory() + "VL2.log";
		logger = new LogFile(logFilename, true);
		logger.log("Log File " + logFilename + " opened");
		logger.log("***** STARTING VL2 [branch " + BRANCH + "] " + new Julian().toString("") + " *****");

		// For Debugging
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

		MsgBox msgBox = new MsgBox(VL2MenuFrame, "Entity Name", "Enter Entity Short Name", "",
				new String[] { "OK", "Cancel" });
		if (msgBox.getCommand().equals("Cancel"))
			logger.logFatal("User Cancel (Enter Entity Name)");
		entityName = msgBox.getResponse().toUpperCase();
		String entityPropFilename = VL2Config.getAccountingDataDirectory() + entityName + "\\" + entityName
				+ ".properties";
		XProperties entityProps = new XProperties(entityPropFilename);
		vl2Config = new VL2Config(entityPropFilename, entityName);

		String defaultYear = entityProps.getProperty("CurrentYear");
		msgBox = new MsgBox(VL2MenuFrame, "Year", "Enter the year (yy)", defaultYear, new String[] { "OK", "Cancel" });
		if (msgBox.getCommand().equals("Cancel"))
			logger.logFatal("User Cancel (Enter year)");
		yy = msgBox.getResponse();
		vl2Config.setCurrentYear(yy);

		// Set printOrientation
		vl2Config.setPrintOrientation("portrait");

		// Set Initial GSN
		new GSNMan(vl2Config, logger);
		logger.logInfo("GSN Initial Value: " + GSNMan.getGSN());

		// Debug Report
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		logger.logDebug("entityName=" + entityName);
		logger.logDebug("year=" + yy);
		logger.logDebug("workDir=" + workDir);
		logger.logDebug("entityPropsFilename=" + entityPropsFilename);
		logger.logDebug("printOrientation=" + vl2Config.getPrintOrientation());

		logger.logInfo("Checking Files ...");
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

		try
		{
			// Check Chart file
			chartFilename = vl2Config.getChartFile();
			logger.logDebug("Checking Chart file: " + chartFilename);
			if (!new File(chartFilename).exists())
				logger.logFatal("Unable to find " + chartFilename);
			chart = new Chart();
			chart.init(chartFilename, logger); // throws IOException & VLException
		} catch (IOException iox)
		{
			logger.logFatal("IO error checking chart: " + iox.getMessage());
		} catch (VLException vlx)
		{
			logger.logFatal("VL Error checking chart: " + vlx.getMessage());
		}
		logger.logInfo("Chart initialized without error");

		// Check GL file

		// For Debugging:
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

		// Check transactions and post transactions to chart elements
		String GLFilename = vl2Config.getGLFile();
		logger.logDebug("Checking GLFile " + GLFilename);
		if (!new File(GLFilename).exists())
			logger.logFatal("Unable to find " + GLFilename);
		glChecker = new GLChecker();
		glChecker.glCheck(chart, "token", vl2Config);
		earliestDate = glChecker.getEarliestDate();
		latestDate = glChecker.getLatestDate();

		// Test for consistent GSN
		if ((maxGSN + 1) != Strings.parseInt(GSNMan.getGSN()))
			logger.logFatal("GSN mismatch: GSNMan=" + GSNMan.getGSN() + " maxGSN=" + maxGSN);
		logger.logDebug("Next GSN=" + GSNMan.getGSN() + " verified");

		// Compute Element totals
		VLUtil.computeElementTotals(chart);

		// Set retained earnings balances
		// plElement is the element for the (unique) type 'R' account
		// ChartElement plElement = chart.getPLElement();
		// the beginBal value should be set by GLChecker (see lines 109 ff)
		// the deltaBal value should be set by chart ...
		logger.log("VL2 206:  plElement=" + chart.getPLElement().toString());

		// Debugging tool to display the current element list
		// String workDir = vl2Config.getWorkingDirectory();
		// VLUtil.showElementList(chart, workDir + "ElementList.txt");

		// For Debugging - BuildDetailedStatusReport has been disabled
		// Build Detailed Status Report & Show results
		// VL2DetailedStatusReport vl2DetailedStatusReport = new
		// VL2DetailedStatusReport();
		// vl2DetailedStatusReport.initialize(vl2Config, logger);
		// vl2DetailedStatusReport.run();
		// vl2DetailedStatusReport.showReport();

		try
		{ // Build ChartTree from Elements
			chartTree = new ChartTree(vl2Config, chart, logger);

			// Position chart tree in display
			// Point is (x,y) Dimension is (width, height)
			Point location = this.getLocation();
			location.x += (this.getWidth() + 375); // why 375? because it works.
			// location.y is unchanged
			Dimension size = new Dimension(500, 370);
			chartJTree = chartTree.getJTree();
			displayTree = new DisplayTree("Chart of Accounts", chartJTree, location, size);
			displayTree.setVisible(true);
			logger.logInfo("Chart tree initialization & display complete.");
		} catch (ParserConfigurationException | SAXException | IOException | VLException x)
		{
			logger.logFatal("ChartTree build exception: " + x.getMessage());
		}
	}

	public void VL2Start(String[] args)
	{
		logger.logInfo("Enter VL2Start");

		// Create the main menu bar
		JMenuBar greenMenuBar = new JMenuBar();

		JMenu muFile = new JMenu("File");

		JMenuItem muViewDir = new JMenuItem("Directory Tree");
		muViewDir.addActionListener(this);
		muFile.add(muViewDir);

		JMenuItem muPrintCheck = new JMenuItem("Print Check");
		muPrintCheck.addActionListener(this);
		muFile.add(muPrintCheck);

		JMenuItem muTest = new JMenuItem("Test");
		muTest.addActionListener(this);
		muFile.add(muTest);

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

		JMenuItem muSearch = new JMenuItem("Search");
		muAudit.add(muSearch);
		muSearch.addActionListener(this);

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

	// Process main menu clicks
	// JTree tree = null;
	// DisplayTree displayTree = null;

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String command = evt.getActionCommand();
		logger.log("MainMenu command: " + command);

		// File
		if (command.equals("Directory Tree"))
		{
			try
			{
				Four11 four11 = new Four11(VL2Config.getAccountingDataDirectory());
				four11.showTree();
			} catch (UtilitiesException ux)
			{
				logger.log("Directory Tree failed: " + ux.getMessage());
			}
		}

		else if (command.equals("Print Check"))
		{
			CheckPrinter2 cp2 = new CheckPrinter2(vl2Config);
			cp2.setSize(800, 600);
			cp2.setLocation(600, 200);
			cp2.pack();
			cp2.setVisible(true);
		}

		else if (command.equals("Test"))
		{
			// logger.logFatal("Command Test is not implemented");
			logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
			logger.log("PLElement: " + chart.getPLElement().toString());
			logger.log("TLNW: " + chart.findTagElement("TLNW"));
			logger.setLogLevel(LogFile.NORMAL_LOG_LEVEL);
		}

		else if (command.equals("Exit"))
		{
			VL2MenuFrame.dispose();
			// Close the log file
			if (logger != null)
			{
				logger.log("Closing VL2.log file");
				UsefulFile logFile = logger.getLogFile();
				logFile.close();
			}
			System.exit(0);
		}

		// Tools
		else if (command.equals("Options"))
		{
			// muListProps(props);
		}

		else if (command.equals("Edit Vendor List"))
		{
			Container contentPane;
			contentPane = this.getContentPane();
			contentPane.setPreferredSize(new Dimension(600, 450));
			contentPane = new ListMan4(vl2Config, ListType.VENDOR);
			VL2MenuFrame.pack();
			contentPane.setVisible(true);
		} else if (command.equals("Edit Customer List"))
		{
			Container contentPane;
			contentPane = new ListMan4(vl2Config, ListType.CUSTOMER, false);
			contentPane.setPreferredSize(new Dimension(500, 450));
			VL2MenuFrame.pack();
			contentPane.setVisible(true);
		} else if (command.equals("List Properties"))
			vl2Config.listAllProperties();

		else if (command.equals("Climb Chart Tree"))
		{
			TreeClimber treeClimber = new TreeClimber();
			try
			{
				Document document = treeClimber.buildDocument(chartFilename, logger, rootPaneCheckingEnabled);
				treeClimber.exploreTree(document);
			} catch (UtilitiesException ux)
			{
				System.out.println("treeClimber failed: " + ux.getMessage());
			}
		}

		// Enter Transactions
		else if (command.equals("Receipts") || command.equals("Disbursements"))
		{
			JPanel contentPanel = null;
			try
			{
				contentPanel = new EnterTransactionPanel(chart, chartTree, logger, command, vl2Config);
			} catch (IOException iox)
			{
				logger.logFatal(iox.getMessage());
			}
			contentPanel.setPreferredSize(new Dimension(500, 450));
			VL2MenuFrame.setContentPane(contentPanel);
			VL2MenuFrame.pack();
			VL2MenuFrame.setVisible(true);
		}

		// Journal Entries
		else if (command.equals("Journal Entries"))
		{
			if (logger == null)
				System.out.println("VL:Journal Entries command: logger is null!");
			JPanel contentPanel;
			contentPanel = new EnterJournalTransaction(chart, chartTree, vl2Config, logger);
			contentPanel.setPreferredSize(new Dimension(500, 450));
			VL2MenuFrame.setContentPane(contentPanel);
			VL2MenuFrame.pack();
			VL2MenuFrame.setVisible(true);
		}

		// Audit
		else if (command.equals("Account Balance"))
			new ShowBal(VL2.chart, chartTree, vl2Config);
		else if (command.equals("Analyze"))
			new Analyze(this, false, VL2.chart, chartTree, vl2Config);
		else if (command.equals("Validate"))
			logger.logInfo("The validate command is not implemented.");
		else if (command.equals("Search"))
			new Search(vl2Config);

		// Reports
		else if (command.equals("Text Statement"))
			startTextStmt();
		// else if (command.equals("Print Chart"))
		// startTextStmt(vl2Config.getWorkingDirectory() + "chart.txt",
		// vl2Config.getPrintOrientation(), true, false);
		// else if (command.equals("PDF Statement"))
		// startPDFStmt();
		else if (command.equals("Transaction Summary"))
			startTranReport(TranReport.SUMMARY, vl2Config, logger);
		else if (command.equals("Transaction Details"))
			startTranReport(TranReport.DETAIL, vl2Config, logger);

		else
			System.out.println("This command (" + command + ") is not implemented");
	}

	// showAccount (means show account numbers in output) is not set in chart XML
	// it should always be true for chart listings and be optional for statements
	// showAmount (means show dollar amounts in output) is not set in chart XML
	// it should be false for chart listings and always true for statements
	private void startTextStmt()
	{
		// For debugging
		logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		logger.logDebug("starting new StatementTXT");
		StatementTXT statementTXT = new StatementTXT();
		logger.logDebug("starting statementTXT.setup");
		statementTXT.setup();
		logger.logDebug("starting statementTXT.initialize");
		statementTXT.initialize();
		logger.logDebug("starting statementTXT.makeReport");
		statementTXT.makeReport();
		logger.logDebug("makeReport completed");
	}

	// private void startPDFStmt()
	// {
	// try
	// {
	// // For Debugging:
	// // logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
	// logger.logDebug("Starting PDFStmt");
	// logger.logDebug("GLFile=" + vl2Config.getGLFile());
	// String outFilename = workDir + "StmtPDF.pdf";
	// new StatementPDF(vl2Config, VL2.chart, new
	// Julian(vl2Config.getEarliestDate()),
	// new Julian(vl2Config.getLatestDate()), 0 // report level
	// , outFilename // outfile name
	// , VL2.logger);
	// // statement.makeStatement();
	//
	// logger.logDebug("Statement is in file " + outFilename);
	// new ViewFile(outFilename, logger);
	// } catch (VLException vlx)
	// {
	// logger.log("Failure in StatementPDF: " + vlx.getMessage());
	// } catch (UtilitiesException ux)
	// {
	// logger.log("Cannot initialize ViewFile: " + ux.getMessage());
	// }
	// }
	//
	private void startTranReport(int reportType, VL2Config vl2Config, LogFile logger)
	{
		// For debugging
		logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		logger.logDebug("starting startTranReport");
		String outFilename = null;
		try
		{
			workDir = vl2Config.getWorkingDirectory();
			if (reportType == TranReport.DETAIL)
				outFilename = workDir + "DetailTranReport.txt";
			else // if (reportType == TranReport.SUMMARY)
				outFilename = workDir + "SummaryTranReport.txt";
			logger.logDebug("calling new TranReport reportType=" + reportType);
			new TranReport(reportType, chart, vl2Config, logger);
			logger.logDebug("startTranReport: outFilename=" + outFilename);
		} catch (IOException iox)
		{
			logger.log("@startTranReport: " + iox.getMessage());
		} catch (VLException vlx)
		{
			logger.log("@startTranReport: " + vlx.getMessage());
		}

		try
		{
			ViewFile viewFile = new ViewFile(outFilename, logger);
			viewFile.setVisible(true);
		} catch (UtilitiesException ux)
		{
			logger.log("Internal Error: startTranReport@512 (" + ux.getMessage() + ")");
		}
	}

	// public void startPrintChart()
	// {
	// // This method will print the Chart in .txt format
	// logger.logDebug("enter startPrintChart");
	// try
	// {
	// PrintChartTxt printChartTxt = new PrintChartTxt();
	// printChartTxt.initialize(workDir + "Chart.txt", chart, logger);
	// } catch (IOException iox)
	// {
	// logger.logFatal("Unable to create Chart outFile");
	// }
	// }
	//
	public static void main(String[] args) throws IOException
	{
		VL2 vl2 = new VL2();
		vl2.VL2Init(args);
		vl2.VL2Start(args);
	}
}
