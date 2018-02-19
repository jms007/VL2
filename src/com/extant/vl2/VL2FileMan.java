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

public class VL2FileMan {

	public static String getAccountingDataDirectory()
	{
		String userName = System.getProperty("user.name");
		String pattern = "C:\\Users\\%s\\OneDrive\\ACCOUNTING\\";
		return String.format(pattern, userName);
	}

	private final String accountingDataDir;
	private final String entityName;
	private final String currentYear;

	private final XProperties props;

	private String earliestDate;
	private String latestDate;

	public VL2FileMan(String entityName, String currentYear) throws IOException
	{
		this.accountingDataDir = getAccountingDataDirectory();
		this.entityName = entityName;
		this.currentYear = currentYear;
		String propFile = accountingDataDir + entityName + "\\" + entityName + ".properties";
		VL2.logger.log("Properties:45" + "Reading properties file: " + propFile);
		props = new XProperties(propFile);
	}

	public void listAllProperties()
	{
		// TODO
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
