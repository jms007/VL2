package com.extant.vl2;

import java.util.Vector;

import com.extant.utilities.LogFile;

public class Group
{
	Group chartGroup;
	Group currentSubGroup;
	Vector<Group> subGroups = new Vector<Group>(10, 10);
	int subGroupIndex = 0;
	String groupType; // chart,section,group
	int beginGroupElementIndex;
	int endGroupElementIndex;

	public Group buildChart(Chart chart, LogFile logger)
	{
		// For Debugging
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

		Vector<ChartElement2> chartElements = chart.chartElements;

		for (int i = 0; i < chart.chartElements.size(); ++i)
		{
			String elementName = chartElements.get(i).name;
			if (elementName.equalsIgnoreCase("chart"))
			{
				subGroups.add(new Group());
				subGroupIndex = 0;
				currentSubGroup = subGroups.get(subGroupIndex);
				subGroups.get(subGroupIndex).groupType = elementName;
				currentSubGroup.beginGroupElementIndex = i;
				currentSubGroup.endGroupElementIndex = -1;
				chartGroup = currentSubGroup;
			} else if (elementName.equalsIgnoreCase("section"))
			{
				Group subGroup = new Group();
				subGroupIndex++;
				subGroup.groupType = "section";
				subGroup.beginGroupElementIndex = i;
				subGroup.endGroupElementIndex = -1;
				subGroups.add(subGroup);
				currentSubGroup = subGroup;
			} else if (elementName.equalsIgnoreCase("group"))
			{
				Group subGroup = new Group();
				subGroupIndex++;
				subGroup.groupType = "group";
				subGroup.beginGroupElementIndex = i;
				subGroup.endGroupElementIndex = -1;
				subGroups.add(subGroup);
				currentSubGroup = subGroup;
			} else if (elementName.equalsIgnoreCase("account"))
			{
				; // currentSubGroup.subGroups.addElement(chartElements.get(i));
			} else if (elementName.equalsIgnoreCase("total"))
			{
				// currentSubGroup.subGroups.addElement(chartElements.get(i));
				currentSubGroup.endGroupElementIndex = i;
				subGroupIndex--;
				currentSubGroup = subGroups.get(subGroupIndex);
			}
		}
		if (chartGroup == null)
			logger.logFatal("chartGroup is null");
		return chartGroup;
	}

	public String snapChartGroup()
	{
		String report = "";
		report += "chartGroup: " + chartGroup.toString();

		return report;
	}

	// String toString()
	// {
	//
	// }
}
