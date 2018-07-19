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
import java.lang.Integer;
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
		this.props = props;
		this.logger = VL2.logger;

		// For debugging:
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		// logger.setLogAll(true);

		currentYear = Integer.parseInt(props.getCurrentYear());
		report = "";
		nErrors = 0;
		lineNo = 0;
		if (fileType.equals("fixed"))
			checkFixedFile(glFilename);
		else if (fileType.equals("token"))
			try
			{
				checkTokenFile(glFilename);
			} catch (VLException vlx)
			{
				System.out.println("Exception in token file: " + vlx.getMessage());
			}
		else
			logger.logFatal("GLCheck: Invalid File Type: " + fileType);
		return nErrors;
	}

	public void checkTokenFile(String glFilename) throws VLException
	{
		UsefulFile f;
		String image = "";
		Julian BALFdate = null;

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
			while (!f.EOF())
			{
				image = f.readLine();
				++lineNo;
				if (image.length() < 3)
					continue; // Ignore (but count) short lines
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
					logger.logFatal("date is not in current year");
				}
				String accountNo = glEntry.getAccountNo();
				Account account = chart.findAcctByNo(accountNo);
				int elementIndex;
				ChartElement2 element;
				if (account != null)
					if (chart.isValidAccount(accountNo))
					{
						account = chart.findAcctByNo(accountNo);
						elementIndex = account.elementIndex;
						element = chart.chartElements.elementAt(elementIndex);
						if (glEntry.getField("JREF").equals("BALF"))
						{
							logger.logDebug("executing glEntry BALF");
							if (BALFdate == null)
							{
								BALFdate = glEntry.getJulianDate();
							} else if (!glEntry.getJulianDate().isEqualTo(BALFdate))
							{
								reportError("Line " + lineNo + "BALF has multiple dates");
								continue;
							}
							logger.logDebug(glEntry.toString());
							logger.logDebug("adding " + glEntry.getSignedAmount() + " to element.beginBal");
							element.beginBal += glEntry.getSignedAmount();
							logger.logDebug("result element.beginBal=" + element.beginBal);
							logger.logDebug("result element=" + element.toString());
						} else
							element.deltaBal += glEntry.getSignedAmount();
						glBal += glEntry.getSignedAmount();

					} else
					{
						reportError("Line " + lineNo + " Account not in chart:" + glEntry.getAccountNo());
						continue;
					}
				if (earliestDate == null)
					earliestDate = glEntry.getJulianDate();
				else if (glEntry.getJulianDate().isEarlierThan(earliestDate))
					earliestDate = glEntry.getJulianDate();
				if (latestDate == null)
					latestDate = glEntry.getJulianDate();
				else if (glEntry.getJulianDate().isLaterThan(latestDate))
					latestDate = glEntry.getJulianDate();
				calcJournalBal(glEntry);
			} // end of transaction processing
			f.close();

			reportInfo("BALFdate=" + BALFdate.toString("yymmdd"));
			reportInfo("EarliestDate=" + earliestDate.toString("yymmdd"));
			props.setEarliestDate(earliestDate.toString("yymmdd"));
			reportInfo("LatestDate=" + latestDate.toString("yymmdd"));
			props.setLatestDate(latestDate.toString("yymmdd"));
			if (earliestDate.isEarlierThan(BALFdate))
				logger.logFatal("One or more transactions pre-date BALF date");
			if (glBal != 0)
				reportError("File " + glFilename + " is out of balance by " + glBal);
			finish(); // throws VLException
		} // end of try Processing Tokenized File
		catch (VLException vlx)
		{
			reportError("VLException Line " + lineNo + ": '" + image + "' " + vlx.getMessage());
		} catch (IOException iox)
		{
			System.out.println("IOException Line " + lineNo + ": " + iox.getMessage());
		}
	}

	public void checkFixedFile(String glFile)
	{
		try
		{
			String image;
			logger.logDebug("Processing Fixed File " + glFile + ": ");
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
					earliestDate = glEntry.getJulianDate();
				else if (glEntry.getJulianDate().isEarlierThan(earliestDate))
					earliestDate = glEntry.getJulianDate();
				if (latestDate == null)
					latestDate = glEntry.getJulianDate();
				else if (glEntry.getJulianDate().isLaterThan(latestDate))
					latestDate = glEntry.getJulianDate();
				calcJournalBal(glEntry);
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

	private void reportJournalBal() // throws VLException
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

	public void finish() throws VLException
	{
		if (earliestDate != null && latestDate != null)
		{
			report += lineNo + " entries, " + nJournals + " Journals.\n" + "Covering dates "
					+ earliestDate.toString("mm-dd-yyyy") + " to " + latestDate.toString("mm-dd-yyyy") + "\n";
			// props.setProperty("EarliestDate", earliestDate.toString("yymmdd"));
			// props.setProperty("LatestDate", latestDate.toString("yymmdd"));
		} else
		{
			reportError("Earliest Date or Latest Date (or both) are not set");
		}
		// report += lineNo + " records, " +
		// nJournals + " Journals.\n";
		reportJournalBal();
		if (glBal != 0L)
			reportError("General Ledger is out of balance by " + Strings.formatPennies(glBal, ","));
		reportInfo(Strings.plurals("Error", nErrors) + " found.\n");
		System.out.print("GLChecker report:\n" + report);
		if (nErrors > 0)
			throw new VLException(VLException.GL_ERRORS);
		logger.log("GLChecker normal completion");
		return;
	}

	// Global variables
	VL2Config props;
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
