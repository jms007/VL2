package com.extant.vl2;

import java.io.IOException;
import com.extant.utilities.LogFile;
import com.extant.utilities.Strings;
import com.extant.utilities.UsefulFile;

public class PrintChartTxt extends AbstractStatement
{
	String outputFilename;
	UsefulFile outFile;
	Chart chart;
	ChartElement2 element;
	String image;
	int maxLineLength = 84; // Portrait
	boolean showAmount = false;
	boolean showAccount = true;
	int maxAccountNoLength;
	LogFile logger;

	public StmtTable initialize(int reportLevel, String outputFilename)
	{
		try
		{
			this.outputFilename = outputFilename;
			outFile = new UsefulFile(outputFilename, "w");
			maxAccountNoLength = chart.maxAccountNoLength;
			return null;
		} catch (IOException iox)
		{
			logger.log("Unable to create " + outputFilename);
		}
		return null;
	}

	public void printTextLine(ChartElement2 element) throws VLException
	{
		image = "";
		image += Strings.leftJustify("", maxAccountNoLength);
		image += element.getAttribute("title");
		printImage();
	}

	public void printAmountLine(ChartElement2 element) throws VLException
	{
		image = "";
		if (element.name.equalsIgnoreCase("account"))
			image += Strings.leftJustify(element.getAttribute("no"), maxAccountNoLength);
		else
			image += Strings.leftJustify("", maxAccountNoLength);
		image += element.getAttribute("title");
		printImage();
	}

	public void close()
	{
		outfile.close();
	}

	public void printRetainedEarnings(ChartElement2 element)
	{ // Not used
	}

	// public void run()
	// {
	// // For debugging:
	// // logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
	//
	// ChartElement2 element;
	// Vector<ChartElement2> chartElements = chart.chartElements;
	// for (int i = 0; i < chart.chartElements.size(); ++i)
	// {
	// element = (ChartElement2) chartElements.get(i);
	// logger.logDebug("ELEMENT[" + nElements + "]" + element.toString());
	// processChartElement(element);
	// }
	// outFile.close();
	// logger.logInfo("Chart complete, " + nElements + " elements processed.");
	// }
	//
	// public void processChartElement(ChartElement2 element)
	// {
	// if (element.name.equals("chart"))
	// processChart(element);
	// else if (element.name.equals("section"))
	// processSection(element);
	// else if (element.name.equals("group"))
	// processGroup(element);
	// else if (element.name.equals("account"))
	// processAccount(element);
	// else if (element.name.equals("total"))
	// processTotal(element);
	//
	// return;
	// }
	//
	private void printImage()
	{
		// processFormat(element);
		// image = "";
		// for (int i = 1; i <= 3; ++i)
		// {
		// logger.logDebug("col" + i + "=" + column[i]);
		// image += column[i];
		// }
		logger.logDebug("printImage: '" + image + "'");
		outFile.appendLine(image);
		// resetColumns();
	}

	// private boolean processFormat(ChartElement2 element)
	// {
	// boolean print;
	// logger.logDebug("image='" + image + "'");
	// String format = element.getAttribute("format");
	// // if (format.contains("newpage"))
	// // image = "\f" + image;
	// if (format.contains("skip"))
	// {
	// outFile.appendLine("\n");
	// }
	// // if (format.contains("begin"))
	// // image += earliestDate.toString(chart.getDateFormat());
	// // if (format.contains("period"))
	// // image += earliestDate.toString(chart.getDateFormat()) + " - " +
	// // latestDate.toString(chart.getDateFormat());
	// if (format.contains("center"))
	// {
	// String temp = image.trim();
	// temp = Strings.center(temp, maxLineLength);
	// logger.logDebug("center: " + temp);
	// image = temp;
	// outFile.appendLine(image);
	// print = false;
	// }
	// print = true;
	// return print;
	// }
	//
	// private void setColumns()
	// {
	// // blanks = "";
	// // for (int i = 0; i < 100; ++i)
	// // blanks += " ";
	// //
	// // Col 1 - Account No
	// column[1] = Strings.leftJustify("", chart.getMaxAcctNoLength());
	// colWidth[1] = column[1].length();
	// logger.logDebug("colWidth[1]=" + colWidth[1]);
	//
	// // col 2 - Group Title
	// column[2] = blanks.substring(0, chart.getMaxGroupTitle().length());
	// colWidth[2] = column[2].length();
	// logger.logDebug("colWidth[2]=" + colWidth[2]);
	// // begin[2] = end[1] + 1;
	// // end[2] = begin[2] + chart.getMaxGroupTitle().length();
	// // colWidth[2] = end[2] - begin[2] + 1;
	//
	// // col3 - Account Title
	// column[3] = blanks.substring(0, chart.getMaxAccountTitleLength());
	// colWidth[3] = column[3].length();
	// logger.logDebug("colWidth[3]=" + colWidth[3]);
	// // begin[3] = end[2] + 1;
	// // end[3] = begin[3] + chart.getMaxAccountTitleLength();
	// // colWidth[3] = end[3] - begin[3] + 1;
	//
	// maxLineLength = colWidth[1] + colWidth[2] + colWidth[3];
	// logger.logDebug("maxLineLength=" + maxLineLength);
	// blanks = blanks.substring(0, maxLineLength);
	// }
	//
	// // private void resetColumns()
	// // {
	// // for (int i = 1; i < 3; ++i)
	// // column[i] = blanks.substring(0, colWidth[i]);
	// // }
	// //
	// private void processChart(ChartElement2 element)
	// { // center
	// logger.logDebug("processChart:" + element.toString());
	// image = "CHART OF ACCOUNTS";
	// printImage();
	// String title = element.getAttribute("title");
	// if (title == null)
	// return;
	// if (title.length() > 0)
	// {
	// image = title;
	// printImage();
	// }
	// }
	//
	// private void processSection(ChartElement2 element)
	// { // center
	// logger.logDebug("processSection:" + element.toString());
	// image = element.getAttribute("title");
	// printImage();
	// }
	//
	// private void processGroup(ChartElement2 element)
	// { // col2
	// logger.log("Processing group element!");
	// logger.logDebug("processGroup:" + element.toString());
	// String s = element.getAttribute("title");
	// putInCol(2, Strings.leftJustify(s, colWidth[2]));
	// printImage();
	// }
	//
	// private void processAccount(ChartElement2 element)
	// { // no -> col1 title ->col2
	// logger.logDebug("processAccount:" + element.toString());
	// String s = element.getAttribute("no");
	// putInCol(1, Strings.leftJustify(s, colWidth[1]));
	// s = element.getAttribute("title");
	// putInCol(2, Strings.leftJustify(s, colWidth[2]));
	// printImage();
	// }
	//
	// private void processTotal(ChartElement2 element)
	// { // totals do not appear in chart
	// logger.logDebug("processTotal:" + element.toString());
	// }
	//
	// private void putInCol(int col, String s)
	// {
	// logger.logDebug("putting '" + s + "' in col " + col);
	// column[col] = s + blanks.substring(0, (colWidth[col] - s.length()));
	// logger.logDebug("column[" + col + "]=" + column[col] + "length=" +
	// column[col].length());
	// }
}
