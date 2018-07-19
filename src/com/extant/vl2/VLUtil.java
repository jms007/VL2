/*
 * VLUtil.java
 * Utilities for Visual Ledger package
 *
 * Created on November 7, 2002, 4:02 PM
 */

package com.extant.vl2;

import com.extant.utilities.UsefulFile;
//import com.lowagie.text.ElementListener; TODO REMOVE
import com.extant.utilities.Julian;
import com.extant.utilities.LogFile;
import com.extant.utilities.Strings;

import java.io.IOException;
import java.util.Vector;
//import java.util.Enumeration; TODO REMOVE
//import java.util.Vector; TODO REMOVE

/**
 *
 * @author jms
 */
public class VLUtil
{
	static LogFile logger = VL2.logger;

	// /*
	// * postToAccounts processes the GLFile to compute the beginning balance
	// * (transactions in journal BALF) and the delta amount (the remaining
	// * transactions up to and including the latest date) for each account. These
	// * values are stored in the Account fields beginBal and deltaBal respectively.
	// *
	// * If a transaction in the GLFile refers to an account which is not in the
	// * Chart, a VLException is thrown.
	// *
	// * This method does not compute total amounts.
	// */
	// public static void postToAccounts(String glFileName, Chart chart, LogFile
	// logger) throws IOException, VLException
	// {
	// // For debugging:
	// // logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
	//
	// GLEntry glEntry;
	// Julian startDate = null;
	// Julian endDate = null;
	// Julian transDate;
	// String type;
	// boolean plType;
	// String currentAcctNo = "";
	// Account currentAccount = null;
	// int currentElementIndex;
	// ChartElement2 currentElement = null;
	// long amount;
	//
	// // chart.clearAccountBalances();
	// // chart.removeGLEntries(); // !! Are you sure ??
	// Account plAccount = chart.getPLAccount();
	// if (plAccount == null)
	// logger.logFatal("VLUtil:170 plAccount is null!");
	// int plIndex = plAccount.elementIndex;
	// ChartElement2 plElement = chart.chartElements.elementAt(plIndex);
	// UsefulFile glFile = new UsefulFile(glFileName, "r");
	// String maxDescr = "";
	// int maxDescrLength = 0;
	// int lineNo = 0;
	// while (!glFile.EOF())
	// {
	// glEntry = new GLEntry(glFile.readLine(UsefulFile.ALL_WHITE));
	// ++lineNo;
	// logger.log("VLUtil.PostToAccounts: Line " + lineNo + " GLEntry:" +
	// glEntry.toString());
	// transDate = new Julian(glEntry.getField("DATE"));
	// if (glEntry.getField("JREF").equals("BALF"))
	// if (startDate == null)
	// startDate = transDate;
	// else if (!transDate.isEqualTo(startDate))
	// logger.logFatal("BALF transactions have varying dates");
	// if (endDate == null)
	// endDate = transDate;
	// else if (transDate.isLaterThan(endDate))
	// endDate = transDate;
	// if (!glEntry.getAccountNo().equals(currentAcctNo))
	// {
	// currentAcctNo = glEntry.getAccountNo();
	// currentAccount = chart.findAcctByNo(currentAcctNo);
	// if (currentAccount == null)
	// throw new VLException(VLException.ACCT_NOT_IN_CHART,
	// "VLUtil.postToAccounts: " + currentAcctNo + " (GL Line " + lineNo + ")");
	// currentElementIndex = currentAccount.elementIndex;
	// currentElement = chart.chartElements.elementAt(currentElementIndex);
	// }
	//
	// // Process this transaction
	// amount = glEntry.getSignedAmount();
	// type = currentAccount.getType();
	// plType = type.equals("I") || type.equals("E");
	//
	// if (glEntry.getField("JREF").equals("BALF"))
	// {
	// // Add this transaction amount to the element beginBal
	// currentElement.beginBal += amount;
	//
	// // Add Income & Expense items to P/L Element Begin balance
	// if (plType)
	// plElement.beginBal += amount;
	// } else
	// // Add this transaction amount to the element deltaBal
	// plElement.deltaBal += amount;
	//
	// // Add Income & Expense items to P/L element Delta balance
	// if (plType)
	// plElement.deltaBal += amount;
	//
	// if (glEntry.getDescrLength() > maxDescrLength)
	// {
	// maxDescr = glEntry.getDescr();
	// maxDescrLength = maxDescr.length();
	// }
	// } // End of GL transactions
	// glFile.close();
	// logger.logInfo("VLUtil.postToAccounts: last GLEntry processed");
	//
	// // // Transfer the computed Account balances to the matching elements
	// // // Enumeration<Account> accounts;
	// // logger.logInfo("starting transfer balances to elements");
	// // Vector<Account> accounts = chart.accounts;
	// // // while (accounts.hasMoreElements())
	// // for (int i = 0; i < accounts.size(); ++i)
	// // {
	// // currentAccount = accounts.elementAt(i);
	// // currentAcctNo = currentAccount.getAccountNo();
	// // ChartElement2 chartElement = mapChartAccountToElement(chart,
	// currentAcctNo);
	// // logger.logInfo("transfering account " + currentAcctNo + " to element " +
	// // chartElement.toString());
	// // chartElement.setBeginBal(currentAccount.getBeginBal());
	// // chartElement.setDeltaBal(currentAccount.getDeltaBal());
	// // }
	// // logger.logInfo("transfer complete; now handle pl account");
	// // // Now add a fake closing transaction to Net Worth Account
	// // // to make Statement show the correct ending balance in that Account
	// // String s = Strings.formatPennies(-plAccount.getDeltaBal());
	// // logger.logDebug("VLUtil.postToAccounts s=" + s);
	// //
	// // String plAccountNo = plAccount.getAccountNo();
	// // GLEntry netIncome = new GLEntry("CLOS", " 0", "E", "C", "100",
	// plAccountNo,
	// // s, "Computed Net Income",
	// // endDate.toString("yymmdd"));
	// // plAccount.addGLEntry(netIncome);
	// // ChartElement2 plElement = mapChartAccountToElement(chart, plAccountNo);
	// //
	// logger.logDebug("postToAccounts normal completion");
	// }
	//
	// Compute totals in ElementList

