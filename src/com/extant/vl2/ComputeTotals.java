/*
 * ComputeTotals.java
 *
 * Created on September 25, 2006, 1:03 AM
 */

package com.extant.vl2;

import java.io.IOException;

import com.extant.utilities.Julian;
import com.extant.utilities.LogFile;
import com.extant.utilities.Strings;

/**
 *
 * @author jms
 */
public class ComputeTotals extends AbstractStatement {
	public ComputeTotals(VL2Config vl2FileMan, Chart chart, Julian begin, Julian end, int reportLevel,
			String outfileName, LogFile logger) throws IOException, VLException
	{
		setup(vl2FileMan, chart, false, begin, end, reportLevel, outfileName, logger);
		makeStatement();
	}

	public void printAmountLine(ChartElement element, Account account)
	// throws VLException // NOT
	{
		if (element.name.equals("total")) {
			long amount = account.getEndBal();
			logger.logDebug("setting total " + element.getAttribute("no") + " = " + Strings.formatPennies(amount));
			element.setTotal(amount);
		}
	}

	public StmtTable initialize(int reportLevel, String outfileName) throws VLException // NOT
	{
		return null;
	}

	public void printTextLine(ChartElement element)
	{
	}

	public void close() throws VLException // NOT
	{
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args)
	{
	}

}
