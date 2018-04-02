/*
 * GLCheck.java
 *
 * Created on November 7, 2002, 11:41 PM
 */

package com.extant.vl2;

import com.extant.utilities.Julian;
import com.extant.utilities.Strings;
import com.extant.utilities.UsefulFile;
import com.extant.utilities.LogFile;
import java.lang.Integer;
import java.io.IOException;

/**
 *
 * @author jms
 */

public class GLCheck
{
	public int glCheck(String glFilename, Chart chart, String fileType // "fixed" or "token"
			, VL2Config props, LogFile loggerParm)
	{
		if (loggerParm == null)
		{
			System.out.println("entering glCheck: loggerparm is null; creating new LogFile");
			logger = new LogFile();
		} else
			logger = loggerParm;
		this.chart = chart;
		this.props = props;

		// For debugging:
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		// logger.setLogAll(true);

		currentYear = Integer.parseInt(props.getCurrentYear());
		report = "";
		nErrors = 0;
		lineNo = 0;
		// if (chart == null)
		// {
		// chart = new Chart();
		// try {chart.init(chartFilename); }
		// catch (Exception x)
		// { logger.logFatal(x.getMessage()); }
		// }
		if (fileType.equals("fixed"))
			checkFixedFile(glFilename);
		else if (fileType.equals("token"))
			checkTokenFile(glFilename);
		else
			logger.logFatal("GLCheck: Invalid File Type = " + fileType);
		return nErrors;
	}

	public void checkTokenFile(String glFilename)
	{
		UsefulFile f;
		String image = "";

		logger.logDebug("Checking Tokenized File " + glFilename);
		try
		{
			GLEntry glEntry;
			f = new UsefulFile(glFilename, "r");
			if (f.length() == 0)
			{
				emptyGL = true;
				report += "GL0010 is empty";
				finish();
			} else
				emptyGL = false;
			while (!f.EOF())
			{
				image = f.readLine();
				++lineNo;
				if (image.length() < 3)
					continue; // Ignore short lines
				glEntry = new GLEntry(image);
				int transactionYear = glEntry.getJulianDate().getYear() % 100;
				if (transactionYear != currentYear)
				{
					logger.log("line " + lineNo + " transactionDate is not in currentYear:");
					logger.logFatal("currentYear=" + currentYear + " transactionDate="
							+ glEntry.getJulianDate().toString("yymmdd"));
				}
				if (!chart.isValidAccount(glEntry.getAccountNo()))
					reportError("Line " + lineNo + " Account not in chart:" + glEntry.getAccountNo());
				if (earliestDate == null)
					earliestDate = glEntry.getFixedJulianDate();
				else if (glEntry.getFixedJulianDate().isEarlierThan(earliestDate))
					earliestDate = glEntry.getFixedJulianDate();
				if (latestDate == null)
					latestDate = glEntry.getFixedJulianDate();
				else if (glEntry.getFixedJulianDate().isLaterThan(latestDate))
					latestDate = glEntry.getFixedJulianDate();
				calcJournalBal(glEntry);
				bal += glEntry.getSignedAmount();
			}
			f.close();
			reportInfo("EarliestDate=" + earliestDate.toString("yymmdd"));
			props.setEarliestDate(earliestDate.toString("yymmdd"));
			reportInfo("LatestDate=" + latestDate.toString("yymmdd"));
			props.setLatestDate(latestDate.toString("yymmdd"));

			if (bal != 0)
				reportError("File " + glFilename + " is out of balance by " + bal);
			finish();
		} catch (VLException x)
		{
			reportError("VLException in Line " + lineNo + ": " + x.getMessage());
			reportError(image);
		} catch (IOException x)
		{
			reportError("IO Error in Line " + lineNo + ": " + x.getMessage());
		}
	}

