/*
 * VLUtil.java
 * Utilities for Visual Ledger package
 *
 * Created on November 7, 2002, 4:02 PM
 */

package com.extant.vl2;

import com.extant.utilities.UsefulFile;
import com.extant.utilities.Julian;
import com.extant.utilities.Strings;
import com.extant.utilities.LogFile;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.sql.SQLException;
import javax.swing.JComboBox;
//import com.extant.dbTools.RemoteFileMan;
//import com.extant.dbTools.DBException;

/**
 *
 * @author jms
 */
public class VLUtil
{
	static LogFile logger = VL2.logger;

	// Use VL2Config.getAccountingDataDirectory()
	// public static String getAccountingRoot()
	// {
	// System.out.println("VLUtil.getAccountingRoot( ) is obsolete");
	// System.exit(1);
	//
	// for (int i = 0; i < Strings.ALPHA_UPPER.length(); ++i)
	// {
	// String disk = Strings.ALPHA_UPPER.substring(i, i + 1);
	// if (new File(disk + ":\\ACCOUNTING").exists())
	// return disk + ":\\ACCOUNTING\\";
	// }
	// return null;
	// }
	//
	// public static void postEntries
	// ( Vector glEntries
	// , String glFileName
	// , RemoteFileMan remoteFileMan
	// , Julian postTime
	// )
	// throws IOException, DBException, SQLException
	// {
	// // Build GL File images
	// Vector images = new Vector( glEntries.size() );
	// for (int i=0; i<glEntries.size(); ++i)
	// images.addElement( ((GLEntry)glEntries.elementAt( i )).buildRecord() );
	// // Add records to remote file first, in case it fails (4-30-06)
	// if ( remoteFileMan != null )
	// remoteFileMan.addRecords( glFileName, images, postTime );
	// // THEN Add records to local file
	// UsefulFile glFile = new UsefulFile( glFileName, "w+" );
	// for (int i=0; i<images.size(); ++i)
	// glFile.println( (String)images.elementAt( i ) );
	// glFile.close();
	// }

	/*
	 * fixDate returns a valid Julian date for the intentional invalid dates used in
	 * GL files: for yy0100 fixDate returns 12/31/<yy-1> for yy1232 fixDate returns
	 * 1/1/<yy+1> for valid dates, fixDate just returns the valid Julian
	 *
	 * Actually, we may not need this at all ... Julian seems to do this without
	 * coaching.
	 */
	public static Julian fixDate(String sDate)
	{
		// LogFile logger = VL2.logger;
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		logger.logDebug("fixDate: sDate=" + sDate);
		if (Julian.isValid(sDate))
			return new Julian(sDate);
		Julian thisDate;
		if (sDate.substring(4).equals("00"))
		{ // Change xx0100 to (xx-1)1231
			sDate = sDate.substring(0, 4) + "01";
			thisDate = new Julian(sDate).addDays(-1L);
			// Do the return here, because if you wait, you will get a big surprise
			return thisDate;
		} else if (sDate.substring(4).equals("32"))
			;
		{ // Change xx1232 to (xx+1)0101
			sDate = sDate.substring(0, 4) + "31";
			thisDate = new Julian(sDate).addDays(1L);
			return thisDate;
		}
	}

	/*
	 * The glExtract methods return a vector of GLEntry objects which meet the
	 * specified selection criteria, sorted by account, subAccount, date. For each
	 * (cols,data) pair, the entry is selected if the gl record contains the String
	 * data[i] starting in column cols[i].
	 *
	 * Except for glExtract( String glFileName, int cols[], String data[], Julian
	 * begin, Julian end ) these routines have not been tested and are not currently
	 * (11-29-02) used.
	 */

