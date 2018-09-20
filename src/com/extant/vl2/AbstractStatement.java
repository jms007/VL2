package com.extant.vl2;

/**
 * AbstractStatement
 * @author jms
 * This is the root method for processing ChartElements to produce:
 *     Financial Statements
 *     Chart of Accounts
 */

//import java.util.Vector;
import com.extant.utilities.Julian;
import com.extant.utilities.LogFile;
import com.extant.utilities.UsefulFile;
//import java.io.IOException;

public abstract class AbstractStatement
{
	Chart chart = VL2.chart;
	VL2Config vl2Config;
	LogFile logger;
	String workDir;
	String dollarFormat;
	String longDateFormat;
	String shortDateFormat;
	Julian earliestDate, latestDate;
	int reportLevel;
	int maxLevel;
	String glFilename;
	int nElements = chart.chartElements.size();
	String maxAccountNo = chart.getMaxAcctNo();
	int maxAccountNoLength = chart.maxAccountNo.length();
	String maxTitle = chart.getMaxTitle();
	int maxTitleLength = maxTitle.length();
	String outfileName;
	UsefulFile outfile;
	String printOrientation;
	int paperWidth;
	int indentPerLevel;
	int maxIndentLength;
	boolean showAccount;
	boolean showAmount;

	public void setup()
	{
		// Set the variables we know
		chart = VL2.chart;
		vl2Config = VL2.vl2Config;
		logger = VL2.logger;
		workDir = vl2Config.getWorkingDirectory();
		dollarFormat = chart.getDollarFormat();
		maxLevel = chart.maxLevel;
		longDateFormat = chart.getLongDateFormat();
		shortDateFormat = chart.getShortDateFormat();
		earliestDate = VL2.earliestDate;
		latestDate = VL2.latestDate;
		maxLevel = chart.getMaxLevel();
		glFilename = vl2Config.getGLFile();
		nElements = chart.chartElements.size();
		maxAccountNo = chart.getMaxAcctNo();
		maxAccountNoLength = chart.maxAccountNo.length();
		maxTitle = chart.getMaxTitle();
		maxTitleLength = chart.maxTitle.length();
		showAccount = true;
		showAmount = true;

		// and set default values for the rest
		reportLevel = 0;
		outfileName = workDir + "stmt.txt";
		vl2Config.setPrintOrientation("portrait");
		paperWidth = vl2Config.getPaperWidth();
		indentPerLevel = chart.getIndent();
		maxIndentLength = maxLevel * indentPerLevel;

		// For debugging:
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		logger.logDebug("StatementTXT.setup...");

		dollarFormat = chart.getDollarFormat();
		longDateFormat = chart.getLongDateFormat();
		shortDateFormat = chart.getShortDateFormat();
		// end = new Julian(vl2Config.getLatestDate());
		// begin = new Julian(vl2Config.getEarliestDate());
	}

	public String makeReport()
	{
		// For Debugging
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		logger.logDebug("AbstractStatement:makeReport()");
		for (int i = 0; i < chart.chartElements.size(); ++i)
		{
			ChartElement element = (ChartElement) chart.chartElements.get(i);
			processElement(element);
		}
		outfile.close();
		return outfileName;
	}

	void processElement(ChartElement element)
	{
		try
		{
			String elementName = element.name;
			if (elementName.equals("chart") || elementName.equals("section") || elementName.equals("group"))
				printTextLine(element);
			else if (elementName.equals("account") && element.getLevel() <= reportLevel)
			{
				if (element.getAttribute("type").equals("R"))
					printRetainedEarnings(element);
				else
					printAmountLine(element);
			} else if (elementName.equals("total") && element.getLevel() <= reportLevel)
				printAmountLine(element);
		} catch (VLException vlx)
		{
			logger.logFatal(vlx.getMessage());
		}
	}

	abstract void initialize();

	abstract void printTextLine(ChartElement element) throws VLException;

	abstract void printAmountLine(ChartElement element) throws VLException;

	abstract void printRetainedEarnings(ChartElement element);

	// abstract void close() throws VLException;
}