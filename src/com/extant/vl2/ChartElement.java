package com.extant.vl2;

/*
 * ChartElement.java
 *
 * These are the elements resulting from a parse of the XML chart.
 * All elements have a 'name' and a 'level'.  Elements which
 * represent real accounts have an index to the Account in class Chart.
 * Elements which represent accounts or totals have total fields.
 * Other properties can be added using method putAttribute(name,value)
 *     and accessed using getAttribute(name)
 *
 * Created on September 10, 2006, 8:23 AM
 */

//import java.io.IOException;
//import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Enumeration;
import com.extant.utilities.*;

/**
 *
 * @author jms
 */
public class ChartElement
{
	public ChartElement(String eName, int level, int accountIndex)
	{
		name = eName;
		this.level = level;
		this.accountIndex = accountIndex;
		props = new Properties();
	}

	public void putAttribute(String attrName, String attrValue)
	{
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

	public int getAccountIndex() throws VLException
	{
		if (name.equals("account"))
			return accountIndex;
		else
			throw new VLException(VLException.INCOMPATIBLE_ACCOUNT,
					"getAccountIndex from " + name + " " + getAttribute("no"));
	}

	public void setTotal(long total)
	// throws VLException
	{
		if (name.equals("total"))
			this.total = total;
		// else throw new VLException
		// (VLException.INCOMPATIBLE_ACCOUNT, "setTotal in " + name + " " +
		// getAttribute( "no" ) );
	}

	public long getTotal() throws VLException
	{
		if (name.equals("total"))
			return total;
		else
			throw new VLException(VLException.INCOMPATIBLE_ACCOUNT, "getTotal from " + name + " " + getAttribute("no"));
	}

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
		String answer = name + ";" + Strings.format(level, "00") + ";" + Strings.format(accountIndex, "00") + ";";
		Enumeration en = props.propertyNames();
		boolean first = true;
		while (en.hasMoreElements())
		{
			String key = (String) en.nextElement();
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
	int accountIndex;
	long beginTotal;
	long deltaTotal;
	long total;
}