	public static void computeElementTotals(Chart chart)
	{
		// For debugging
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

		Vector<ChartElement2> chartElements = chart.getChartElements();
		logger.logDebug("maxLevel=" + chart.getMaxLevel());
		long[][] levelTotals = new long[chart.getMaxLevel() + 1][2]; // [level][0]=begin [level][1]=delta
		int maxLevel = chart.getMaxLevel();
		logger.log("levelTotals dimensions=" + (maxLevel + 1) + " x 2  for begin & delta");
		String[] levelTitles = new String[maxLevel + 1];
		ChartElement2 currentElement;
		String currentElementName;
		int currentElementLevel;
		String currentElementTitle;
		String currentGroupTitle = ""; // used in debugging
		String currentAccountNo;

		logger.logDebug("Entering computeElementTotals");
		for (int i = 0; i < levelTotals.length; ++i)
			for (int j = 0; j < 2; ++j)
				levelTotals[i][j] = 0L;
		for (int i = 0; i < chartElements.size(); ++i)
		{
			currentElement = chartElements.elementAt(i);
			currentElementName = currentElement.name;
			currentElementLevel = currentElement.level;
			currentElementTitle = currentElement.getAttribute("title");
			logger.logDebug("element[" + i + "] level=" + currentElementLevel + " " + currentElementName + " "
					+ currentElementTitle);

			if (currentElementName.equals("chart"))
			{
				// currentElementLevel = currentElement.level; // should be zero
				levelTitles[currentElementLevel] = currentElementTitle;

			} else if (currentElementName.equals("section"))
			{
				// currentElementLevel = currentElement.level;
				currentElementTitle = currentElement.getAttribute("title");
				levelTitles[currentElementLevel] = currentElement.getAttribute("title");
				levelTitles[currentElementLevel] = currentElementTitle;
				// For Debugging
				if (currentElementTitle.equals("INCOME STATEMENT"))
					dumpElementTotals("start " + currentElementTitle, levelTotals, maxLevel, levelTitles);

			} else if (currentElementName.equals("group"))
			{
				// currentElementLevel = currentElement.level;
				// currentElementTitle = currentElement.getAttribute("title");
				currentGroupTitle = currentElement.getAttribute("title");
				levelTitles[currentElementLevel] = currentGroupTitle;
				levelTotals[currentElementLevel][0] += currentElement.beginBal;
				levelTotals[currentElementLevel][1] += currentElement.deltaBal;
				logger.logDebug("updating balances in level " + currentElementLevel + ": "
						+ Strings.formatPennies(levelTotals[currentElementLevel][0]) + " "
						+ Strings.formatPennies(levelTotals[currentElementLevel][1]));
				// // Zero balances for this level
				// levelTotals[currentElementLevel][0] = 0L;
				// levelTotals[currentElementLevel][1] = 0L;

			} else if (currentElementName.equals("account"))
			{
				// No modification of levels
				currentAccountNo = currentElement.getAttribute("no");
				logger.logDebug("account " + currentAccountNo + " currentElementLevel=" + currentElementLevel);
				levelTotals[currentElementLevel][0] += currentElement.beginBal;
				levelTotals[currentElementLevel][1] += currentElement.deltaBal;

			} else if (currentElementName.equals("total"))
			{
				levelTitles[currentElementLevel] = currentElement.getAttribute("title") + " currentLevel="
						+ currentElementLevel;
				logger.logDebug("totaling " + levelTitles[currentElementLevel] + ": "
						+ Strings.formatPennies(levelTotals[currentElementLevel][0]) + " "
						+ Strings.formatPennies(levelTotals[currentElementLevel][1]));
				logger.logDebug("getting totals from level " + currentElementLevel + ":");
				logger.logDebug(Strings.formatPennies(levelTotals[currentElementLevel][0]) + "   "
						+ Strings.formatPennies(levelTotals[currentElementLevel][1]));
				currentElement.beginBal = levelTotals[currentElementLevel][0];
				currentElement.deltaBal = levelTotals[currentElementLevel][1];
				logger.logDebug("Promoting these totals to update level " + (currentElementLevel - 1));
				levelTotals[currentElementLevel - 1][0] += currentElement.beginBal;
				levelTotals[currentElementLevel - 1][1] += currentElement.deltaBal;
				logger.logDebug("zeroing totals in level " + currentElementLevel);
				levelTotals[currentElementLevel][0] = 0L;
				levelTotals[currentElementLevel][1] = 0L;
			}
			// levelTitles[currentElementLevel] = currentGroupTitle;
			// for (int j = 0; j < 2; ++j)
			// levelTotals[currentElementLevel][j] = 0L;
			// if (currentElement.beginBal != 0 || currentElement.deltaBal != 0)
			// dumpElementTotals(levelTotals, chart.maxLevel + 1, levelTitles);
		}
		// For Debugging
		dumpElementTotals("exit ComputeElementTotals", levelTotals, chart.maxLevel, levelTitles);
		logger.logDebug("ComputeElementTotals normal completion");
		logger.setLogLevel(LogFile.NORMAL_LOG_LEVEL);
	}

