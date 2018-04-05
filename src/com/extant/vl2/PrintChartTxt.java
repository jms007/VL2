package com.extant.vl2;

import java.util.Enumeration;
import java.io.IOException;
import com.extant.utilities.Julian;
import com.extant.utilities.LogFile;
import com.extant.utilities.Strings;
import com.extant.utilities.UsefulFile;

public class PrintChartTxt
{
	String outputFilename;
	UsefulFile outFile;
	Chart chart;
	Julian earliestDate;
	Julian latestDate;
	int indent;
	int nElements = 0;
	int maxAcctNoLength;
	ChartElement element;
	String image;
	int pageWidth = 80;
	LogFile logger;

	public void initialize(String outputFilename, Chart chart, Julian earliestDate, Julian latestDate, LogFile logger)
			throws IOException // (Creating outFile failed)
	{
		this.outputFilename = outputFilename;
		outFile = new UsefulFile(outputFilename, "w");
		this.chart = chart;
		this.earliestDate = earliestDate;
		this.latestDate = latestDate;
		this.logger = logger;
		indent = chart.getIndent();
		maxAcctNoLength = chart.getMaxAcctNoLength();
		// For debugging:
		logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		run();
	}

	public void run()
	{
		Enumeration<ChartElement> chartElements = chart.chartElements();
		while (chartElements.hasMoreElements())
		{
			element = (ChartElement) chartElements.nextElement();
			++nElements;
			logger.logDebug("ELEMENT[" + nElements + "]" + element.toString());
			processChartElement(element);
		}
		outFile.close();
		logger.logInfo("Chart complete, " + nElements + " elements processed.");
	}

	public void processChartElement(ChartElement element)
	{
		if (element.name.equals("chart"))
			processChart(element);
		else if (element.name.equals("section"))
			processSection(element);
		else if (element.name.equals("group"))
			processGroup(element);
		else if (element.name.equals("account"))
			processAccount(element);
		else if (element.name.equals("total"))
			processTotal(element);

		processFormat(element);
		printImage();
		return;
	}

	private void printImage()
	{
		logger.logDebug("printImage: " + image);
		processFormat(element);
		outFile.appendLine(image);
		image = "";
	}

	private void processFormat(ChartElement element)
	{
		String format = element.getAttribute("format");
		if (format.contains("newpage"))
			image += "\f";
		if (format.contains("skip"))
			image += "\n";
		if (format.contains("begin"))
			image += earliestDate.toString(chart.getDateFormat());
		if (format.contains("period"))
			image += earliestDate.toString(chart.getDateFormat()) + " - " + latestDate.toString(chart.getDateFormat());
		if (format.contains("center"))
			image = Strings.center(image, pageWidth);
	}

	private String calcIndention()
	{
		String indention = "";
		for (int i = 0; i < element.level; ++i)
			indention += "          ".substring(0, indent);
		return indention;
	}

	private void processChart(ChartElement element)
	{
		logger.logDebug("processChart:" + element.toString());
		image = "CHART OF ACCOUNTS";
		printImage();
		String title = element.getAttribute("title");
		if (title.length() > 0)
		{
			image = title;
			printImage();
		}
	}

	private void processSection(ChartElement element)
	{
		logger.logDebug("processSection:" + element.toString());
		image = calcIndention() + element.getAttribute("title");
		printImage();
	}

	private void processGroup(ChartElement element)
	{
		logger.logDebug("processGroup:" + element.toString());
		image = calcIndention() + element.getAttribute("title");
		printImage();
	}

	private void processAccount(ChartElement element)
	{
		logger.logDebug("processAccount:" + element.toString());
		String entry = calcIndention() + element.getAttribute("title");
		image = Strings.leftJustify(element.getAttribute("no"), maxAcctNoLength);
		image += entry;
	}

	private void processTotal(ChartElement element)
	{
		logger.logDebug("processTotal:" + element.toString());
	}
}
