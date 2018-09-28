/*
 * GLChecker.java
 *
 * Created on November 7, 2002, 11:41 PM
 * 
 * This class (1) checks the validity of the GLEntry's in the GLFile,
 * (2) locates and balances the journals, and (3) computes the beginning
 * and delta balances in the Chart Account's and the Chart elements.
 * 
 * It does NOT compute totals.
 */

package com.extant.vl2;

import com.extant.utilities.Julian;
import com.extant.utilities.Strings;
import com.extant.utilities.UsefulFile;
import com.extant.utilities.LogFile;
import java.io.IOException;

/**
 *
 * @author jms
 */

public class GLChecker
{
	public int glCheck(Chart chart, String fileType // "fixed" or "token"
			, VL2Config props)
	{
		glFilename = props.getGLFile();
		this.chart = chart;
		this.logger = VL2.logger;

		// For debugging:
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		// logger.setLogAll(true);

		currentYear = Integer.parseInt(props.getCurrentYear());
		infoReport = "";
		nErrors = 0;
		lineNo = 0;
		logger.logDebug("fileType=" + fileType);
		if (fileType.equals("fixed"))
			checkFixedFile(glFilename);
		else if (fileType.equals("token"))
			checkTokenFile(glFilename);
		else
			logger.logFatal("GLCheck: Invalid File Type: " + fileType);
		return nErrors;
	}

	public void checkTokenFile(String glFilename)
	{
		UsefulFile f;
		String image = "";
		Julian BALFdate = null;
		ChartElement plElement;

		logger.logDebug("Processing Tokenized File " + glFilename);
		try
		{
			GLEntry glEntry;
			f = new UsefulFile(glFilename, "r");
			emptyGL = (f.length() == 0);
			if (emptyGL)
			{
				reportError("GL0010 is empty");
				finish();
			}
			plElement = chart.plElement;
			while (!f.EOF())
			{
				image = f.readLine();
				++lineNo;
				if (image.length() < 3)
				{
					logger.log("GLChecker: line " + lineNo + " is blank");
					continue; // Ignore (but count) short lines
					// TODO DELETE SHORT LINES
				}
				glEntry = new GLEntry(image);
				logger.logDebug("Line " + lineNo + ": " + image);
				logger.logDebug("GLEntry: " + glEntry.toString());
				GSNtest = Strings.parseInt(glEntry.getField("GSN"));
				if (GSNtest > VL2.maxGSN)
					VL2.maxGSN = GSNtest;
				int transactionYear = glEntry.getJulianDate().getYear() % 100;
				if (transactionYear != currentYear)
				{
					String errorMsg = "line " + lineNo + " transactionDate is not in currentYear:\\n";
					errorMsg += "currentYear=" + currentYear + " transactionDate="
							+ glEntry.getJulianDate().toString("yymmdd");
					reportError(errorMsg);
				}
				String accountNo = glEntry.getAccountNo();
				Account account = chart.findAcctByNo(accountNo);
				if (account == null)
				{
					reportError("Invalid AccountNo='" + accountNo + "'");
					continue;
				}
				int elementIndex;
				ChartElement element;
				if (account != null)
					if (chart.isValidAccount(accountNo))
					{
						account = chart.findAcctByNo(accountNo);
						String accountType = account.getType().toUpperCase();
						boolean plType = accountType.equals("I") || accountType.equals("E");
						elementIndex = account.elementIndex;
						element = chart.chartElements.elementAt(elementIndex);
						if (glEntry.getField("JREF").equals("BALF"))
						{
							logger.logDebug("processing glEntry BALF");
							if (BALFdate == null)
							{
								BALFdate = glEntry.getJulianDate();
							} else if (!glEntry.getJulianDate().isEqualTo(BALFdate))
							{
								reportError("BALF has multiple dates");
								continue;
							}
							logger.logDebug(glEntry.toString());
							logger.logDebug("adding " + glEntry.getSignedAmount() + " to element.beginBal");
							element.beginBal += glEntry.getSignedAmount();
							logger.logDebug("result element.beginBal=" + element.beginBal);
							logger.logDebug("result element=" + element.toString());
						} else
						{
							element.deltaBal += glEntry.getSignedAmount();
							if (plType)
								plElement.deltaBal += glEntry.getSignedAmount();
						}
						updateJournalBal(glEntry);
						glBal += glEntry.getSignedAmount();
					}
				if (earliestDate == null)
					earliestDate = glEntry.getJulianDate();
				else if (glEntry.getJulianDate().isEarlierThan(earliestDate))
					earliestDate = glEntry.getJulianDate();
				if (latestDate == null)
					latestDate = glEntry.getJulianDate();
				else if (glEntry.getJulianDate().isLaterThan(latestDate))
					latestDate = glEntry.getJulianDate();
			} // end of transaction processing
			f.close();

			reportInfo("BALFdate=" + BALFdate.toString("yymmdd"));
			reportInfo("EarliestDate=" + earliestDate.toString("yymmdd"));
			vl2Config.setEarliestDate(earliestDate.toString("yymmdd"));
			reportInfo("LatestDate=" + latestDate.toString("yymmdd"));
			vl2Config.setLatestDate(latestDate.toString("yymmdd"));
			if (earliestDate.isEarlierThan(BALFdate))
				reportError("One or more transactions pre-date BALF");
			finish();
		} // end of try Processing Tokenized File
		catch (IOException iox)
		{
			reportError("IOException: " + iox.getMessage());
		} catch (VLException vlx)
		{
			reportError("VLExeption: " + vlx.getMessage());
		}
	}