	// For debugging
	public static void dumpElementTotals(String msg, long[][] levelTotals, int maxLevel, String[] levelTitles)
	{
		logger = VL2.logger;
		if (logger.getLogLevel() >= LogFile.DEBUG_LOG_LEVEL)
		{
			logger.logDebug("dumpElementTotals " + msg + ":");
			for (int i = 0; i < maxLevel; ++i)
				logger.log("level " + i + " " + levelTitles[i] + " beginBal=" + Strings.formatPennies(levelTotals[i][0])
						+ " deltaBal=" + Strings.formatPennies(levelTotals[i][1]));
		}
	}

	// Connect an Account number to its corresponding Element
	public static ChartElement2 mapChartAccountToElement(Chart chart, String accountNo)
	{
		Account account = chart.findAcctByNo(accountNo);
		int elementIndex = account.getAccountElementIndex();
		return chart.chartElements.get(elementIndex);
	}

	// method to test mapChartAccountToElement() using quito/17 cash account as
	// example
	// public static void test(Chart chart, String accountNo)
	// {
	// ChartElement2 element = mapChartAccountToElement(chart, accountNo);
	// System.out.println("element: " + element.toString());
	// }

	// For debugging: display the current element list
	public static void showChartElements(Chart chart, String OutFileName)
	{
		UsefulFile elementsOutFile = null;
		try
		{
			elementsOutFile = new UsefulFile(OutFileName, "w");
			elementsOutFile.appendLine("VLUtil.showElementsList " + new Julian().toString("mm-dd-yy hhmmss"));
		} catch (IOException iox)
		{
			System.out.println("VLUtil.showElementList: cannot open " + OutFileName + " " + iox.getMessage());
			System.exit(2);
		}
		for (int i = 0; i < chart.chartElements.size(); ++i)
			elementsOutFile.println(chart.chartElements.elementAt(i).toString());
		elementsOutFile.close();
	}
}
