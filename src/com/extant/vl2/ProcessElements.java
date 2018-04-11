package com.extant.vl2;

import java.util.Enumeration;
import com.extant.utilities.LogFile;

public abstract class ProcessElements
{
	public ProcessElements()
	{
	}

	public void processElements(Chart chart)
	{
		int nElements;

		Enumeration<ChartElement> chartElements = chart.chartElements();
		nElements = 0;
		while (chartElements.hasMoreElements())
		{
			ChartElement element = (ChartElement) chartElements.nextElement();
			++nElements;
			logger.logDebug("ELEMENT[" + nElements + "]" + element.toString());
			processElement(element);
		}
		logger.logDebug("nElements=" + nElements);
	}

	abstract void processElement(ChartElement element);

	Chart chart;
	LogFile logger = VL2.logger;
}
