/*
 * VLUtil.java
 * Utilities for Visual Ledger package
 *
 * Created on November 7, 2002, 4:02 PM
 */

package com.extant.vl2;

import com.extant.utilities.UsefulFile;
import com.extant.utilities.Julian;
import com.extant.utilities.LogFile;
import com.extant.utilities.Strings;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author jms
 */
public class VLUtil
{
	static LogFile logger = VL2.logger;

	/*
	 * postToAccounts processes the GLFile to (a) add the GLEntries to the Account
	 * glEntries vector and (b) compute the beginning balance (transactions in
	 * journal BALF) and the delta amount (the remaining transactions up to and
	 * including the latest date) for each account. These values are stored in the
	 * ChartElement fields beginBal and deltaBal respectively, for the ChartElement
	 * associated with that account.
	 *
	 * If a transaction in the GLFile refers to an account which is not in the
	 * Chart, a VLException is thrown.
	 *
	 * This method does not compute total amounts.
	 * 
	 * This method is obsolete and not used in VL2 9-26-18
	 */
	public static void postToAccounts(String glFileName, Chart chart) throws IOException, VLException
	{
		// For debugging:
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

		GLEntry glEntry;
		Julian startDate = null;
		Julian endDate = null;
		Julian transDate;
		String type;
		boolean plType;
		String currentAcctNo = "";
		Account currentAccount = null;
		int currentElementIndex;
		ChartElement currentElement = null;
		long amount;

		// TODO Are you sure ??
		chart.clearElementBalances();
		// chart.removeGLEntries();

		Account plAccount = chart.getPLAccount();
		if (plAccount == null)
			logger.logFatal("VLUtil.postToAccounts: plAccount is null!");
		int plIndex = plAccount.elementIndex;
		ChartElement plElement = chart.chartElements.elementAt(plIndex);
		if (plElement == null)
			logger.logFatal("VLUtil.postToAccounts: plElement is null!");
		logger.logInfo("Posting to Accounts ...");
		UsefulFile glFile = new UsefulFile(glFileName, "r");
		int lineNo = 0;

		while (!glFile.EOF())
		{
			glEntry = new GLEntry(glFile.readLine(UsefulFile.ALL_WHITE));
			++lineNo;
			logger.log("VLUtil.PostToAccounts: Line " + lineNo + " GLEntry: " + glEntry.toString());
			transDate = new Julian(glEntry.getField("DATE"));
			if (glEntry.getField("JREF").equals("BALF"))
				if (startDate == null)
					startDate = transDate;
				else if (!transDate.isEqualTo(startDate))
					logger.logFatal("BALF transactions have varying dates");
			if (endDate == null)
				endDate = transDate;
			else if (transDate.isLaterThan(endDate))
				endDate = transDate;
			if (!glEntry.getAccountNo().equals(currentAcctNo))
			{
				currentAcctNo = glEntry.getAccountNo();
				currentAccount = chart.findAcctByNo(currentAcctNo);
				if (currentAccount == null)
					throw new VLException(VLException.ACCT_NOT_IN_CHART,
							"VLUtil.postToAccounts: " + currentAcctNo + " (GL Line " + lineNo + ")");
				currentElementIndex = currentAccount.elementIndex;
				currentElement = chart.chartElements.elementAt(currentElementIndex);
			}

			// Process this transaction
			// Add to the vector of transactions in this account
			currentAccount.glEntries.add(glEntry);
			amount = glEntry.getSignedAmount();
			type = currentAccount.getType();
			plType = type.equals("I") || type.equals("E");

			if (glEntry.getField("JREF").equals("BALF"))
			{
				// Add this transaction amount to the element beginBal
				currentElement.beginBal += amount;
				// logger.logDebug("element.beginBal updated to " + currentElement.beginBal);

				// Add Income & Expense items to P/L Element Begin balance
				// TODO Probably unnecessary
				if (plType)
					plElement.beginBal += amount;
			} else
			{
				// Add this transaction amount to the element deltaBal
				currentElement.deltaBal += amount;
				logger.logDebug("element.deltaBal updated to " + currentElement.deltaBal);

				if (plType)
					// Add this transaction amount to plElement deltaBal
					plElement.deltaBal += amount;
			}

			// Add Income & Expense items to P/L element Delta balance
			if (plType)
				plElement.deltaBal += amount;
			logger.logDebug("element=" + currentElement.toString());
		} // End of GL transactions

		glFile.close();
		logger.logInfo("VLUtil.postToAccounts: last GLEntry processed");
		logger.logInfo("plElement=" + plElement.toString());

		// // Transfer the computed Account balances to the matching elements
		// // Enumeration<Account> accounts;
		// logger.logInfo("starting transfer balances to elements");
		// Vector<Account> accounts = chart.accounts;
		// // while (accounts.hasMoreElements())
		// for (int i = 0; i < accounts.size(); ++i)
		// {
		// currentAccount = accounts.elementAt(i);
		// currentAcctNo = currentAccount.getAccountNo();
		// ChartElement chartElement = mapChartAccountToElement(chart,
		// currentAcctNo);
		// logger.logInfo("transfering account " + currentAcctNo + " to element " +
		// chartElement.toString());
		// chartElement.setBeginBal(currentAccount.getBeginBal());
		// chartElement.setDeltaBal(currentAccount.getDeltaBal());
		// }
		// logger.logInfo("transfer complete; now handle pl account");
		// // Now add a fake closing transaction to Net Worth Account
		// // to make Statement show the correct ending balance in that Account
		// String s = Strings.formatPennies(-plAccount.getDeltaBal());
		// logger.logDebug("VLUtil.postToAccounts s=" + s);
		//
		// String plAccountNo = plAccount.getAccountNo();
		// GLEntry netIncome = new GLEntry("CLOS", " 0", "E", "C", "100",
		// plAccountNo,
		// s, "Computed Net Income",
		// endDate.toString("yymmdd"));
		// plAccount.addGLEntry(netIncome);
		// ChartElement plElement = mapChartAccountToElement(chart, plAccountNo);
		//
		logger.logDebug("postToAccounts normal completion");
	}

