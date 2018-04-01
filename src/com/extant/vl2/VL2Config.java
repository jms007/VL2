package com.extant.vl2;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.extant.utilities.XProperties;

//EntityName=JMS
//EntityLongName=JAN M. SMITH
//CurrentYear=17
//EntityRoot=accountingRoot\\JMS\\
//WorkDir=GL17\\
//ChartFile=CHART.XML
//CustomerList=CUSTOMER.LST
//VendorList=VENDOR.LST
//ContactsList=CONTACTS.LST
//GLFile=GL0010.DAT
//CheckRegFile=GL0020.DAT
//GSNFile=gsnNVFile.txt
//CashAcctNo=0010

public class VL2Config
{
	public static String getAccountingDataDirectory()
	{
		String userName = System.getProperty("user.name");
		String pattern = "C:\\Users\\%s\\OneDrive\\ACCOUNTING\\";
		return String.format(pattern, userName);
	}

	private final String accountingDataDir;
	private final String entityName;
	private final String currentYear;

	// private final XProperties props;
	private XProperties props;

	private String earliestDate;
	private String latestDate;

	public VL2Config(String entityName, String currentYear) throws IOException
	{
		this.accountingDataDir = getAccountingDataDirectory();
		this.entityName = entityName;
		this.currentYear = currentYear;
		String propFile = accountingDataDir + entityName + "\\" + entityName + ".properties";
		try
		{
			VL2.logger.logDebug("Properties:46" + "Reading properties file: " + propFile);
			props = new XProperties(propFile);
		} catch (Exception x)
		{
			VL2.logger.logFatal("Unable to find " + entityName + "/" + currentYear);
		}
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

}
