package com.extant.vl2;

import java.io.IOException;
import java.io.File;

import com.extant.utilities.Strings;
import com.extant.utilities.XProperties;
//import com.sun.java.util.jar.pack.Package.File;

public class VL2Config
{
	public static String getAccountingDataDirectory()
	{
		String userName = System.getProperty("user.name");
		String pattern = "C:\\Users\\%s\\OneDrive\\ACCOUNTING\\";
		return String.format(pattern, userName);
	}

	private final String validEntities = "EXTANT, JMS, JMSCT, QUITO, XINV";
	private final String accountingDataDir;
	private final String entityName;
	String currentYear; // Set after user enters yy or confirms default
	private XProperties props;
	private String earliestDate;
	private String latestDate;
	private String nextGSN;
	private String printOrientation; // Defaults to 'portrait' can be set to 'landscape'
	public static final int portraitWidth = 84;
	public static final int landscapeWidth = 108;
	public static final long MAX_DOLLAR_AMOUNT = -999999999L; // $9,999,999.99
	public static final int MAX_AMOUNT_LENGTH = Strings.formatPennies(MAX_DOLLAR_AMOUNT).length();

	public VL2Config(String propFilename, String entityName) throws IOException
	{
		this.accountingDataDir = getAccountingDataDirectory();
		this.entityName = entityName;
		if (!(new File(this.accountingDataDir + "\\"+ entityName).exists()))
		{
			VL2.logger.logFatal(entityName + " is not a valid entity name.");
		}
		VL2.logger.logDebug("Properties:46" + "Reading properties file: " + propFilename);
		props = new XProperties(propFilename);
	}

	void setCurrentYear(String yy)
	{
		currentYear = yy;
		if (!new File(this.accountingDataDir + "\\" + entityName + "\\GL" + yy).exists())
			VL2.logger.logFatal(yy + " is not a valid year.");
	}

	public void listAllProperties()
	{
		System.out.println("AccountingDataDirectory=" + getAccountingDataDirectory());
		System.out.println("EntityName=" + getEntityName());
		System.out.println("EntityLongName=" + getEntityLongName());
		System.out.println("currentYear=" + getCurrentYear());
		System.out.println("ChartFile=" + getChartFile());
		System.out.println("CustomerList=" + getCustomerList());
		System.out.println("VendorList=" + getVendorList());
		System.out.println("ContactsList=" + getContactsList());
		System.out.println("GLFile=" + getGLFile());
		System.out.println("GSNFile=" + getGSNFile());
		System.out.println("CheckRegFile=" + getCheckRegFile());
		System.out.println("CashAcctNo=" + getCashAcctNo());
		System.out.println("WorkingDirectory=" + getWorkingDirectory());
		System.out.println("earliestDate=" + getEarliestDate());
		System.out.println("latestDate=" + getLatestDate());
		System.out.println("printOrientation=" + getPrintOrientation());
		System.out.println("portraitWidth=" + portraitWidth);
		System.out.println("landscapeWidth=" + landscapeWidth);
	}

	public String getEntityName()
	{
		return entityName;
	}

	public String getEntityLongName()
	{
		return props.getString("EntityLongName");
	}

	public String getCurrentYear()
	{
		return currentYear;
	}

	public String getChartFile()
	{
		return getWorkingDirectory() + props.getString("ChartFile");
	}

	public String getCustomerList()
	{
		return getWorkingDirectory() + props.getString("CustomerList");
	}

	public String getVendorList()
	{
		return getWorkingDirectory() + props.getString("VendorList");
	}

	public String getContactsList()
	{
		return getWorkingDirectory() + props.getString("ContactsList");
	}

	public String getGLFile()
	{
		return getWorkingDirectory() + props.getString("GLFile");
	}

	public String getGSNFile()
	{
		return getWorkingDirectory() + props.getString("GSNFile");
	}

	public String getCheckRegFile()
	{
		return getWorkingDirectory() + props.getString("CheckRegFile");
	}

	public String getCashAcctNo()
	{
		return props.getString("CashAcctNo");
	}

	public String getWorkingDirectory()
	{
		return accountingDataDir + entityName + "\\GL" + currentYear + "\\";
	}

	public String getEarliestDate()
	{
		return earliestDate;
	}

	public void setEarliestDate(String earliestDate)
	{
		this.earliestDate = earliestDate;
	}

	public String getLatestDate()
	{
		return latestDate;
	}

	public void setLatestDate(String latestDate)
	{
		this.latestDate = latestDate;
	}

	public void setNextGSN(String GSN)
	{
		this.setNextGSN(GSN);
	}

	public String getNextGSN()
	{
		return nextGSN;
	}

	public String setPrintOrientation(String orientation)
	{
		// orientation = 'protrait' or 'landscape'
		if (orientation.equalsIgnoreCase("portrait") || orientation.equalsIgnoreCase("landscape"))
		{
			printOrientation = orientation;
			VL2.logger.logDebug("printOrientation set to " + orientation);
			return orientation;
		}
		VL2.logger.log("setPrintOrientation to '" + orientation + "' failed.");
		return null;
	}

	public String getPrintOrientation()
	{
		return printOrientation;
	}

	public int getPaperWidth()
	{
		if (printOrientation.equalsIgnoreCase("portrait"))
			return portraitWidth;
		else
			return landscapeWidth;
	}

	public int getPortraitWidth()
	{
		return portraitWidth;
	}

	public int getLandscapeWidth()
	{
		return landscapeWidth;
	}
}
