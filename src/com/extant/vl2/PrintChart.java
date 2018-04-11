/*
 * PrintChart.java
 * Produces a listing of the Chart of Accounts in .pdf format.
 *
 * Created on May 10, 2003, 2:48 PM
 */

package com.extant.vl2;

import com.extant.utilities.*;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
//import java.util.StringTokenizer;
//import javax.swing.JFrame;
import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;

/**
 *
 * @author jms
 */
public class PrintChart
{
	// Local variables
	VL2Config vl2Config;
	String workDir;
	String glFilename;
	Chart chart;
	String outfileName;
	LogFile logger;
	long fakeAmount = 100;
	StatementPDF statement;
	boolean showacctSave;
	StmtTable stmtTable;

	public void printPDF(VL2Config vl2Config, Chart chart, String outfileName, LogFile logger) throws VLException
	{
		this.logger = logger;
		if (logger == null)
			throw new VLException(VLException.NULL_LOGGER, "printPDF");
		// For debugging:
		// logger.setLogAll(true);

		this.vl2Config = vl2Config;
		this.chart = chart;
		workDir = vl2Config.getWorkingDirectory();
		glFilename = vl2Config.getGLFile();
		if (outfileName.contains(File.separator))
			this.outfileName = outfileName;
		else
			this.outfileName = workDir + outfileName;
		showacctSave = chart.setShowacct(false);
		int reportLevel = chart.getMaxLevel();
		Julian begin = new Julian("1/1/20" + new Julian().toString("yy"));
		Julian end = new Julian();
		statement = new StatementPDF(vl2Config, chart, outfileName, logger);
		stmtTable = statement.initialize(reportLevel, outfileName);
		statement.levelTotals = new long[10];
		for (int i = 0; i < statement.levelTotals.length; ++i)
			statement.levelTotals[i] = 0L;
	}

	public void doit() throws IOException, VLException, BadElementException, DocumentException
	{
		Enumeration elements = chart.chartElements();
		while (elements.hasMoreElements())
		{
			ChartElement element = (ChartElement) elements.nextElement();
			logger.logDebug("[PrintChart.doit] element: " + element.toString());
			// Do not print the out-of-balance warnings
			if (element.getAttribute("title").startsWith("*****"))
				continue;
			if (element.name.equals("chart"))
				continue; // printChartElement(element);
			else if (element.name.equals("section"))
				continue; // printSectionElement(element);
			if (element.name.equals("account") || element.name.equals("total"))
			{
				int level = element.getLevel();
				if (element.name.equals("total"))
					--level;
				Account account = new Account(element.getAttribute("no"), Strings.format(level, "00"),
						element.getAttribute("type"), element.getAttribute("title"));

				account.zeroBalances();
				String acctType = account.getType();
				if (Strings.validateChar(acctType.charAt(0), "AE"))
					account.addToBeginBal(fakeAmount);
				else
					account.addToBeginBal(-fakeAmount);
				statement.printAmountLine(element, account);
			} else
				statement.printTextLine(element);
		}
		stmtTable.finish();
		chart.setShowacct(showacctSave);
	}
}
