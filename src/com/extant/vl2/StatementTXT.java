package com.extant.vl2;
/*
 * StatementTXT.java
 * @author jms
 * Provides the methods to produce financial statements and chart listings
 * in .txt format.
 *
 * Created on February 21, 2005, 11:04 AM
 * Major Revision September 16, 2018 10:17 AM
 *
 */

import com.extant.utilities.*;
import java.io.*;

public class StatementTXT extends AbstractStatement
{
	void initialize()
	{
		// For debugging:
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		logger.log("debugging message entry StatementTXT.initialize()");
		logger.log("plElement: " + chart.plElement.toString());

		try
		{
			outfile = new UsefulFile(outfileName, "w");

			if (reportLevel <= 0 || reportLevel > maxLevel)
				reportLevel = chart.getMaxLevel();

			// Compute the maximum length of a text line
			int maxTextLineLength = 0;
			if (showAccount)
				maxTextLineLength += maxAccountNoLength;
			maxTextLineLength += (indentPerLevel * maxLevel);
			maxTextLineLength += maxTitle.length();

			// Compute the maximum length of an amount line
			int maxAmountLineLength = 0;
			if (showAccount)
				maxAmountLineLength += maxAccountNoLength;
			maxAmountLineLength += indentPerLevel * maxLevel;
			maxAmountLineLength += maxTitle.length();
			if (showAmount)
				maxAmountLineLength += VL2Config.MAX_AMOUNT_LENGTH;

			// Compute the maximum length of all statement lines
			int maxLineLength = maxTextLineLength;
			if (maxAmountLineLength > maxLineLength)
				maxLineLength = maxAmountLineLength;

			logger.logDebug("maxTextLineLength=" + maxTextLineLength);
			logger.logDebug("maxAmountLineLength=" + maxAmountLineLength);
			logger.logDebug("maxLineLength=" + maxLineLength);
			logger.logDebug("StatementTXT:initialize: shortDateFormat=" + shortDateFormat);
			logger.logDebug("StatementTXT:initialize: dollarFormat=" + dollarFormat);
			logger.logDebug("paperWidth=" + paperWidth);
			if (maxLineLength <= paperWidth)
				logger.logDebug("This report can be printed with portrait paper orientation.");
			else
				logger.logDebug("This report requires landscape (or wider) paper orientation.");

			logger.logDebug("outfileName=" + outfileName);
			outfile = new UsefulFile(outfileName, "w");

			stmtTxtFormatReport = new UsefulFile(stmtTxtFormatReportFilename, "w");
			makeFormatReport();
		} catch (IOException iox)
		{
			logger.log("StatementTXT.initialize: IOException " + iox.getMessage());
		}
	}

	// Methods to set values which overrride defaults
	void setOutfileName(String name)
	{
		outfileName = name;
	}

	void setPrintOrientation(String orientation)
	{
		printOrientation = "portrait";
		VL2.vl2Config.setPrintOrientation("portrait");
	}

	void setShowAccount(boolean yes)
	{
		showAccount = yes;
	}

	void setShowAmount(boolean yes)
	{
		showAmount = yes;
	}

	@Override // implements abstract method from AbstractStatement
	void printTextLine(ChartElement element)
	{ // Print a line for an element that contain only text (titles)
		logger.logDebug("printTextLine element=" + element.toString());
		image = "";
		String format = element.getAttribute("format");
		if (format.contains("newpage"))
			image += "\f";
		if (format.contains("center"))
		{
			logger.log("printtextLine centering '" + element.getAttribute("title") + "' in " + paperWidth);
			image += Strings.center(element.getAttribute("title"), paperWidth);
		} else
		{
			if (showAccount)
				image += Strings.leftJustify("", maxAccountNoLength);
			image += Strings.leftJustify("", indent(element).length());
			image += element.getAttribute("title");
		}
		if (format.contains("ending"))
			image += "\r\n" + Strings.center(latestDate.toString(longDateFormat), paperWidth);
		if (format.contains("period"))
			image += "\r\n" + Strings.center(
					earliestDate.toString(longDateFormat) + " - " + latestDate.toString(longDateFormat), paperWidth);
		if (format.contains("skip"))
			image += "\r\n";
		outfile.println(image);
		logger.logDebug("printed: '" + image + "'");
		return;
	}

	@Override
	// implements abstract method from AbstractStatement
	void printAmountLine(ChartElement element)
	{ // Print a line for an element which defines an amount
		image = "";
		String elementName = element.name.toLowerCase();
		String amountPortion;
		String title = element.getAttribute("title").trim();
		String accountNo = element.getAttribute("no").trim();
		long amount = element.beginBal + element.deltaBal;
		if (fixSign(element))
			amount = -amount;
		// Do not print any line containing "noPrint" tag
		if (element.getAttribute("tag").equalsIgnoreCase("noPrint"))
		{
			logger.logDebug("StatementTXT.printAmountLine: bypass printing of element: " + element.toString());
			return;
		}

		if (elementName.equals("account"))
		{
			logger.logDebug("printAmountLine: title=" + title + " accountNo=" + accountNo);
			if (showAccount)
				image += Strings.leftJustify(accountNo, chart.maxAccountNoLength);
			image += indent(element);
			image += Strings.leftJustify(title, chart.maxTitleLength);
			if (showAmount)
				image += Strings.rightJustify(formatAmount(amount, dollarFormat), VL2Config.MAX_AMOUNT_LENGTH);
		} else if (elementName.equals("total"))
		{
			title = element.getAttribute("title");
			if (showAccount)
				image += Strings.leftJustify(" ", chart.maxAccountNoLength);
			image += indent(element);
			image += Strings.leftJustify(title, chart.maxTitleLength);
			if (showAmount)
			{
				amountPortion = Strings.rightJustify(formatAmount(amount, dollarFormat), VL2Config.MAX_AMOUNT_LENGTH);
				logger.log("printAmountLine total amountPortion='" + amountPortion + "'");
				logger.logDebug("amountPortion length=" + amountPortion.length());
				// logger.logDebug("MAX_AMOUNT_LENGTH=" + VL2Config.MAX_AMOUNT_LENGTH);
				image += Strings.rightJustify(amountPortion, VL2Config.MAX_AMOUNT_LENGTH);
			}
			if (element.getAttribute("format").contains("skip"))
				image += "\r\n";
		}
		outfile.println(image);
		logger.logDebug("printed: '" + image + "'");
		return;
	}

