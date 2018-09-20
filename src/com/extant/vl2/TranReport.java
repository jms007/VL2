/*
 * TranReport.java
 *
 * Created on December 3, 2002, 5:24 PM
 */

package com.extant.vl2;

import com.extant.utilities.*;
//import com.extant.utilities.Sorts;
//import com.extant.utilities.XProperties;
//import com.extant.utilities.Strings;
//import com.extant.utilities.UsefulFile;
//import com.extant.utilities.Julian;
//import com.extant.utilities.LogFile;
//import com.extant.utilities.VLException;
import javax.swing.*;
import java.io.*;
//import java.util.Enumeration;

/**
 * Methods to Produce GL Summary and GL Detail Transaction Reports
 * 
 * @author jms
 */
public class TranReport extends JDialog
{
	public TranReport(int reportType, Chart chart, VL2Config vl2FileMan, LogFile logger) throws IOException, VLException
	{
		this.chart = chart;
		this.vl2FileMan = vl2FileMan;
		this.logger = logger;
		this.reportType = reportType;
		setup();
		makeReport();
	}

	private void setup() throws IOException, VLException
	{
		// String image;
		// GLEntry glEntry;
		// For Debugging:
		logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		logger.logDebug("entering TranReport.setup");

		workDir = vl2FileMan.getWorkingDirectory();
		if (reportType == DETAIL)
			outFilename = workDir + "DetailTranReport.txt";
		else if (reportType == SUMMARY)
			outFilename = workDir + "SummaryTranReport.txt";
		if (new File(outFilename).exists())
			new File(outFilename).delete();
		glFileName = vl2FileMan.getGLFile();

		titleWidth = chart.getMaxTitleLength();
		dateFormat = "mm-dd-yyyy";
		// Default is to include all transactions
		begin = new Julian(vl2FileMan.getEarliestDate());
		end = new Julian(vl2FileMan.getLatestDate());
		sBegin = begin.toString("mm-dd-yyyy");
		sEnd = end.toString("mm-dd-yyyy");
		logger.logDebug("TranReport setup: " + sBegin + " " + sEnd);
	}

	public void makeReport() throws IOException, VLException
	{
		// For debugging:
		// logger.setLogLevel((LogFile.DEBUG_LOG_LEVEL));
		logger.logDebug("entering makeReport");

		long beginTotal = 0L; // SUMMARY - total of all accounts at beginDate
		long transTotal = 0L; // SUMMARY - net transactions in period for all accounts
		long endTotal = 0L; // both - total of all accounts at endDate
		logger.logDebug("entering makeReport: beginDate=" + sBegin + "   outFilename=" + outFilename);
		if (begin.isLaterThan(end))
			throw new VLException(VLException.INCONSISTENT_DATES, "Begin Date (" + begin.toString(dateFormat)
					+ ") is later than End Date (" + end.toString(dateFormat) + ")");
		outfile = new UsefulFile(outFilename, "w");
		printTitle(outfile, reportType, begin.toString(dateFormat), end.toString(dateFormat), vl2FileMan);
		// Compute balances and add GL transactions to the accounts
		logger.logDebug("calling VLUtil.postToAccounts begin=" + begin + "   end=" + end);

		VLUtil.postToAccounts(vl2FileMan.getGLFile(), chart);

		// Console.println( "[TranReport.makeReport] plTrans:\n" +
		// chart.getPLAccount().glEntries.elementAt( 0 ).toString() );
		// Console.println( " plAccount begin=" + chart.getPLAccount().getBeginBal() +
		// " delta=" + chart.getPLAccount().getDeltaBal() + " end=" +
		// chart.getPLAccount().getEndBal() );
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		for (int i = 0; i < chart.accounts.size(); ++i)
		{
			Account account = (Account) chart.accounts.get(chart.acctsByNumberP[i]);
			logger.logDebug("Account: " + account.accountNo);
			ChartElement element = chart.chartElements.get(account.elementIndex);
			logger.logDebug(
					"Account " + account.accountNo + " begin=" + element.beginBal + " delta=" + element.deltaBal);
			if (account.glEntries.isEmpty() && element.beginBal == 0L)
				continue; // no beginning balance or transactions in this account
			transTotal += element.deltaBal;
			endTotal += element.beginBal + element.deltaBal;
			report(acctHeaderLine(reportType, account));
			report(acctBeginLine(reportType, account, element.beginBal));
			// Sort the GLEntries by date here (for detail report)
			String dateList[] = new String[account.glEntries.size()];
			for (int j = 0; j < dateList.length; ++j)
				dateList[j] = ((GLEntry) account.glEntries.elementAt(j)).getJulianDate().toString("yymmdd");
			int sortedEntries[] = Sorts.sort(dateList);
			for (int j = 0; j < sortedEntries.length; ++j)
			{
				report(detailLine(reportType, (GLEntry) account.glEntries.elementAt(sortedEntries[j]),
						element.deltaBal));
			}
			report(acctTotalLine(reportType, account, element));
		}
		report(reportTotalsLine(reportType, beginTotal, transTotal, endTotal));
		outfile.close();
		logger.logDebug("Transaction report is in " + outFilename);
	}

