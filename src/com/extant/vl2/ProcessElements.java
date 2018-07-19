package com.extant.vl2;

//import java.util.Enumeration;
import com.extant.utilities.LogFile;

public abstract class ProcessElements
{
	public ProcessElements()
	{
	}

	public void processElements(Chart chart)
	{
		// Enumeration<ChartElement> chartElements = chart.chartElements();
		// nElements = 0;
		// while (chartElements.hasMoreElements())
		ChartElement2 element;
		for (int i = 0; i < chart.chartElements.size(); ++i)
		{
			element = chart.chartElements.elementAt(i);
			logger.logDebug("ELEMENT[" + i + "]" + element.toString());
			processElement(element);
		}
	}

	abstract void processElement(ChartElement2 element);

	Chart chart;
	LogFile logger = VL2.logger;
}
