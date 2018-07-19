package com.extant.vl2;

/*
 * ChartElement2.java
 *
 * These are the elements resulting from a parse of the XML chart.
 * All elements have a 'name', 'level', and an 'index'.
 * Elements which represent totals have total fields (begin & delta).
 * Other properties can be added or retrieved using the methods
 *  	putAttribute(name,value) or getAttribute(name)
 *
 * Created on September 10, 2006, 8:23 AM
 */

import com.extant.utilities.Strings;
import java.util.Properties;
import java.util.Enumeration;

/**
 *
 * @author jms
 */
public class ChartElement2
{
	public ChartElement2(String eName, int level, int index)
	{
		name = eName;
		this.level = level;
		this.index = index;
		props = new Properties();
	}

	public void putAttribute(String attrName, String attrValue)
	{
		if (attrName.equals("beginBal"))
			VL2.logger.whereAreWe(4, new Error());

		props.put(attrName, attrValue);
	}

	public String getAttribute(String attrName)
	{
		return props.getProperty(attrName, "");
	}

	public String getName()
	{
		return name;
	}

	public int getIndex()
	{
		return index;
	}

	public void setBeginBal(long bal) throws VLException
	{
		if (name.equals("total") || name.equals("account"))
			beginBal = bal;
		else
			throw new VLException(VLException.INCOMPATIBLE_ACCOUNT, "setTotal in " + name + " " + getAttribute("no"));
	}

	public void setDeltaBal(long bal) throws VLException
	{
		if (name.equals("total") || name.equals("account"))
			deltaBal = bal;
		else
			throw new VLException(VLException.INCOMPATIBLE_ACCOUNT, "setTotal in " + name + " " + getAttribute("no"));
	}

	// public long getTotal() throws VLException
	// {
	// if (name.equals("account") || name.equals("total"))
	// return props.getProperty("beginBal").beginBal + deltaBal;
	// else
	// throw new VLException(VLException.INCOMPATIBLE_ACCOUNT, "getTotal from " +
	// name + " " + getAttribute("no"));
	// }
	//
	public int getLevel()
	{
		return level;
	}

	public String getLevelString()
	{
		return Strings.format(level, "00");
	}

	// toString returns a String of the form:
	// name;level;accountIndex;key=value | key=value ...;
	public String toString()
	{
		String answer = name + ";" + Strings.format(level, "00") + ";" + Strings.format(index, "00") + ";" + "beginBal="
				+ Strings.formatPennies(beginBal) + ";" + "deltaBal=" + Strings.formatPennies(deltaBal);
		Enumeration en = props.propertyNames();
		boolean first = true;
		while (en.hasMoreElements())
		{
			String key = (String) en.nextElement();
			if (key.contains("bal"))
				continue; // the balances should not be in these props
			String value = props.getProperty(key);
			if (!first)
				answer += "|";
			answer += key + "=" + value;
			first = false;
		}
		return answer + ";";
	}

	public String name;
	Properties props;
	int level;
	int index;
	long beginBal;
	long deltaBal;
}
