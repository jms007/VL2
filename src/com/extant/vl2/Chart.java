/*
 * Chart.java
 * The Chart class built from the XML description of the Chart of Accounts
 *
 * Created on September 4, 2006, 8:34 PM
 * 10-19-06 Added errors for (a) no P/L account and (b) multiple P/L accounts
 * 07-13-18 Removed ChartElement[] elementList, replaced by <Vector> chartElements
 */
package com.extant.vl2;

import com.extant.utilities.Sorts;
import com.extant.utilities.Julian;
import com.extant.utilities.Strings;
import com.extant.utilities.LogFile;
import java.util.Vector;
import java.util.StringTokenizer;

import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import java.io.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

//import javax.xml.namespace.NamespaceContext;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.xpath.XPath;
//import javax.xml.xpath.XPathConstants;
//import javax.xml.xpath.XPathExpressionException;
//import javax.xml.xpath.XPathFactory;

/**
 *
 * @author jms
 */
public class Chart extends DefaultHandler // implements Enumeration<String>
{
	public Chart()
	{
	}

	/*
	 * The init method creates a Chart with all accounts as specified in the given
	 * chart.xml input file and specifies a format for financial statements and
	 * Chart of Accounts listings. No 'amount' information is calculated.
	 */
	public void init(String chartFilename, LogFile logger) throws IOException, VLException
	{
		this.logger = logger;
		try
		{
			// For Debugging
			// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

			logger.logDebug("Chart.init: chartFilename=" + chartFilename);
			nErrors = 0;
			errorReport = "";
			if (!Strings.fileSpec("Ext", chartFilename).equalsIgnoreCase("XML"))
				throw new IOException("Input file is not of type XML");
			this.chartFilename = chartFilename;
			accounts = new Vector<Account>(500, 100);
			chartElements = new Vector<ChartElement>(500, 100);
			elementCount = 0;
			level = 0;
			maxLevel = 0;

			// To learn about Document and XPATH see:
			// https://www.tutorialspoint.com/java_xml/java_xpath_query_document.htm/

			// Use the validating parser
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(true);

			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new File(chartFilename), this);

			Vector<String> acctNumbers = buildAcctsByNumber();
			acctsByNumberP = Sorts.sort(acctNumbers);
			if (Sorts.hasDupKeys(acctNumbers))
			{
				reportDupAccounts(accounts, acctsByNumberP);
				throw new VLException(VLException.DUP_ACCOUNTS, "");
			}
			Vector<String> descrs = buildAcctsByDescr();
			acctsByDescrP = Sorts.sort(descrs);

			// Check for account titles beginning with a digit
			boolean titleError = false;
			for (int i = 0; i < acctsByDescrP.length; ++i)
			{
				account = (Account) accounts.elementAt(i);
				String title = account.getTitle();
				if (Strings.isNumeric(title.charAt(0)))
				{
					reportError("[" + account.getAccountNo() + "] '" + title
							+ "'   Account Title cannot start with a number.");
					titleError = true;
				}
			}
			if (titleError)
				throw new VLException(VLException.CHART_FORMAT_ERROR, "");

			// Verify there is a defined P&L account
			if (plAccount == null)
			{
				reportError("No P&L Account Defined in this chart");
				throw new VLException(VLException.CHART_FORMAT_ERROR, "No P/L Account");
			}
			// acctNumbers = null; // basura
			// descrs = null; // basura
			return;
		} catch (ParserConfigurationException pcx)
			{
			reportError(pcx.getMessage());
			}
			catch (SAXException xl)
			{
			reportError(xl.getMessage());
			logger.logFatal(xl.getMessage());
			throw new IOException(xl.getMessage());
			}
	}

	public void startElement(String namespaceURI, String sName, // simple name (localName)
			String qName, // qualified name
			Attributes attrs) throws SAXException
	{ // Set element name
		String eName = sName; // element name = localName
		if (eName.equals(""))
			eName = qName; // element name = qualified name (namespaceAware = false)

		// For Debugging
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);

		// Create this chart element and its attributes
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		ChartElement chartElement = new ChartElement(eName, level, elementCount);
		logger.logDebug("start element[" + elementCount + "]: " + eName);
		for (int i = 0; i < attrs.getLength(); ++i)
		{
			attrs.getLocalName(i);
			chartElement.putAttribute(attrs.getLocalName(i), attrs.getValue(i));
			logger.logDebug("   " + attrs.getLocalName(i) + "=" + attrs.getValue(i));
		}

		// Now process the individual element types
		// Note that chart & section titles are always centered, so their max values
		// are not needed.
		if (eName.equals("chart"))
		{ // START CHART
			// Set default values
			indent = 4;
			dollarFormat = "(,";
			longDateFormat = "mmmm dd, yyyy";
			shortDateFormat = "mm-dd-yy";

			conum = attrs.getValue("conum");
			coname = attrs.getValue("title");
			indent = Integer.parseInt(attrs.getValue("indent"));
			if (attrs.getValue("indent") != null)
				indent = new Integer(attrs.getValue("indent"));
			if (attrs.getValue("dollarFormat") != null)
				dollarFormat = attrs.getValue("dollarFormat");
			if (attrs.getValue("longDateFormat") != null)
				longDateFormat = attrs.getValue("longDateFormat");
			if (attrs.getValue("shortDateFormat") != null)
				shortDateFormat = attrs.getValue("shortDate");
			logger.logDebug("Chart Element: longDateFormat=" + longDateFormat);
			logger.logDebug("Chart Element: shortDateFormat=" + shortDateFormat);
			chartVersion = attrs.getValue("version");
			++level;
			logger.logDebug("   incrementing level to " + level);
		} else if (eName.equals("section"))
		{ // START SECTION
			++level;
			logger.logDebug("   Chart Element: incrementing level to " + level);

		} else if (eName.equals("group"))
		{ // START GROUP
			String title = attrs.getValue("title");
			// maxTitleCalc (group)
			if (title.length() > maxTitleLength)
			{
				maxTitle = title;
				maxTitleLength = title.length();
			}
			++level;
			logger.logDebug("   Group Element: incrementing level to " + level);

		} else if (eName.equals("account"))
		{ // START ACCOUNT
			logger.logDebug("Creating Account: " + attrs.getValue("no") + " '" + attrs.getValue("title") + "'");
			// if (findAcctByNo(attrs.getValue("no")) != null)
			// ; !! This is a duplicate account number, but
			// we can't throw an exception here
			account = new Account(attrs.getValue("no"), Strings.format(level, "00"), attrs.getValue("type"),
					attrs.getValue("title"), elementCount);
			// TODO 'type' should be renamed 'class'
			if (account == null)
				System.out.println("chart.200 account is null!");
			accounts.addElement(account);
			// if (account.accountType.equals("R"))
			// {
			// plAccount = account;
			// logger.logDebug("plAccount=" + plAccount.toString());
			// }
			String accountNo = account.accountNo;
			// maxAccountNoCalc (account)
			if (accountNo.length() > maxAccountNoLength)
			{
				maxAccountNo = accountNo;
				maxAccountNoLength = maxAccountNo.length();
			}
			String title = attrs.getValue("title");
			// maxTitleCalc (account)
			if (title.length() > maxTitleLength)
			{
				maxTitle = title;
				maxTitleLength = maxTitle.length();
			}
			if (level > maxLevel)
				maxLevel = level;

			if (attrs.getValue("type").equals("R"))
			{
				if (plAccount != null)
				{
					reportError("Multiple P/L Accounts are defined: " + plAccount.accountNo + " " + account.accountNo);
					return;
				} else
				{
					plAccount = account;
					plElement = chartElement;
				}
			}

		} else if (eName.equals("total"))
		{
			String title = attrs.getValue("title");
			if (title.length() > maxTitleLength)
			{
				maxTitle = title;
				maxTitleLength = title.length();
			}

		} else
		{ // UNEXPECTED ELEMENT NAME - IGNORE
			logger.log("unexpected element name: " + eName);
			return;
		}

		// Add this element to the element list
		logger.logDebug("Chart: Adding to chartElements: " + chartElement.toString());
		chartElements.addElement(chartElement);
		++elementCount;
	}

	public void endElement(String namespaceURI, String sName // simple name
			, String qName // qualified name
	)
	{
		if (qName.trim().equals("group"))
		{ // END GROUP
			--level;
			logger.logDebug("   decrementing level to " + level);
		} else if (qName.trim().equals("section"))
		{ // END SECTION
			--level;
			logger.logDebug("   decrementing level to " + level);
		} else if (qName.trim().equals("chart"))
		{ // END CHART
			logger.log("Chart parsing is complete.");
			// Show the last element (if debug)
			logger.logDebug("lastElement: [" + chartElements.size() + "]=" + chartElements.lastElement().toString());
			// Copy all ChartElement's from chartElements to elementList
			if (chartElements == null)
				logger.logFatal("Chart:249: chartElements is null!");
			--level;
			logger.logDebug("   decrementing level to " + level);
			logger.logDebug(chartElements.size() + " ChartElements created");
			if (level != 0)
				logger.logFatal("Chart: Final Level is " + level);
			nAccounts = accounts.size();
		}
	}

	public Vector<ChartElement> getChartElements()
	{
		return chartElements;
	}

	// findAcctByNo will locate an account given either
	// <account> or <account>/<subAccount>, or
	// its readable form: [<account>/<subAccount>]Description
	// if no matching account is found, returns null
	public Account findAcctByNo(String acctNo)
	{
		if (acctNo == null)
			return null;
		String test = acctNo;
		if (test.startsWith("["))
		{
			StringTokenizer st = new StringTokenizer(test, "[]");
			test = (String) st.nextElement();
		}
		for (int i = 0; i < accounts.size(); ++i)
		{
			if (((Account) accounts.elementAt(i)).getAccountNo().equals(test))
				return (Account) accounts.elementAt(i);
		}
		return null;
	}

	public boolean isValidAccount(String accountNo)
	{
		return findAcctByNo(accountNo) != null;
	}

	public Account getAccount(int index)
	{
		if (index < 0 || index >= accounts.size())
			return null;
		return (Account) accounts.elementAt(index);
	}

	public String match(String partial)
	{
		logger.log("Chart.match: nAccounts=" + nAccounts);
		// !! This won't correctly locate an account for which the description begins
		// with a decimal digit ( like '429 Burnett' ).
		String trial;
		int p;
		boolean lookForNumber = Strings.isDecimalDigit(partial.charAt(0));
		// Console.println( "[Chart.match] partial='" + partial + "' lookForNumber=" +
		// lookForNumber );
		int i;
		for (i = 0; i < nAccounts; ++i)
		{
			if (lookForNumber)
				trial = ((Account) accounts.elementAt(acctsByNumberP[i])).getAccountNo();
			else
				trial = ((Account) accounts.elementAt(acctsByDescrP[i])).getTitle();
			if (trial.toLowerCase().startsWith(partial.toLowerCase()))
				break;
		}
		// Console.println( " i=" + i + " fail=" + (i>=nAccounts) );
		if (i < nAccounts)
		{
			if (lookForNumber)
				p = acctsByNumberP[i];
			else
				p = acctsByDescrP[i];
			// Console.println( " returning " + ((Account)accounts.elementAt( p
			// )).toString() );
			return ((Account) accounts.elementAt(p)).toString();
		}
		// Console.println( " returning null" );
		return null;
	}

	public static boolean match(String acctEntry, GLEntry glEntry)
	{
		return match(acctEntry, glEntry, null, null);
	}

	public static boolean match(String normalizedAccountNo, GLEntry glEntry, Julian cutoff)
	{
		return match(normalizedAccountNo, glEntry, null, cutoff);
	}

	public static boolean match(String accountNo, GLEntry glEntry, Julian startDate, Julian endDate)
	{
		boolean match;
		// StringTokenizer st = new StringTokenizer( normalizedAccountNo, "[]" );
		// String chartAcct = (String)st.nextElement();
		String chartAcct = accountNo;
		String entryAcct = Strings.trim(glEntry.getField("account"), " ");
		// String entrySubacct = Strings.trim( glEntry.getField( "subAccount" ), " " );
		// if ( !entrySubacct.equals( "" ) ) entryAcct += "/" + entrySubacct;
		match = chartAcct.equals(entryAcct);
		if (startDate != null)
			match &= glEntry.getJulianDate().getDayNumber() >= startDate.getDayNumber();
		if (endDate != null)
			match &= glEntry.getJulianDate().getDayNumber() <= endDate.getDayNumber();
		return match;
	}

	public void clearElementBalances()
	{
		for (int i = 0; i < chartElements.size(); ++i)
		{
			ChartElement element = (ChartElement) chartElements.elementAt(i);
			element.beginBal = 0L;
			element.deltaBal = 0L;
		}
	}

	// public void removeGLEntries()
	// {
	// for (int i = 0; i < accounts.size(); ++i)
	// {
	// Account account = (Account) accounts.elementAt(i);
	// account.removeGLEntries();
	// }
	// }
	//
	private Vector<String> buildAcctsByNumber()
	{
		Vector<String> numbers = new Vector<String>(accounts.size());
		String accountNo;

		for (int i = 0; i < accounts.size(); ++i)
		{
			accountNo = accounts.elementAt(i).getAccountNo();
			numbers.addElement(accountNo);
		}
		return numbers;
	}

	private Vector<String> buildAcctsByDescr()
	{
		Vector<String> descrs = new Vector<String>(accounts.size());
		for (int i = 0; i < accounts.size(); ++i)
			descrs.addElement(((Account) accounts.elementAt(i)).getTitle());
		return descrs;
	}

	public Account getPLAccount()
	{
		return plAccount;
	}

	public ChartElement getPLElement()
	{
		return plElement;
	}

	public String getVersion()
	{
		return chartVersion;
	}

	public String getConum()
	{
		return conum;
	}

	public String getConame()
	{
		return coname;
	}

	public int getIndent()
	{
		return indent;
	}

	public String getIndention(int level)
	{
		return Strings.leftJustify(" ", level * indent);
	}

	public String getDollarFormat()
	{
		return dollarFormat;
	}

	public String getLongDateFormat()
	{
		return longDateFormat;
	}

	public String getShortDateFormat()
	{
		return shortDateFormat;
	}

	public Vector<Account> getAccounts()
	{
		return accounts;
	}

	public int getNAccounts()
	{
		return nAccounts;
	}

	// max get methods
	public String getMaxDescr()
	{
		return maxTitle;
	}

	public String getMaxTitle()
	{
		return maxTitle;
	}

	public int getMaxDescrLength()
	{
		return maxTitleLength;
	}

	public int getMaxTitleLength()
	{
		return maxTitleLength;
	}

	public int getMaxLevel()
	{
		return maxLevel;
	}

	public String getMaxAcctNo()
	{
		return maxAccountNo;
	}

	public int getMaxAccountNoLength()
	{
		return maxAccountNoLength;
	}

	public String getFileName()
	{
		return chartFilename;
	}

	public int getElementCount()
	{
		return elementCount;
	}

	// public Enumeration acctsByChart()
	// {
	// enumP = acctsByChartP;
	// enumerationIndex = 0;
	// enumeratingAccounts = true;
	// enumeratingElements = false;
	// return (Enumeration) this;
	// }
	//
	// public Enumeration <Account> acctsByNumber()
	// {
	// enumP = acctsByNumberP;
	// enumerationIndex = 0;
	// enumeratingAccounts = true;
	// enumeratingElements = false;
	// return (Enumeration <Account>) acctsByNumber.elementAt;
	// }
	//
	// public Enumeration acctsByDescr()
	// {
	// enumP = acctsByDescrP;
	// enumerationIndex = 0;
	// enumeratingAccounts = true;
	// enumeratingElements = false;
	// return (Enumeration) this;
	// }
	//
	// public Enumeration chartElements()
	// {
	// enumP = allElementsP;
	// enumerationIndex = 0;
	// enumeratingElements = true;
	// enumeratingAccounts = false;
	// return (Enumeration) this;
	// }
	//
	public boolean hasMoreElements()
	{
		return enumerationIndex < enumP.length;
	}

	public Account nextAccountElement()
	{
		if (enumerationIndex >= enumP.length)
			return null;
		return accounts.elementAt(enumP[enumerationIndex++]);
	}

	// Find the element that has 'tag' attributes which contain the substring tag
	// (an inefficient but simple xPath, assumes tags are unique)
	public ChartElement findTagElement(String tag)
	{
		ChartElement element;
		ChartElement answer = null;
		String tagValue;
		logger.logDebug("searching for tag '" + tag + "'");
		for (int i = 0; i < chartElements.size(); ++i)
		{
			element = chartElements.elementAt(i);
			tagValue = element.getAttribute("tag");
			logger.logDebug("element[" + i + "] tagValue=" + tagValue);
			if (tagValue != null)
			{
				if (tagValue.contains(tag))
				{
					answer = element;
					logger.logDebug("findTagElements found " + element.toString());
					break;
				}
			}
		}
		return answer;
	}

	private void reportDupAccounts(Vector<Account> accounts, int p[])
	{
		for (int i = 0; i < accounts.size() - 1; ++i)
		{
			if (((Account) accounts.elementAt(i)).getAccountNo()
					.equals(((Account) accounts.elementAt(i + 1)).getAccountNo()))
			{
				logger.log("Duplicate account: " + ((Account) accounts.elementAt(i)).getAccountNo() + "\n   "
						+ ((Account) accounts.elementAt(i)).getTitle() + "\n   "
						+ ((Account) accounts.elementAt(i + 1)).getTitle());
				reportError("Duplicate Account: " + ((Account) accounts.elementAt(i)).getAccountNo());
			}
		}
	}

	private void reportError(String msg)
	{
		++nErrors;
		errorReport += msg + "\n";
	}

	public int getNErrors()
	{
		return nErrors;
	}

	public String getErrorReport()
	{
		return "No. of errors = " + nErrors + "\n" + errorReport;
	}

	String chartFilename;
	LogFile logger;
	int nErrors;
	String errorReport = "";
	int level;
	Vector<ChartElement> chartElements; // Replaces ChartElement[]
	private int elementCount;
	Vector<Account> accounts; // All accounts in chart order
	public int nAccounts;
	public int acctsByNumberP[]; // index of accounts in account number order
	public int acctsByDescrP[]; // index of accounts in descr (title) order
	private int enumP[];
	private String conum;
	private String coname;
	private int indent;
	private String dollarFormat = ",)"; // Default
	private String longDateFormat;
	private String shortDateFormat;
	private String chartVersion = "3.2"; // Default
	private Account account;
	public Account plAccount = null;
	public ChartElement plElement;
	private int enumerationIndex;
	int maxLevel = 0;

	String maxAccountNo = "";
	int maxAccountNoLength = 0;
	String maxTitle = "";
	int maxTitleLength = 0;

	long beginBal = 0L;
	long deltaBal = 0L;
}