	private void printTitle(UsefulFile outfile, int reportType, String sDate, String eDate, VL2Config vl2FileMan)
	{
		int reportWidth;
		if (reportType == DETAIL)
			reportWidth = 81;
		else
			reportWidth = 21 + 1 + chart.getMaxDescrLength() + 1 + 3 * 15;
		outfile.println(Strings.center(vl2FileMan.getEntityLongName(), reportWidth));
		if (reportType == DETAIL)
			outfile.println(Strings.center("G/L Transaction Detail Report", reportWidth));
		else
			outfile.println(Strings.center("G/L Transaction Summary Report", reportWidth));
		outfile.println(Strings.center(sDate + " through " + eDate, reportWidth));
		outfile.println(Strings.rightJustify("Prepared " + new Julian().toString(dateFormat), reportWidth));
		outfile.println("");
		if (reportType == SUMMARY)
			outfile.println("                                                            "
					+ "Beginning  Total Trans         Ending");
	}

	private String acctHeaderLine(int reportType, Account account)
	{
		String image;
		if (reportType == DETAIL)
			image = account.getTitle() + "   (" + account.getAccountNo() + ")";
		else
			image = "";
		return image;
	}

	private String acctBeginLine(int reportType, Account account, long beginBal)
	{
		String image;
		if (reportType == DETAIL)
			image = "      " + sBegin + "      Beginning Balance                              "
					+ Strings.rightJustify(Strings.formatPennies(beginBal, dollarFormat), 15);
		else
			image = "";
		return image;
	}

	private String detailLine(int reportType, GLEntry glEntry, long accountTransTotal)
	{
		String image;
		long amount;
		if (reportType == DETAIL && !glEntry.getField("JREF").equals("BALF"))
		{
			amount = glEntry.getSignedAmount();
			image = "      " + glEntry.getJulianDate().toString(dateFormat) + " " + glEntry.getField("JRef") + " "
					+ Strings.leftJustify(glEntry.getField("DESCR"), 30)
					+ Strings.rightJustify(Strings.formatPennies(amount, dollarFormat), 15);
			accountTransTotal += amount;
		} else
			image = "";
		return image;
	}

	private String acctTotalLine(int reportType, Account account, ChartElement element)
	{
		String image;
		long acctTotal = element.beginBal + element.deltaBal;
		if (reportType == DETAIL)
			image = "      " + sEnd + "      Ending Balance                                 "
					+ Strings.rightJustify(Strings.formatPennies(acctTotal, dollarFormat), 15) + "\n";
		else
			image = Strings.leftJustify(account.getAccountNo(), 22) + Strings.leftJustify(account.getTitle(), 30) + " "
					+ Strings.rightJustify(Strings.formatPennies(element.beginBal, dollarFormat), 15)
					+ Strings.rightJustify(Strings.formatPennies(element.deltaBal, dollarFormat), 15)
					+ Strings.rightJustify(Strings.formatPennies(acctTotal, dollarFormat), 15);
		// Console.println( "[TranReport.acctTotalLine] image.length()=" +
		// image.length() );
		return image;
	}

