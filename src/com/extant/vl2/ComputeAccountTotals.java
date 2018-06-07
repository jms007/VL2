package com.extant.vl2;

import java.io.File;
import java.io.IOException;
import com.extant.utilities.*;
//import java.util.Vector;

public class ComputeAccountTotals
{
	LogFile logger;
	String chartFilename;
	Chart chart;

	void initialize(Chart chart, VL2Config vl2Config)
	{
		// try
		// {
		// logger = new LogFile("ComputeAccountTotals.log");
		// logger.log("***** ComputeAccountTotals starting " + new Julian().toString() +
		// "*****");
		// // For debugging
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		//
		// // Initialize Chart
		// chartFilename = vl2Config.getChartFile();
		// logger.logDebug("Building Chart file: " + chartFilename);
		// if (!new File(chartFilename).exists())
		// logger.logFatal("Unable to find " + chartFilename);
		// chart = new Chart();
		// chart.init(chartFilename, logger); // throws IOException & VLException
		// } catch (IOException iox)
		// {
		// logger.logFatal("IO error checking chart: " + iox.getMessage());
		// } catch (VLException vlx)
		// {
		// logger.logFatal("VL Error checking chart: " + vlx.getMessage());
		// }
		//
		// logger.logInfo("Chart initialized without error");

		// Post transactions for this period to the chart
		try
		{
			GLEntry glEntry;
			Account account;
			UsefulFile GLFile = null;

			GLFile = new UsefulFile(vl2Config.getGLFile());
			long amount;
			while (!GLFile.EOF())
			{
				glEntry = new GLEntry(GLFile.readLine());
				account = chart.findAcctByNo(glEntry.getAccountNo());
				amount = glEntry.getSignedAmount();
				if (glEntry.getField("JREF").equals("BALF"))
					chart.addToBeginBal(amount);
				else
					chart.addToDeltaBal(amount);
			}
		} catch (IOException iox)
		{
			logger.logFatal(iox.getMessage());
		} catch (VLException vlx)
		{
			logger.logFatal(vlx.getMessage());
		}

		// Compute totals
		ChartElement[] elements = chart.elementList;
		long beginTotal;
		long deltaTotal;

		for (int i = 0; i < elements.length; ++i)
		{
			if (elements[i].name.equals("group"))
				beginTotal = deltaTotal = 0L;
			else if (elements[i].name.equals("account"))
				beginTotal += elements[i].beginBal;

		}
	}
}