	// Compute totals in ElementList
	public static void computeElementTotals(Chart chart)
	{
		// For debugging
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

		logger.logDebug("computing ElementTotals ...");
		Vector<ChartElement> chartElements = chart.getChartElements();
		logger.logDebug("maxLevel=" + chart.getMaxLevel());
		long[][] levelTotals = new long[chart.getMaxLevel() + 1][2]; // [level][0]=begin [level][1]=delta
		int maxLevel = chart.getMaxLevel();
		logger.logDebug("levelTotals dimensions=" + (maxLevel + 1) + " x 2  for begin & delta");
		String[] levelTitles = new String[maxLevel + 1];
		ChartElement currentElement;
		String currentElementName;
		int currentElementLevel;
		String currentElementTitle;
		String currentGroupTitle = ""; // used in debugging
		String currentAccountNo;

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
				levelTitles[currentElementLevel] = currentElementTitle;

			} else if (currentElementName.equals("section"))
			{
				currentElementTitle = currentElement.getAttribute("title");
				levelTitles[currentElementLevel] = currentElementTitle;
				// Added 9-27-18:
				zeroLevelTotals(levelTotals);
				levelTotals[currentElementLevel][0] = 0L;
				levelTotals[currentElementLevel][1] = 0L;

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
				// Zero balances for this level
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

				// String tag = currentElement.getAttribute("tag");
				// if (tag.contains("PL"))
				// {
				// if (tag.contains("Balance"))
				// {
				// ChartElement plElement = chart.getPLElement();
				// plElement.deltaBal = -currentElement.deltaBal;
				// }
				// logger.log("(VLUtil.computeElementTotals: tag=" + tag + " " +
				// currentElement.beginBal + " "
				// + currentElement.deltaBal);
				// }
			}
		}
	}

	private static void zeroLevelTotals(long[][] levelTotals)
	{
		for (int i = 0; i < levelTotals.length; ++i)
			for (int j = 0; j < 2; ++j)
				levelTotals[i][j] = 0L;
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

	// Map an Account number to its corresponding Element
	public static ChartElement mapChartAccountToElement(Chart chart, String accountNo)
	{
		Account account = chart.findAcctByNo(accountNo);
		int elementIndex = account.getAccountElementIndex();
		return chart.chartElements.get(elementIndex);
	}

	// // Locate the P&L element
	// public ChartElement findplElement(Chart chart)
	// {
	// return mapChartAccountToElement(chart, chart.getPLAccount().accountNo);
	// }
	//
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