	private String reportTotalsLine(int reportType, long beginTotal, long transTotal, long endTotal)
	{
		// NOTE THE REPORT TOTAL FOR ALL ACCOUNTS WILL INCLUDE THE NET INCOME AMOUNT FOR
		// THE PERIOD
		String image;
		if (reportType == DETAIL)
			image = "Total for all accounts:                                           "
					+ Strings.rightJustify(Strings.formatPennies(endTotal, dollarFormat), 18);
		else
			image = "\n" + "Totals for All Accounts" + "                              "
					+ Strings.rightJustify(Strings.formatPennies(beginTotal, dollarFormat), 15)
					+ Strings.rightJustify(Strings.formatPennies(transTotal, dollarFormat), 15)
					+ Strings.rightJustify(Strings.formatPennies(endTotal, dollarFormat), 15);
		return image;
	}

	private void report(String image)
	{
		if (image.length() == 0)
			return;
		if (image.startsWith("\n"))
			outfile.println("");
		outfile.println(Strings.trim(image, "\n"));
		if (image.endsWith("\n"))
			outfile.println("");
	}

	public void setEffectiveDates(Julian begin, Julian end)
	{
		this.begin = begin;
		this.end = end;
	}

	public void setDollarFormat(String format)
	{
		dollarFormat = format;
	}

	public void setDateFormat(String format)
	{
		dateFormat = format;
	}

	public Julian getBeginDate()
	{
		return begin;
	}

	public Julian getEndDate()
	{
		return end;
	}

	public String getOutfileName()
	{
		return outFilename;
	}

	/*****
	 * For Testing: // Use: TranReport // ini=<VL Initialization File> // dir=<Work
	 * Directory> // o=<output file name> // t=DETAIL | SUMMARY // b=<begin date> //
	 * e=<end date> // [-s] to produce output in CSV format //!! not implemented //
	 * public static void main(String[] args) { try { Clip clip = new Clip( args,
	 * new String[] { "props=E:\\ACCOUNTING\\EXTANT\\EXTANT.properties" ,
	 * "dir=E:\\ACCOUNTING\\EXTANT\\GL17\\" //, "o=GLSUM.TXT", "t=SUMMARY" ,
	 * "o=GLDTL.TXT", "t=DETAIL" //, "b=1-1-17", "e=12-31-17" } ); LogFile logger =
	 * new LogFile(); logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL); XProperties props
	 * = new XProperties( clip.getParam( "props" )); String workDir = clip.getParam(
	 * "dir" ); props.setProperty( "WorkDirectory", workDir ); String chartFileName
	 * = props.getString( "ChartFile" ); Chart chart = new Chart(); chart.init(
	 * chartFileName ); logger.logDebug( "new GLCheck" + "(" +
	 * props.getString("GL0010") + " " + props.getString("CHART") + " " + "token" +
	 * " " + "props" + " " + "logger" + ")" );
	 * 
	 * GLCheck glCheck = new GLCheck(); glCheck ( props.getString("GL0010") ,
	 * props.getString("CHART") , "token" // "fixed" or "token" , props , logger );
	 * TranReport tranReport = new TranReport(); int reportType=0; if (
	 * clip.getParam( "t" ).equalsIgnoreCase( "DETAIL" ) ) reportType =
	 * TranReport.DETAIL; else if ( clip.getParam( "t" ).equalsIgnoreCase( "SUMMARY"
	 * ) ) reportType = TranReport.SUMMARY; else { Console.println( "Invalid Report
	 * Type: " + clip.getParam( "t" ) ); System.exit( 1 ); } String outFilename =
	 * workDir + "TranReport.txt"; tranReport.makeReport ( reportType , outFilename
	 * , chart , new Julian( clip.getParam( "b" ) ) , new Julian( clip.getParam( "e"
	 * ) ) , props , null ); Console.println( "G/L Transaction Report is in " +
	 * outFilename); } catch (IOException iox) { Console.println( iox.getMessage()
	 * ); } catch (VLException vlx) { Console.println( vlx.getMessage() ); } catch
	 * (UtilitiesException ux) { Console.println( ux.getMessage() ); }
	 * System.exit(1); } /
	 *****/

	public static final int DETAIL = 1;
	public static final int SUMMARY = 2;
	public VL2Config vl2FileMan;
	LogFile logger;
	String workDir;
	String glFileName;
	public String outFilename;
	UsefulFile outfile;
	Chart chart;
	Julian begin;
	String sBegin;
	Julian end;
	String sEnd;
	int reportType;
	String dateFormat = "mm-dd-yyyy"; // overwritten by the value of "ReportDateFormat" in .ini file
	String dollarFormat = "<,";
	int titleWidth;
}