	public void checkFixedFile(String glFile)
	{
		try
		{
			// logger.log("WARNING GLChecker.checkTokenFile() HAS NOT BEEN TESTED!");
			logger.logFatal("GLChecker.checkTokenFile() HAS NOT BEEN TESTED!");

			String image;
			logger.logDebug("Processing Fixed File " + glFile + ": ");
			UsefulFile f = new UsefulFile(glFile, "r");
			while (!f.EOF())
			{
				image = f.readLine();
				++lineNo;
				if (image.length() < 3)
					continue; // ignore short lines
				if (image.length() != 78)
				{
					reportError(": wrong length (" + image.length() + ")");
					continue;
				}
				glEntry = new GLEntry(image);
				glEntry.validate();
				if (chart != null)
					if (!chart.isValidAccount(glEntry.getNormalizedAccountNo()))
						reportError("Account not in chart:" + glEntry.getNormalizedAccountNo());
				if (earliestDate == null)
					earliestDate = glEntry.getJulianDate();
				else if (glEntry.getJulianDate().isEarlierThan(earliestDate))
					earliestDate = glEntry.getJulianDate();
				if (latestDate == null)
					latestDate = glEntry.getJulianDate();
				else if (glEntry.getJulianDate().isLaterThan(latestDate))
					latestDate = glEntry.getJulianDate();
				updateJournalBal(glEntry);
				glBal += glEntry.getSignedAmount();
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

	private void updateJournalBal(GLEntry glEntry)
	{
		for (int i = 0; i < nJournals; ++i)
			if (journals[i].equals(glEntry.getField("jRef")))
			{
				logger.logDebug("adding " + glEntry.getSignedAmount() + " to " + journals[i]);
				journalBals[i] += glEntry.getSignedAmount();
				return;
			}
		logger.logDebug("adding journal " + glEntry.getField("jref"));
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
		infoReport += msg + "\n";
	}

	private void reportError(String msg)
	{
		errorReport += "Line " + lineNo + " " + msg + "\n";
		++nErrors;
	}

	public String getInfoReport()
	{
		return infoReport;
	}

	public String getErrorReport()
	{
		return errorReport;
	}

	// public int getNLines()
	// {
	// return lineNo;
	// }
	//
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

	public void finish()
	{
		// Check that begin and end dates have been set
		logger.logDebug("GLChecker Finish");
		if (earliestDate != null && latestDate != null)
		{
			reportInfo(nJournals + " Journals  " + "Covering dates " + earliestDate.toString("mm-dd-yyyy") + " to "
					+ latestDate.toString("mm-dd-yyyy") + "\n");
		} else
			reportError("Earliest Date and/or Latest Date not set");
		// Calculate & check the journal balances
		reportJournalBal(); // Adds any errors to errorReport
		// Check that GL is balanced
		if (glBal != 0L)
			reportError("General Ledger is out of balance by " + Strings.formatPennies(glBal, ","));
		// Now publish the reports
		logger.log(getInfoReport());
		if (nErrors == 0)
			logger.log("GLChecker normal completion");
		else
			logger.logFatal(
					"GLChecker error report contains " + Strings.plurals("Error", nErrors) + ":\n" + errorReport);
		return;
	}

	VL2Config vl2Config = VL2.vl2Config;

	int currentYear;
	GLChecker glCheck;
	String glFilename;
	GLEntry glEntry;
	Chart chart;
	int lineNo;
	int GSNtest;
	int nErrors;
	boolean emptyGL = false;
	Julian earliestDate = null;
	Julian latestDate = null;
	long glBal = 0L;
	String infoReport = "";
	String errorReport = "";
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
