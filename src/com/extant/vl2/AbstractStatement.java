package com.extant.vl2;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
//import com.lowagie.text.DocumentException;
//import javax.security.auth.login.AccountNotFoundException;

import com.extant.utilities.Julian;
import com.extant.utilities.LogFile;
import com.extant.utilities.Strings;

/**
 *
 * @author jms
 */
public abstract class AbstractStatement {
	public AbstractStatement()
	{
	}

	public void setup(VL2FileMan vl2FileMan, Chart chart, boolean showAmounts, Julian begin, Julian end,
			int reportLevel, String outfileName // specify "" if output is not needed
			, LogFile logger) throws VLException
	{
		try {
			this.vl2FileMan = vl2FileMan;
			this.chart = chart;
			this.showAmounts = showAmounts;
			this.glFilename = vl2FileMan.getGLFile();
			if (reportLevel <= 0 || reportLevel > chart.getMaxLevel())
				this.reportLevel = chart.getMaxLevel();
			else
				this.reportLevel = reportLevel;
			this.logger = logger;
			logger.log("[AbstractStatement.setup]");
			// if ( this.outfileName.length() > 0 )
			// this.outfileName = workDir + outfileName;
			dollarFormat = chart.getDollarFormat();
			dateFormat = chart.getDateFormat();
			shortDateFormat = chart.getShortDateFormat();
			this.begin = begin;
			this.end = end;
			// !! You cannot feed nulls to extractBalances() for begin & end
			if (reportLevel <= 0)
				reportLevel = chart.getMaxLevel();
			if (showAmounts) {
				logger.logDebug("   calling VLUtil.extractBalances(" + glFilename + ",<chart>,"
						+ begin.toString("yymmdd") + "," + end.toString("yymmdd") + ")");
				VLUtil.extractBalances(glFilename, chart, begin, end, logger);
				logger.logDebug("   back from extractBalances()");
				levelTotals = new long[chart.getMaxLevel() + 1];
				for (int i = 0; i < levelTotals.length; ++i)
					levelTotals[i] = 0L;
			}

			// Overwrite confirmation is obtained in ReportOptions under VL
			// !! BUT ReportOptions does not run if executing stand-alone
			if (outfileName.length() > 0) {
				File f = new File(outfileName);
				if (f.exists())
					f.delete();
			}

			logger.logDebug("   calling initialize( " + reportLevel + ",'" + outfileName + "')");
			initialize(reportLevel, outfileName);
			logger.logDebug("   back from initialize()");
			makeStatement();
		} catch (IOException iox) {
			throw new VLException(VLException.IOX, iox.getMessage());
		}
	}

	public String makeStatement() throws VLException
	{
		Enumeration chartElements = chart.chartElements();
		while (chartElements.hasMoreElements()) {
			ChartElement element = (ChartElement) chartElements.nextElement();
			processChartElement(element);
		}
		// close();
		return outfileName;
	}

	void processChartElement(ChartElement element) throws VLException
	{
		try {
			String elementName = element.name;
			if (elementName.equals("chart") || elementName.equals("section") || elementName.equals("group")) {
				if (element.getLevel() <= reportLevel)
					cPrint(element, null, elementName.equals("group")); // only group titles are conditionally printed
			}

			else if (elementName.equals("account") && showAmounts) { // Print ending balance in this account & add to
																		// level total
				int accountIndex = element.getAccountIndex(); // !!Mystery VLException from this line
				Account account;
				account = chart.getAccount(accountIndex);

				if (element.getAttribute("type").equals("R"))
					printNetWorth(element, account, showAmounts);
				if (account.getEndBal() != 0L && element.getLevel() <= reportLevel)
					cPrint(element, account, false);
			} else if (elementName.equals("total") && showAmounts) {
				element.setTotal(levelTotals[element.getLevel()]);
				Account account = new Account("", element.getLevelString(), element.getAttribute("type"),
						element.getAttribute("title"));
				account.zeroBalances();
				account.addToBeginBal(levelTotals[element.getLevel()]);
				if (account.getEndBal() != 0L && element.getLevel() <= reportLevel) {
					cPrint(element, account, false);
				}
				if (element.getLevel() > 0)
					levelTotals[element.getLevel() - 1] += levelTotals[element.getLevel()];
				levelTotals[element.getLevel()] = 0L;
			}
		} catch (VLException vlx) {
			logger.logFatal("Mystery Exception@AbstractStatement: ProcessChartElement: " + vlx.getMessage());
		}
	}

