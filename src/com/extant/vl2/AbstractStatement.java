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
	static int maxLevel; // used by PrintChartTxt
	String glFilename;
	int nElements = chart.chartElements.size();
	ChartElement plElement;
	String maxAccountNo = chart.getMaxAcctNo();
	int maxAccountNoLength = chart.maxAccountNo.length();
	String maxTitle = chart.getMaxTitle();
	int maxTitleLength = maxTitle.length();
	String outfileName;
	UsefulFile outfile;
	String printOrientation;
	int paperWidth;
	static int indentPerLevel; // used by PrintChartTxt
	int maxIndentLength;
	boolean showAccount;
	boolean showAmount;
	String image;

	public void setup()
	{
		// Set the variables we know
		logger = VL2.logger;
		vl2Config = VL2.vl2Config;
		earliestDate = VL2.earliestDate;
		latestDate = VL2.latestDate;
		workDir = vl2Config.getWorkingDirectory();
		glFilename = vl2Config.getGLFile();
		chart = VL2.chart;
		dollarFormat = chart.getDollarFormat();
		maxLevel = chart.maxLevel;
		longDateFormat = chart.getLongDateFormat();
		shortDateFormat = chart.getShortDateFormat();
		maxLevel = chart.getMaxLevel();
		nElements = chart.chartElements.size();
		plElement = chart.getPLElement();
		indentPerLevel = chart.getIndent();
		maxIndentLength = maxLevel * indentPerLevel;
		maxAccountNo = chart.getMaxAcctNo();
		maxAccountNoLength = chart.maxAccountNo.length();
		maxTitle = chart.getMaxTitle();
		maxTitleLength = chart.maxTitle.length();

		// and set default values for the rest
		reportLevel = 0;
		outfileName = workDir + "stmt.txt";
		vl2Config.setPrintOrientation("portrait");
		paperWidth = vl2Config.getPaperWidth();
		showAccount = true;
		showAmount = true;

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
		logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		logger.logDebug("AbstractStatement:makeReport()");
		logger.logDebug("reportLevel=" + reportLevel);
		for (int i = 0; i < chart.chartElements.size(); ++i)
		{
			image = "";
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
			logger.logDebug("processing element: " + element.toString());
			String elementName = element.name.toLowerCase();
			if (elementName.equals("chart") || elementName.equals("section") || elementName.equals("group"))
				printTextLine(element);
			else if (elementName.equals("account"))
			{
				if (element.getAttribute("type").toUpperCase().equals("R"))
				{
					// This is the profit/loss element (plElement) which triggers printing
					// of RetainedEarnings (or NetWorth)
					plElement = element;
					printRetainedEarnings(element);
					return;
				} else if (element.getLevel() <= reportLevel)
					printAmountLine(element);
			} else if (elementName.equals("total"))
			{
				printAmountLine(element);
			}
		} catch (VLException vlx)
		{
			logger.logFatal(vlx.getMessage());
		}
	}

	abstract void initialize();

	abstract void printTextLine(ChartElement element) throws VLException;

	abstract void printAmountLine(ChartElement element) throws VLException;

	abstract void printRetainedEarnings(ChartElement element);
}