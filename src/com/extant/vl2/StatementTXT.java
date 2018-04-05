/*
 * StatementTXT.java
 * Provides the methods to produce financial statements and chart listings in .txt format.
 *
 * Created on February 21, 2005, 11:04 AM
 *
 */

package com.extant.vl2;

//import com.extant.VL.AbstractStatement;
import com.extant.utilities.*;
//import com.extant.utilities.XProperties;
//import com.extant.utilities.Strings;
//import com.extant.utilities.UsefulFile;
//import com.extant.utilities.Julian;
//import com.extant.utilities.LogFile;
import java.io.*;

/**
 *
 * @author jms
 */
public class StatementTXT extends AbstractStatement
{
	public StatementTXT(VL2Config vl2Config, Chart chart, String glFilename, Julian earliestDate, Julian latestDate,
			int reportLevel, String outfileName, LogFile logger) throws VLException
	{
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		setup(vl2Config, chart, true, earliestDate, latestDate, reportLevel, outfileName, logger);
	}

	public StmtTable initialize(int reportLevel, String outfileName) throws VLException
	{
		try
		{
			logger.logDebug("[StatementTXT.initialize]");
			outfile = new UsefulFile(outfileName, "w");
			calculateColumns(chart, reportLevel);
			return null;
		} catch (IOException iox)
		{
			throw new VLException(VLException.IOX, iox.getMessage());
		}
	}

	void printTextLine(ChartElement element)
	{ // Prints lines containing text only
		String image = "";
		String format = element.getAttribute("format");
		if (format.contains("newpage"))
			image += "\f";
		if (format.contains("center"))
			image += Strings.center(element.getAttribute("title"), maxLineLength);
		else
		{
			if (chart.getShowacct())
				image += Strings.leftJustify(" ", chart.maxAccountNo.length() + 1);
			image += chart.getIndention(element.getLevel());
			image += element.getAttribute("title");
		}

		if (format.contains("ending"))
			image += "\r\n" + Strings.center(end.toString(dateFormat), maxLineLength);
		if (format.contains("period"))
			image += "\r\n"
					+ Strings.center(begin.toString(dateFormat) + " - " + end.toString(dateFormat), maxLineLength);
		if (format.contains("skip"))
			image += "\r\n";
		outfile.println(image);
	}

	@Override // implements method from vl2.AbstractStatement
	void printAmountLine(ChartElement element, Account account) throws VLException
	{
		outfile.println(formatAmountLine(element, account));
	}

	String formatAmountLine(ChartElement element, Account account) throws VLException
	{ // Formats lines with amounts
		String image = "";
		int formatLevel = account.getLevel();
		if (account.getType().equalsIgnoreCase("T"))
			--formatLevel;
		if (chart.getShowacct())
		{
			if (account.getType().equalsIgnoreCase("A"))
				image += Strings.leftJustify(account.getAccountNo(), chart.maxAccountNo.length()) + " ";
			else
				image += Strings.leftJustify(" ", chart.maxAccountNo.length()) + " ";
		}
		image += chart.getIndention(formatLevel);
		image += Strings.leftJustify(account.getTitle(), chart.maxAccountTitle.length()) + " ";
		long bal = account.getEndBal();
		if (account.getType().equalsIgnoreCase("L") || account.getType().equalsIgnoreCase("I")
				|| account.getType().equalsIgnoreCase("R"))
			bal = -bal;
		image += Strings.rightJustify(Strings.formatPennies(bal, dollarFormat),
				(reportLevel - formatLevel) * dollarIndent + maxDollarFieldLength);
		if (element != null)
			if (element.getAttribute("format").contains("skip"))
				image += "\r\n";
		return Strings.trimRight(image, " ");
	}

	public void close() throws VLException
	{
		outfile.close();
	}

	public int calculateColumns(Chart chart, int reportLevel) throws VLException
	{ // maxLineLength is used to center text
		// maxLineAcct is an account at the highest level with the maximum description
		// length and maximum dollar amount
		// Account maxLineAcct = new Account("xxx", "yyy", "0", "Description");
		maxLineLength = 0;
		if (chart.getShowacct())
			maxLineLength = chart.getMaxAcctNoLength() + 1;
		maxLineLength += chart.getMaxAccountTitleLength() + 1;
		maxLineLength += chart.getIndention(reportLevel).length() + 1;
		maxLineLength += Strings.formatPennies(MAX_DOLLAR_AMOUNT).length();
		logger.logDebug("maxLineLength=" + maxLineLength);
		return maxLineLength;
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	/*****
	 * FOR TESTING ***** public static void main(String[] args) { try { LogFile
	 * logger = new LogFile(); Clip clip = new Clip( args, new String[] {
	 * "dir=E:\\ACCOUNTING\\QUITO\\GL17\\" , "c=CHART.XML" , "gl=GL0010.DAT" ,
	 * "o=StmtOOP.txt" } ); String workDir = clip.getParam( "dir" ); if (
	 * !workDir.endsWith( File.separator ) ) workDir += File.separator; String
	 * chartFilename = clip.getParam( "c" ); if ( chartFilename.indexOf(
	 * File.separator ) < 0 ) chartFilename = workDir + chartFilename; Chart chart =
	 * new Chart(); chart.init( chartFilename ); String glFilename = clip.getParam(
	 * "gl" ); if ( glFilename.indexOf( File.separator ) < 0 ) glFilename = workDir
	 * + glFilename; GLCheck glCheck = new GLCheck(); glCheck.checkTokenFile(
	 * glFilename ); Julian begin = glCheck.getEarliestDate(); Julian end =
	 * glCheck.getLatestDate(); String outfileName = clip.getParam( "o" ); if (
	 * outfileName.indexOf( File.separator ) < 0 ) outfileName = workDir +
	 * outfileName; // For Debugging: //logger.setLogLevel( logger.DEBUG_LOG_LEVEL
	 * );
	 * 
	 * StatementTXT statementTXT = new StatementTXT ( workDir , chart , glFilename ,
	 * begin , end , 0 // reportLevel , outfileName , logger );
	 * statementTXT.makeStatement(); System.out.println( "Output is in file " +
	 * outfileName ); } catch (Exception x) { x.printStackTrace(); } // catch
	 * (UtilitiesException ux) { Console.println( ux.getMessage() ); } // catch
	 * (IOException iox) { Console.println( iox.getMessage() ); } // catch
	 * (VLException vlx) { Console.println( vlx.getMessage() ); } } /
	 *****/

	UsefulFile outfile;
	int dollarIndent = 10;
	public final static long MAX_DOLLAR_AMOUNT = 999999999L; // $ 9,999,999.99
	int maxDollarFieldLength;
	int maxLineLength;
}