	private void printNetWorth(ChartElement element, Account thisAccount, boolean showAmounts) throws VLException
	{
		if (!showAmounts)
			return;
		String label = "Retained Earnings at " + begin.toString(dateFormat);
		Account pl = new Account(thisAccount.getAccountNo(), Strings.format(thisAccount.getLevel() + 1, "#"),
				thisAccount.getType(), label);
		pl.addToBeginBal(thisAccount.getBeginBal());
		cPrint(element, pl, false); // net worth at beginning of period

		label = "Retained Earnings Change in Period ";
		// thisAccount.setTitle( label );
		pl.setTitle(label);
		pl.zeroBalances();
		pl.addToDeltaBal(thisAccount.getDeltaBal());
		cPrint(element, pl, false); // p&l change during the period

		label = "Retained Earnings at " + end.toString(dateFormat);
		thisAccount.setTitle(label);
		pl.setTitle(label);
		pl.addToBeginBal(thisAccount.getBeginBal());
		pl.setLevel(pl.getLevel() - 1);
		// cPrint( element, pl, false ); System will print this line
	}

	// cPrint is a conditional print method which suppresses the printing of
	// group titles when there is no detail to be printed in that group
	private Vector<ChartElement> cPrintBuffer = new Vector<ChartElement>(10, 10);

	public void cPrint(ChartElement element, Account account, boolean conditional) throws VLException
	{
		if (conditional) {
			for (int i = 0; i < cPrintBuffer.size(); ++i) {
				int cPrintLevel = ((ChartElement) cPrintBuffer.elementAt(i)).getLevel();
				String cPrintImage = ((ChartElement) cPrintBuffer.elementAt(i)).getAttribute("title");
				if (cPrintLevel >= element.getLevel()) {
					logger.logDebug("Removing '" + cPrintImage + "'");
					cPrintBuffer.removeElementAt(i);
					--i;
				}
			}
			cPrintBuffer.addElement(element);
			logger.logDebug("Adding '" + element.getAttribute("title") + "' to cPrintBuffer");
		} else {
			for (int i = 0; i < cPrintBuffer.size(); ++i) {
				int cPrintLevel = ((ChartElement) cPrintBuffer.elementAt(i)).getLevel();
				String cPrintImage = ((ChartElement) cPrintBuffer.elementAt(i)).getAttribute("title");
				logger.logDebug("level=" + element.getLevel() + " Checking [" + cPrintLevel + "] " + cPrintImage
						+ "(print=" + (element.getLevel() > cPrintLevel) + ")");
				if (element.getLevel() > cPrintLevel) {
					printTextLine((ChartElement) cPrintBuffer.elementAt(i));
				}
				cPrintBuffer.removeElementAt(i);
				--i;
			}
			if (element.name.equals("group") || element.name.equals("chart") || element.name.equals("section"))
				printTextLine(element);
			else if (element.name.equals("account") || element.name.equals("total"))
				printAmountLine(element, account);
		}
	}

	// For non-PDF output, initialize should return null
	abstract StmtTable initialize(int reportLevel, String outfileName) throws VLException;

	abstract void printTextLine(ChartElement element) throws VLException;

	abstract void printAmountLine(ChartElement element, Account account) throws VLException;
	// abstract void close() throws VLException;

	VL2FileMan vl2FileMan;
	Chart chart;
	LogFile logger;
	String workDir;
	String outfileName;
	String dollarFormat;
	String dateFormat;
	String shortDateFormat;
	Julian begin, end;
	int reportLevel;
	String glFilename;
	long levelTotals[];
	boolean showAmounts;
}