	public void checkFixedFile(String glFile)
	{
		try
		{
			String image;
			logger.logDebug("Checking Fixed File " + glFile + ": ");
			UsefulFile f = new UsefulFile(glFile, "r");
			while (!f.EOF())
			{
				image = f.readLine();
				++lineNo;
				if (image.length() != 78)
				{
					reportError("Line " + lineNo + ": wrong length (" + image.length() + ")");
					continue;
				}
				glEntry = new GLEntry(image);
				glEntry.validate();
				if (chart != null)
					if (!chart.isValidAccount(glEntry.getNormalizedAccountNo()))
						reportError("Line " + lineNo + " Account not in chart:" + glEntry.getNormalizedAccountNo());
				if (earliestDate == null)
					earliestDate = glEntry.getFixedJulianDate();
				else if (glEntry.getFixedJulianDate().isEarlierThan(earliestDate))
					earliestDate = glEntry.getFixedJulianDate();
				if (latestDate == null)
					latestDate = glEntry.getFixedJulianDate();
				else if (glEntry.getFixedJulianDate().isLaterThan(latestDate))
					latestDate = glEntry.getFixedJulianDate();
				calcJournalBal(glEntry);
				bal += glEntry.getSignedAmount();
			}
			f.close();
			emptyGL = lineNo < 2;
			finish();
		} catch (Exception x)
		{
			logger.logFatal(x.getMessage());
		}
	}

	public int getNEntries()
	{
		return lineNo;
	}

	public void finish()
	{
		if (emptyGL)
		{
			reportError("No transactions found");
		}
		if (earliestDate != null && latestDate != null)
		{
			report += lineNo + " entries, " + nJournals + " Journals.\n" + "Covering dates "
					+ earliestDate.toString("mm-dd-yyyy") + " to " + latestDate.toString("mm-dd-yyyy") + "\n";
			// props.setProperty("EarliestDate", earliestDate.toString("yymmdd"));
			// props.setProperty("LatestDate", latestDate.toString("yymmdd"));
		} else
		{
			reportError("dates are not set");
		}
		// report += lineNo + " records, " +
		// nJournals + " Journals.\n";
		reportJournalBal();
		if (bal != 0L)
			reportError("General Ledger is out of balance by " + Strings.formatPennies(bal, ","));
		report += Strings.plurals("Error", nErrors) + " found.\n";
		logger.logDebug("GLCheck normal completion");
		System.out.print("GLCheck report:\n" + report);
	}

	private void calcJournalBal(GLEntry glEntry)
	{
		for (int i = 0; i < nJournals; ++i)
			if (journals[i].equals(glEntry.getField("jRef")))
			{
				journalBals[i] += glEntry.getSignedAmount();
				return;
			}
		journals[nJournals] = glEntry.getField("jRef");
		journalBals[nJournals] = glEntry.getSignedAmount();
		++nJournals;
	}

	private void reportJournalBal()
	{
		for (int i = 0; i < nJournals; ++i)
			if (journalBals[i] != 0L)
				reportError("Journal " + journals[i] + " is out of balance by "
						+ Strings.formatPennies(journalBals[i], ","));
	}

	private void reportInfo(String msg)
	{
		report += msg + "\n";
	}

	private void reportError(String msg)
	{
		report += msg + "\n";
		++nErrors;
	}

	public String getReport()
	{
		return report;
	}

	public int getNLines()
	{
		return lineNo;
	}

	public int getNJournals()
	{
		return nJournals;
	}

	public int getNErrors()
	{
		return nErrors;
	}

	public Julian getEarliestDate()
	{
		return earliestDate;
	}

	public Julian getLatestDate()
	{
		return latestDate;
	}

	// Global variables
	VL2Config props;
	int currentYear;
	GLEntry glEntry;
	Chart chart;
	int lineNo;
	int nErrors;
	boolean emptyGL = false;
	Julian earliestDate = null;
	Julian latestDate = null;
	long bal = 0L;
	String report = "";
	String journals[] = new String[100];
	long journalBals[] = new long[100];
	int nJournals = 0;
	LogFile logger;

	/*****
	 * FOR TESTING ***** public static void main(String[] args) { GLCheck
	 * glCheckinstance = new GLCheck(); glCheckinstance.glCheck (
	 * "G:\\ACCOUNTING\\JMS\\ARCHIVES\\GL06\\GL0010.DAT" ,
	 * "G:\\ACCOUNTING\\JMS\\ARCHIVES\\GL06\\CHART.XML" , "token" , new LogFile() );
	 * 
	 * Console.print( glCheckinstance.getReport() ); } /***** END OF TESTING
	 *****/
}