	// Print an amount line with specific values (not obtained from an element)
	private void printAmountLine(String acctNo, int indent, String title, long amount)
	{
		image = "";
		if (showAccount)
			image += Strings.leftJustify(acctNo, chart.maxAccountNoLength);
		image += Strings.leftJustify("", indent);
		image += Strings.leftJustify(title, chart.maxTitleLength);
		image += formatAmount(amount, dollarFormat);
		outfile.println(image);
	}

	@Override
	// Implements abstract method from AbstractStatement
	// expects the retained earnings account element (plElement)
	void printRetainedEarnings(ChartElement element)
	{
		logger.logDebug("printRetainedEarnings: " + element.toString());
		if (element != plElement)
			logger.logFatal("plElement mismatch in printRetainedEarnings");
		String beginDate = new Julian(vl2Config.getEarliestDate()).toString(chart.getShortDateFormat());
		String endDate = new Julian(vl2Config.getLatestDate()).toString(chart.getShortDateFormat());
		logger.logDebug("plElement: " + plElement.toString()); // -110,854.29 -398.80 -111,253.09

		// Print R/E at begin
		String acctNo = element.getAttribute("AccountNo");
		int indent = element.level * indentPerLevel;
		printAmountLine(acctNo, indent, "R/E " + beginDate, element.beginBal); // correct -110,854.29

		// Print R/E change during period
		printAmountLine(acctNo, indent, "Profit (Loss) during period", -element.deltaBal); // correct -398.80

		// Print R/E at end
		// plElement.putAttribute("title", "R/E " + endDate);
		// long REEnd = plElement.beginBal + plElement.deltaBal;
		// printAmountLine(plElement, REEnd);
		printAmountLine(acctNo, indent, "R/E " + endDate, -(element.beginBal + element.deltaBal));
		// correct -111,253.09

		// Find total liabilities, equity, and retained earnings (tag "TLRE")
		// and add the retained earnings (REEnd) to the deltaBal

		// TODO The total retained earnings (at end of period) must be manually added to
		// 'Total Liabilities & Retained Earnings', because the retained earnings
		// total was not available at the time the totals were computed
		// (by VLUtil.computeElementTotals).
		// Possibly, VLUtil.computeElementTotals could be modified to accomplish the
		// same thing, but that approach appears to be much more difficult & risky.
		//
		// Solution Plan A: Plan A does not work!
		// (a) add tag="TLRE" to the total element titled
		// 'Total Liabilities & Retained Earnings' (b) add code here
		// use Chart.findTagElements("TLRE") to find the TLRE element and apply
		// deltaBal += (plElement.beginBal + plElement.deltaBal);
		// (c) call printAmountLine(TNLWElement); and (d) in AbstractStatement
		// add code to suppress printing of elements with tag="TLRE" or tag="noPrint".
		//
		// Solution Plan B:
		// Modify VLUtil.computeElementTotals
	}

	private String formatAmount(long amount, String dollarFormat)
	{
		String ans = Strings.rightJustify(Strings.formatPennies(amount, dollarFormat), VL2Config.MAX_AMOUNT_LENGTH);
		ans = Strings.trimRight(ans);
		if (amount >= 0)
			ans += " ";
		// logger.log("formatAmount returns '" + ans + "'");
		return ans;
	}

	public static String indent(ChartElement element)
	{
		String elementName = element.name.toLowerCase();
		int level = element.level;
		if (elementName.equals("total"))
			--level;
		int x = level * indentPerLevel;
		return Strings.leftJustify("", x);
	}

	private boolean fixSign(ChartElement element)
	{
		String type = element.getAttribute("type");
		return type.equals("L") || type.equals("I") || type.equals("R");
	}

	private void makeFormatReport()
	{
		stmtTxtFormatReport.println("orientation=" + vl2Config.getPrintOrientation());
		stmtTxtFormatReport.println("paperWidth=" + vl2Config.getPaperWidth());
		stmtTxtFormatReport.println("showAccount=" + showAccount);
		stmtTxtFormatReport.println("showAmount=" + showAmount);
		stmtTxtFormatReport
				.println("maxAcctNo='" + chart.getMaxAcctNo() + "'" + " (" + chart.getMaxAccountNoLength() + " chars)");
		stmtTxtFormatReport.println("maxTitle='" + chart.getMaxTitle() + "' (" + chart.getMaxTitleLength() + " chars)");
		// stmtTxtFormatReport.println("maxAmountLine=" + maxAmountLine);
		// stmtTxtFormatReport.println("maxTextLineLength=" + maxTextLineLength);
		// stmtTxtFormatReport.println("maxAmountLineLength=" + maxAmountLineLength);
		stmtTxtFormatReport.close();
	}

	String stmtTxtFormatReportFilename = VL2.workDir + "stmtTxtFormatReport.txt";
	UsefulFile stmtTxtFormatReport;
}