	// public static Vector glExtract( String glFileName, String fieldName, String
	// equals )
	// throws IOException
	// {
	// return glExtract( glFileName, fieldName, equals, null, null );
	// }
	//
	// public static Vector glExtract( String glFileName, String fieldName, String
	// equals, Julian begin, Julian end )
	// throws IOException
	// {
	// return glExtract( glFileName,
	// new int[] {GLEntry.pStart[GLEntry.getFieldIndex( fieldName )]},
	// new String[] { equals },
	// begin, end );
	// }
	//
	// public static Vector glExtract( String glFileName, int cols[], String data[]
	// )
	// throws IOException
	// {
	// return glExtract( glFileName, cols, data, null, null );
	// }
	//
	// public static Vector glExtract( String glFileName, int cols[], String data[],
	// Julian begin, Julian end )
	// throws IOException, VLException
	// {
	// Vector entries = new Vector( 100, 1000 );
	// String image="";
	// Julian date;
	// GLEntry glEntry;
	// int pDateStart = GLEntry.pStart[GLEntry.getFieldIndex( GLEntry.DATE )];
	// int pDateStop = GLEntry.pEnd [GLEntry.getFieldIndex( GLEntry.DATE )];
	// UsefulFile glFile = new UsefulFile( glFileName, "r" );
	// while ( !glFile.EOF() )
	// {
	// image = glFile.readLine( UsefulFile.ALL_WHITE );
	// if ( image.length() < 3 ) continue;
	// boolean match = true;
	// if ( cols != null )
	// {
	// for (int i=0; i<cols.length; ++i)
	// {
	// if ( image.substring( cols[i] ).startsWith( data[i] ) ) continue;
	// match = false;
	// break;
	// }
	// }
	// if ( !match ) continue;
	// // Now check the date
	// date = fixDate( image.substring( pDateStart, pDateStop + 1 ) );
	// if ( begin != null ) if ( date.isEarlierThan( begin ) ) continue;
	// if ( end != null ) if ( date.isLaterThan( end ) ) continue;
	// entries.addElement( new GLEntry( image ) );
	// }
	// glFile.close();
	//
	// // Now sort by ACCOUNT, SUBACCOUNT, DATE
	// int fieldP[] = new int[6];
	// fieldP[0] = GLEntry.pStart[GLEntry.getFieldIndex( GLEntry.ACCOUNT )];
	// fieldP[1] = GLEntry.pEnd [GLEntry.getFieldIndex( GLEntry.ACCOUNT )];
	// fieldP[2] = GLEntry.pStart[GLEntry.getFieldIndex( GLEntry.SUBACCOUNT )];
	// fieldP[3] = GLEntry.pEnd [GLEntry.getFieldIndex( GLEntry.SUBACCOUNT )];
	// fieldP[4] = GLEntry.pStart[GLEntry.getFieldIndex( GLEntry.DATE )];
	// fieldP[5] = GLEntry.pEnd [GLEntry.getFieldIndex( GLEntry.DATE )];
	// int sortP[] = Sorts.sort( entries, fieldP );
	// Vector sortedEntries = new Vector( entries.size() );
	// for (int i=0; i<entries.size(); ++i)
	// sortedEntries.addElement( entries.elementAt( sortP[i] ) );
	// return sortedEntries;
	// }

