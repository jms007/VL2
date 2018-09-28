package com.extant.vl2;

import java.io.IOException;
import com.extant.utilities.Strings;
import com.extant.utilities.UsefulFile;

public class PrintChartTxt extends AbstractStatement
{
	String outputFilename;
	ChartElement element;
	String image;
	int maxLineLength = 84; // Portrait
	boolean showAmount = false;
	boolean showAccount = true;

	public void initialize()
	{
		try
		{
			// logger = VL2.logger;
			// if (logger == null)
			// System.out.println("logger is null!");
			reportLevel = maxLevel;
			outputFilename = vl2Config.getWorkingDirectory() + "Chart.txt";
			outfile = new UsefulFile(outputFilename, "w");
			logger.log("outputFilename=" + outputFilename);
		} catch (IOException iox)
		{
			logger.log("Unable to create " + outputFilename);
		}
	}

	public void printTextLine(ChartElement element) throws VLException
	{
		image = "";
		if (element.name.equals("chart"))
		{
			image = Strings.center(element.getAttribute("title"), maxLineLength);
			printImage();
			image = Strings.center("CHART OF ACCOUNTS", maxLineLength);
			printImage();
			image = "\n";
			printImage();
		} else if (element.name.equals("section"))
		{
			image = Strings.center(element.getAttribute("title"), maxLineLength);

		} else if (element.name.equals("group"))
		{
			image = Strings.leftJustify("", maxAccountNoLength);
			image += StatementTXT.indent(element);
			image += element.getAttribute("title");
			printImage();
		}
		// image = "";
		// image += Strings.leftJustify("", maxAccountNoLength);
		// image += StatementTXT.indent(element);
		// image += element.getAttribute("title");
		// printImage();
	}

	public void printAmountLine(ChartElement element)
	{
		image = "";
		image += Strings.leftJustify(element.getAttribute("no"), maxAccountNoLength);
		image += (StatementTXT.indent(element) + element.getAttribute("title"));
		printImage();
	}

	public void printRetainedEarnings(ChartElement element)
	{
		printAmountLine(element);
	}

	private void printImage()
	{
		logger.logDebug("printImage: '" + image + "'");
		outfile.appendLine(image);
	}
}
