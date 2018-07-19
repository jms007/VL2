/*
 * Chart.java
 * The Chart class built from the XML description of the Chart of Accounts
 *
 * Created on September 4, 2006, 8:34 PM
 * 10-19-06 Added errors for (a) no P/L account and (b) multiple P/L accounts
 * 07-13-18 Removed ChartElement2[] elementList, replaced by <Vector> chartElements
 */
package com.extant.vl2;

//import org.xml.sax.*;
import com.extant.utilities.Sorts;
import com.extant.utilities.Julian;
import com.extant.utilities.Strings;
import com.extant.utilities.LogFile;
//import com.extant.utilities.Clip;
//import com.extant.utilities.UtilitiesException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import java.io.*;
import java.util.Vector;
//import java.util.Enumeration;
import java.util.StringTokenizer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

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
	 * init method creates a Chart with all accounts as specified in the given
	 * chart.xml input file. No 'amount' information is calculated.
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
			chartElements = new Vector<ChartElement2>(500, 100);
			maxLevel = 0;
			elementCount = 0;
			level = 0;

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
			acctsByChartP = new int[nAccounts];
			for (int i = 0; i < nAccounts; ++i)
				acctsByChartP[i] = i;
			allElementsP = new int[chartElements.size()];
			for (int i = 0; i < chartElements.size(); ++i)
				allElementsP[i] = i;

			// Check for account descriptions beginning with a digit
			boolean titleError = false;
			for (int i = 0; i < acctsByDescrP.length; ++i)
			{
				Account account = (Account) accounts.elementAt(i);
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
			if (plAccount == null)
			{
				reportError("No P&L Account Defined in this chart");
				throw new VLException(VLException.CHART_FORMAT_ERROR, "No P/L Account");
			}
			acctNumbers = null; // basura
			descrs = null; // basura
			return;
		} catch (ParserConfigurationException | SAXException xl)
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
		ChartElement2 chartElement = new ChartElement2(eName, level, elementCount);
		logger.logDebug("start element[" + elementCount + "]: " + eName);
		for (int i = 0; i < attrs.getLength(); ++i)
		{
			attrs.getLocalName(i);
			chartElement.putAttribute(attrs.getLocalName(i), attrs.getValue(i));
			logger.logDebug("   " + attrs.getLocalName(i) + "=" + attrs.getValue(i));
		}

		// Now process the individual element types

		if (eName.equals("chart"))
		{ // START CHART
			conum = attrs.getValue("conum");
			coname = attrs.getValue("title");
			showacct = attrs.getValue("showacct").equalsIgnoreCase("Y");
			indent = Integer.parseInt(attrs.getValue("indent"));
			dollarFormat = attrs.getValue("edit");
			dateFormat = attrs.getValue("date");
			chartVersion = attrs.getValue("version");
			// chartElements.addElement(chartElement);
			++level;
			logger.logDebug("   incrementing level to " + level);
		} else if (eName.equals("section"))
		{ // START SECTION
			String title;
			title = attrs.getValue("title");
			if (title.length() > maxSectionTitleLength)
			{
				maxSectionTitle = title;
				maxSectionTitleLength = title.length();
				++level;
				logger.logDebug("   incrementing level to " + level);
			}

		} else if (eName.equals("group"))
		{ // START GROUP
			String title = attrs.getValue("title");
			// currentGroupTitle = title;
			if (title.length() > maxGroupTitleLength)
			{
				maxGroupTitle = title;
				maxGroupTitleLength = title.length();
			}
			++level;
			logger.logDebug("   incrementing level to " + level);

		} else if (eName.equals("account"))
		{ // START ACCOUNT
			logger.logDebug("Creating Account: " + attrs.getValue("no") + " '" + attrs.getValue("title") + "'");
			if (findAcctByNo(attrs.getValue("no")) != null)
				; // !! This is a duplicate account number, but
					// we can't throw an exception here
			Account account = new Account(attrs.getValue("no"), Strings.format(level, "00"), attrs.getValue("type"),
					attrs.getValue("title"), elementCount);
			// 'type' should be renamed 'class'
			accounts.addElement(account);
			// chartElements.addElement(chartElement);
			String title = attrs.getValue("title");
			if (title.length() > maxAccountTitleLength)
			{
				maxAccountTitle = title;
				maxAccountTitleLength = title.length();
			}
			String no = attrs.getValue("no");
			if (no.length() > maxAccountNoLength)
			{
				maxAccountNo = no;
				maxAccountNoLength = no.length();
			}

			if (level > maxLevel)
				maxLevel = level;
			if (attrs.getValue("type").equals("R"))
			{
				if (plAccount == null)
					plAccount = account;
				else
					reportError("Multiple P/L Accounts are defined.");
			}

		} else if (eName.equals("total"))
		{ // START TOTAL
			String title = attrs.getValue("title");
			if (title.length() > maxTotalTitleLength)
			{
				maxTotalTitle = title;
				maxTotalTitleLength = title.length();
			}

		} else
		{ // UNEXPECTED ELEMENT NAME - IGNORE
			logger.log("unexpected element name: " + eName);
			return;
		}

		// Finally add this element to the list
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
			// elementList = new ChartElement2[chartElements.size()];
			// Show the last element (if debug)
			logger.logDebug("lastElement: [" + chartElements.size() + "]=" + chartElements.lastElement().toString());
			// Copy all ChartElement's from chartElements to elementList
			if (chartElements == null)
				logger.logFatal("Chart:253: chartElements is null!");
			// if (elementList == null)
			// logger.logFatal("Chart:255: elementList is null!");
			// for (int i = 0; i < chartElements.size(); i++)
			// elementList[i] = chartElements.get(i);
			--level;
			logger.logDebug("   decrementing level to " + level);
			logger.logDebug(chartElements.size() + " ChartElements created");
			if (level != 0)
				logger.logFatal("Chart: Final Level is " + level);
		}
	}

	public Vector<ChartElement2> getChartElements()
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
		// !! This won't properly locate an account for which the description begins
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

	// public void clearAccountBalances()
	// {
	// for (int i = 0; i < accounts.size(); ++i)
	// {
	// Account account = (Account) accounts.elementAt(i);
	// account.zeroBalances();
	// }
	// // // Added 1-13-04 to zero balances in 'S' and 'T' entries:
	// // for (int i=0; i<stmtTemplate.size(); ++i)
	// // {
	// // Account acct = (Account)stmtTemplate.elementAt( i );
	// // acct.zeroBalances();
	// // }
	// }
	//
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
		for (int i = 0; i < accounts.size(); ++i)
		{
			StringTokenizer st = new StringTokenizer(((Account) accounts.elementAt(i)).getAccountNo(), "[]");
			if (st.hasMoreElements())
				numbers.addElement((String) st.nextElement());
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

	// public Vector<ChartElements> getChartElements()
	// {
	// ChartElement2 elementList[] = new ChartElement2[chartElements.size()];
	// for (int i = 0; i < elementList.length; ++i)
	// elementList[i] = (ChartElement2) chartElements.elementAt(i);
	// return elementList;
	// }
	//
	public Account getPLAccount()
	{
		return plAccount;
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

	public boolean setShowacct(boolean showacct)
	{
		boolean old = this.showacct;
		this.showacct = showacct;
		return old;
	}

	public boolean getShowacct()
	{
		return showacct;
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

	public String getDateFormat()
	{
		return dateFormat;
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

	public int getMaxDescrLength()
	{
		return maxAccountTitle.length();
	}

	public String getMaxDescr()
	{
		return maxAccountTitle;
	}

	public int getMaxAccountTitleLength()
	{
		return maxAccountTitle.length();
	}

	public int getMaxLevel()
	{
		return maxLevel;
	}

	public int getMaxAcctNoLength()
	{
		return maxAccountNo.length();
	}

	public String getMaxAcctNo()
	{
		return maxAccountNo;
	}

	public String getMaxGroupTitle()
	{
		return maxGroupTitle;
	}

	public String getFileName()
	{
		return chartFilename;
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
	// public Enumeration acctsByNumber()
	// {
	// enumP = acctsByNumberP;
	// enumerationIndex = 0;
	// enumeratingAccounts = true;
	// enumeratingElements = false;
	// return (Enumeration) this;
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

	// public String nextElement()
	// {
	// if (enumerationIndex >= enumP.length)
	// throw new NoSuchElementException();
	// if (enumeratingAccounts)
	// return accounts.elementAt(enumP[enumerationIndex++]);
	// else if (enumeratingElements)
	// return chartElements.elementAt(enumerationIndex++);
	// else
	// return null;
	// }
	//
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

	// private void reportInfo(String msg)
	// {
	// errorReport += msg + "\n";
	// }
	//
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
		return errorReport + "No. of errors = " + nErrors + "\n";
	}

	public void addToBeginBal(long amount)
	{
		beginBal += amount;
		logger.logDebug("Chart adding " + amount + " to beginBal resulting in " + beginBal);
	}

	public void addToDeltaBal(long amount)
	{
		deltaBal += amount;
		logger.logDebug("Chart adding " + amount + " to deltaBal resulting in " + deltaBal);
	}

	public long getBeginBal()
	{
		return beginBal;
	}

	public long getDeltaBal()
	{
		return deltaBal;
	}

	public long getTotalBal()
	{
		return beginBal + deltaBal;
	}

	/*****
	 * FOR TESTING ***** public static void main(String[] args) { Chart chart =
	 * null; try { Clip clip = new Clip(args, new String[] {
	 * "in=G:\\ACCOUNTING\\JMS\\ARCHIVES\\GL06\\CHART.XML" }); LogFile logger = new
	 * LogFile(); // For debugging: logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
	 * chart = new Chart(); chart.init(clip.getParam("in"), logger);
	 * 
	 * Enumeration<Account> accounts;
	 * com.extant.utilities.Console.println("*****ACCOUNTS BY ACCOUNT NUMBER*****");
	 * accounts = chart.acctsByNumber(); while (accounts.hasMoreElements())
	 * com.extant.utilities.Console.println(((Account)
	 * accounts.nextElement()).toString());
	 * 
	 * com.extant.utilities.Console.println("*****ACCOUNTS BY DESCRIPTION*****");
	 * accounts = chart.acctsByDescr(); while (accounts.hasMoreElements())
	 * com.extant.utilities.Console.println(((Account)
	 * accounts.nextElement()).toString());
	 * 
	 * com.extant.utilities.Console.println("*****STATEMENT ELEMENTS*****"); //
	 * Enumeration ChartElement2 elements = chart.chartElements(); // ChartElement2
	 * element; // while (elements.hasMoreElements()) { // element = (ChartElement)
	 * elements.nextElement(); //
	 * com.extant.utilities.Console.println(element.toString()); }
	 * com.extant.utilities.Console.println("maxLevel=" + chart.getMaxLevel());
	 * com.extant.utilities.Console .println("maxAcctNoLength=" +
	 * chart.getMaxAcctNoLength() + " '" + chart.getMaxAcctNo() + "'");
	 * com.extant.utilities.Console .println("maxDescr=" + chart.getMaxDescrLength()
	 * + " '" + chart.getMaxDescr() + "'");
	 * 
	 * com.extant.utilities.Console.println("Chart initialization complete - no
	 * errors found"); } catch (UtilitiesException ux) {
	 * com.extant.utilities.Console.println(ux.getMessage()); } catch (Throwable x)
	 * { if (chart != null)
	 * com.extant.utilities.Console.println(chart.getErrorReport()); //
	 * x.printStackTrace(); com.extant.utilities.Console.println(x.getMessage()); }
	 * }
	 * 
	 * /***** END OF TEST
	 *****/

	String chartFilename;
	LogFile logger;
	int nErrors;
	String errorReport = "";
	int level;
	// ChartElement2[] elementList;
	Vector<ChartElement2> chartElements; // Replaces ChartElement2[]
	private int elementCount;
	Vector<Account> accounts;
	private int nAccounts;
	private int acctsByChartP[];
	private int acctsByNumberP[];
	private int acctsByDescrP[];
	private int allElementsP[];
	private int enumP[];
	// private boolean enumeratingAccounts;
	// private boolean enumeratingElements;
	// private String currentGroupTitle;
	private String conum;
	private String coname;
	private boolean showacct;
	private int indent;
	private String dollarFormat = ",)"; // Default
	private String dateFormat = "mmmm dd, yyyy"; // Default
	private String shortDateFormat = "mm/mm/dddd";
	private String chartVersion = "3.1"; // Default
	private Account plAccount = null;
	private int enumerationIndex;
	int maxLevel = 0;

	String maxSectionTitle = "";
	String maxGroupTitle = "";
	String maxAccountNo = "";
	String maxAccountTitle = "";
	String maxTotalTitle = "";

	int maxSectionTitleLength = 0;
	int maxGroupTitleLength = 0;
	int maxAccountNoLength = 0;
	int maxAccountTitleLength = 0;
	int maxTotalTitleLength = 0;

	long beginBal = 0L;
	long deltaBal = 0L;
}