	/*
	 * computeAccountBalances processes the GLFile to compute the beginning balance
	 * (as of the begin date) and the delta amount (up to and including the end
	 * date) for each account contained in the chart. These values are stored in
	 * beginBal and deltaVal fields in the Account.
	 * 
	 * All transactions between begin date and end date are attached to the account.
	 *
	 * If a transaction is found in the GL for an account which is not in the chart,
	 * a VLException is thrown.
	 *
	 * This method does not compute total amounts. If you need the total amounts,
	 * follow this call with a call to OopStatement.ComputeTotals which will set the
	 * totals in the appropriate ChartEntry's
	 */
	public static int computeAccountBalances(String glFileName, Chart chart, String begins, String ends, LogFile logger)
			throws IOException, VLException
	{
		// For debugging:
		logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

		logger.logDebug("enter computeAccountBalances: begin=" + "   end=");
		Julian begin = new Julian(begins);
		Julian end = new Julian(ends);
		GLEntry glEntry;
		String currentAcctNo = "";
		Account currentAccount = null;
		chart.clearAccountBalances();
		chart.removeGLEntries(); // !! Are you sure ??
		Account plAccount = chart.getPLAccount();
		if (plAccount == null)
			logger.log("VLUtil:189 plAccount is null!");
		UsefulFile glFile = new UsefulFile(glFileName, "r");
		int maxDescrLength = 0;
		int lineNo = 0;
		while (!glFile.EOF())
		{
			glEntry = new GLEntry(glFile.readLine(UsefulFile.ALL_WHITE));
			++lineNo;
			logger.logDebug("Line " + lineNo + " GLEntry:" + glEntry.toString());
			if (!glEntry.getAccountNo().equals(currentAcctNo))
			{
				currentAcctNo = glEntry.getAccountNo();
				currentAccount = chart.findAcctByNo(currentAcctNo);
				if (currentAccount == null)
					throw new VLException(VLException.ACCT_NOT_IN_CHART,
							"[VLUtil.extractBalances] " + currentAcctNo + " (GL Line " + lineNo + ")");
			}

			if (glEntry.getFixedJulianDate().isEarlierThan(begin))
			{
				// Add these transactions to beginBal
				currentAccount.addToBeginBal(glEntry.getSignedAmount());
				// Add Income & Expense items to P/L Account Begin balance
				if (currentAccount.getType().equals("I") || currentAccount.getType().equals("E"))
					plAccount.addToBeginBal(glEntry.getSignedAmount());
			} else if (!glEntry.getFixedJulianDate().isLaterThan(end))
			{
				// Add this transaction to the account deltaBal
				currentAccount.addGLEntry(glEntry);
				currentAccount.addToDeltaBal(glEntry.getSignedAmount());
				// Add Income & Expense items to P/L Account Delta balance
				if (currentAccount.getType().equals("I") || currentAccount.getType().equals("E"))
				{
					long pl = glEntry.getSignedAmount();
					plAccount.addToDeltaBal(pl);
					logger.logDebug("adding " + Strings.formatPennies(pl));
				}
				if (glEntry.getDescrLength() > maxDescrLength)
					maxDescrLength = glEntry.getDescrLength();
			}
			// else the transaction date is after end date and thus has no effect
		}
		glFile.close();
		// Now add a fake closing transaction to Net Worth Account
		// to make Statement show the correct ending balance in that Account

		String s = Strings.formatPennies(-plAccount.getDeltaBal());
		logger.logDebug("VLUtil.computeAccountBalances: s=" + s);

		GLEntry netIncome = new GLEntry("CLOS", "  0", "E", "C", "100", plAccount.getAccountNo(), s,
				"Computed Net Income", end.toString("yymmdd"));
		plAccount.addGLEntry(netIncome);
		logger.logDebug("extractBalances normal completion");
		return maxDescrLength;
	}

	public void computeTotals(Chart chart)
	{
		ChartElement2[] chartElements = chart.getChartElementList();
		// int nElements = chartElements.length;
		long[][] levelTotals = new long[chart.getMaxLevel()][2]; // [level][0]=begin [level][1]=delta
		int currentLevel = 0;
		ChartElement2 currentElement;
		Account currentAccount;
		String name;

		for (int i = 0; i < levelTotals.length; ++i)
			for (int j = 0; j < 2; ++j)
				levelTotals[i][j] = 0L;
		for (int i = 0; i < chartElements.length; ++i)
		{
			currentElement = chartElements[i];
			name = currentElement.name;
			currentLevel = currentElement.getLevel();
			if (name.equals("group"))
				++currentLevel;
			else if (name.equals("account"))
			{
				currentAccount = chart.getAccount(currentElement.accountIndex);
				levelTotals[currentLevel][0] += currentAccount.getBeginBal();
				levelTotals[currentLevel][1] += currentAccount.getDeltaBal();
			} else if (name.equals("total"))
			{
				levelTotals[currentLevel - 1][0] = levelTotals[currentLevel][0];
				levelTotals[currentLevel - 1][1] = levelTotals[currentLevel][1];
				--currentLevel;
			}
		}
	}
}
