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
	// String workDir;
	// String outfileName;
	// UsefulFile outfile;
	// String printOrientation;
	// int paperWidth;
	// boolean showAmount;
	// boolean showAccount;
	// int reportLevel;
	// int maxLevel;
	// int indentPerLevel;
	// String glFilename;
	// String dollarFormat;
	// String longDateFormat;
	// String shortDateFormat;
	// Julian begin;
	// Julian end;
	// int maxIndentLength;
	// String maxAccountNo;
	// int maxAccountNoLength;
	// String maxTitle;
	// int maxTitleLength;
	// int MAX_AMOUNT_LENGTH;

	void initialize()
	{
		// For debugging:
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
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

	// Methods to set values to overrride defaults
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
	{ // Print lines that contain only text (titles)
		logger.logDebug("printTextLine element=" + element.toString());
		String image = "";
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
	// prints a line which contains an amount (except for PL lines)
	void printAmountLine(ChartElement element)
	{
		String image = "";
		String amountPortion;
		String title = element.getAttribute("title").trim();
		String accountNo = element.getAttribute("no").trim();
		long amount = element.beginBal + element.deltaBal;
		if (fixSign(element))
			amount = -amount;
		// Do not print lines containing "PL" tag
		if (element.getAttribute("tag").contains("PL"))
			return;
		logger.logDebug("StatementTXT.printAmountLine DollarFormat='" + chart.getDollarFormat() + "'");

		if (element.name.equalsIgnoreCase("account"))
		{
			logger.logDebug("printAmountLine: title=" + title + " accountNo=" + accountNo);
			if (showAccount)
				image += Strings.leftJustify(accountNo, chart.maxAccountNoLength);
			image += indent(element);
			image += Strings.leftJustify(title, chart.maxTitleLength);
			if (showAmount)
				image += Strings.rightJustify(amountPrint(amount, dollarFormat), VL2Config.MAX_AMOUNT_LENGTH);
		} else if (element.name.equalsIgnoreCase("total"))
		{
			title = element.getAttribute("title");
			if (showAccount)
				image += Strings.leftJustify(" ", chart.maxAccountNoLength);
			image += indent(element);
			image += Strings.leftJustify(title, chart.maxTitleLength);
			if (showAmount)
			{
				amountPortion = Strings.rightJustify(amountPrint(amount, dollarFormat), VL2Config.MAX_AMOUNT_LENGTH);
				logger.log("printAmountLine total amountPortion='" + amountPortion + "'");
				logger.logDebug("amountPortion length=" + amountPortion.length());
				logger.logDebug("MAX_AMOUNT_LENGTH=" + VL2Config.MAX_AMOUNT_LENGTH);
				image += Strings.rightJustify(amountPortion, VL2Config.MAX_AMOUNT_LENGTH);
			}
			if (element.getAttribute("format").contains("skip"))
				image += "\r\n";
		}
		outfile.println(image);
		logger.logDebug("printed: '" + image + "'");
		// if (image.length() > maxAmountLineLength)
		// {
		// maxAmountLine = image;
		// maxAmountLineLength = image.length();
		// }
		return;
	}

	// Print retained earnings
	void printAmountLine(ChartElement element, long amount)
	{
		String image = "";
		String title = element.getAttribute("title").trim();
		String accountNo = element.getAttribute("no").trim();
		// Print parameter 'amount' instead of beginBal + deltaBal;
		if (fixSign(element))
			amount = -amount;

		if (element.name.equalsIgnoreCase("account"))
		{
			logger.logDebug("printAmountLine: title=" + title + " accountNo=" + accountNo);
			if (showAccount)
				image += Strings.leftJustify(accountNo, chart.maxAccountNoLength);
			image += indent(element);
			image += Strings.leftJustify(title, chart.maxTitleLength);
			if (showAmount)
				image += Strings.rightJustify(amountPrint(amount, dollarFormat), VL2Config.MAX_AMOUNT_LENGTH + 1);
		} else if (element.name.equalsIgnoreCase("total"))
		{
			title = element.getAttribute("title");
			if (showAccount)
				image += Strings.leftJustify(" ", chart.maxAccountNoLength);
			image += indent(element);
			image += Strings.leftJustify(title, chart.maxTitleLength);
			if (showAmount)
			{
				image += Strings.rightJustify(amountPrint(amount, dollarFormat), VL2Config.MAX_AMOUNT_LENGTH + 1);
			}
			if (element.getAttribute("format").contains("skip"))
				image += "\r\n";
		}
		outfile.println(image);
		// if (image.length() > maxAmountLineLength)
		// {
		// maxAmountLine = image;
		// maxAmountLineLength = image.length();
		// }
		logger.logDebug("printed: '" + image + "'");
		return;
	}

	private String amountPrint(long amount, String dollarFormat)
	{
		String ans = Strings.trimRight(Strings.formatPennies(amount, dollarFormat));
		if (amount >= 0)
			ans += " ";
		logger.log("amountPrint returns '" + ans + "'");
		return ans;
	}

	@Override
	// Implements abstract method from AbstractStatement
	// expects the retained earnings account element
	void printRetainedEarnings(ChartElement element)
	{
		ChartElement TLNWelement;
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		String beginDate = new Julian(vl2Config.getEarliestDate()).toString(chart.getShortDateFormat());
		String endDate = new Julian(vl2Config.getLatestDate()).toString(chart.getShortDateFormat());
		ChartElement plElement = chart.getPLElement();
		logger.logDebug("plElement: " + plElement.toString());

		// Print R/E at begin
		plElement.putAttribute("title", "R/E " + beginDate);
		logger.logDebug("print plElement-begin=" + plElement.beginBal);
		// long amount = plElement.beginBal;
		printAmountLine(plElement, plElement.beginBal);

		// Print R/E change during period
		plElement.putAttribute("title", "Profit (Loss) during period");
		logger.logDebug("print plElement-delta" + plElement.deltaBal);
		printAmountLine(plElement, plElement.deltaBal);

		// Print R/E at end
		plElement.putAttribute("title", "R/E " + endDate);
		printAmountLine(plElement, plElement.beginBal + plElement.deltaBal);

		// TODO The total retained earnings (at end) must be manually added to
		// 'Total Liabilities & Retained Earnings', because the retained earnings
		// total was not available at the time the totals were computed
		// (by VLUtil.computeElementTotals).
		// Possibly, VLUtil.computeElementTotals could be modified to accomplish the
		// same thing, but this approach appears to be much more difficult & risky.
		//
		// Solution: (a) add tag="TLNW" to the total element titled
		// 'Total Liabilities & Retained Earnings' (b) add code here
		// use Chart.findTagElements("TLNW") to find the TLNW element and apply
		// deltaBal += (plElement.beginBal + plElement.deltaBal);
		// and (c) call printAmountLine(TNLWElement);
		TLNWelement = null;
		TLNWelement = chart.findTagElement("TLNW");
		if (TLNWelement == null)
			logger.logFatal("Cannot find TLNWelement");
		TLNWelement.deltaBal += (plElement.beginBal + plElement.deltaBal);
		logger.log("adjusted TLNWelement: " + TLNWelement.toString());
		printAmountLine(TLNWelement);
	}

	private String indent(ChartElement element)
	{
		int maxLevel = chart.getMaxLevel();
		int indentPerLevel = chart.getIndent();
		int level = element.level;
		if (element.name.equals("total"))
			--level;
		int x = (maxLevel - level) * indentPerLevel;
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
