/*
 * GLEntry.java
 *
 * Builds one variable-length (tokenized) GLEntry from the given data.
 * Interprets a GLEntry to make its individual fields available.
 * Validates a GLEntry and verifies accounts with the given Chart.
 *
 * Created on November 7, 2002, 4:06 PM
 */

package com.extant.vl2;

import com.extant.utilities.Julian;
import com.extant.utilities.Strings;
import com.extant.utilities.LogFile;
//import java.util.StringTokenizer;
//import java.util.regex.Pattern;

/**
 *
 * @author jms
 */

public class GLEntry
{
	// Constructs an empty GLEntry
	public GLEntry()
	{
	}

	// Constructs a GLEntry from its token (variable-length) String form
	public GLEntry(String image) throws VLException
	{
		logger = VL2.logger;
		if (logger == null)
			logger = new LogFile();
		fields = image.split("\\|");
		if (fields.length != nFields)
			throw new VLException(VLException.INVALID_NO_FIELDS, String.valueOf(fields.length));
		validate();
	}

	// Constructs a GLEntry from these parameters
	public GLEntry(String jRef, String GSN, String status, String drcr, String conum, String account, String amount,
			String descr, String date // yymmdd
	)
	{
		logger = VL2.logger;
		if (logger == null)
			logger = new LogFile();
		// logger.setLogLevel(LogFile.DEBUG_LOG_LEVEL);
		setField("JREF", jRef);
		setField("GSN", GSN);
		setField("STATUS", status);
		setField("DRCR", drcr);
		setField("CONUM", conum);
		setField("ACCOUNT", account);
		setField("AMOUNT", amount);
		setField("DESCR", descr);
		setDate(date);
	}

	public int getIndex(String fieldName)
	{
		for (int i = 0; i < nFields; ++i)
			if (fieldName.equalsIgnoreCase(fieldNames[i]))
				return i;
		return -1;
	}

	public String getField(String fieldName)
	{
		int index = getIndex(fieldName);
		if (index == -1)
			return null;
		return fields[index];
	}

	public void setField(String fieldName, String value)
	{
		for (int i = 0; i < nFields; ++i)
			if (fieldNames[i].equalsIgnoreCase(fieldName))
			{
				fields[i] = value;
				logger.logDebug("setField sets " + fieldNames[i] + " to '" + fields[i] + "'");
				return;
			}
	}

	public boolean isCredit()
	{
		return getField("DRCR").equals("C");
	}

	public boolean isDebit()
	{
		return getField("DRCR").equals("D");
	}

	public String getNormalizedAccountNo()
	{
		return getAccountNo();
	}

	public String getAccountNo()
	{ // returns <account> -or- <account>/<subAccount>
		String na = Strings.trim(getField("account"), " ");
		// logger.logDebug("GLEntry:accountNo=" + na);
		return na;
	}

	// public void setNormalizedAccountNo( String normalizedAccountNo )
	// {
	// // normalizedAccountNo can be any of the following:
	// // <account> or <account>/<subAccount>
	// // [<account>] or [<account>/<subAccount>]
	// // [<account>]<description> or [<account>/<subAccount>]<description>
	// String account = normalizedAccountNo;
	// String subAccount = "";
	// if ( normalizedAccountNo.startsWith( "[" ) )
	// {
	// StringTokenizer st = new StringTokenizer( account, "[]" );
	// account = (String)st.nextElement();
	// }
	// int p = account.indexOf( "/" );
	// if ( p >= 0 )
	// {
	// subAccount = account.substring( p+1 );
	// account = account.substring( 0, p );
	// }
	// setField( "account", account );
	// setField( "subAccount", subAccount );
	// }

	public long getLongAmount()
	{
		return Strings.parsePennies(getField("Amount"));
	}

	public long getSignedAmount()
	{
		if (this.isCredit())
			return -Strings.parsePennies(getField("Amount"));
		else
			return Strings.parsePennies(getField("Amount"));
	}

	public void setAmount(long pennies)
	{
		if (pennies < 0)
			pennies = -pennies;
		setField("DRCR", "C");
		setField("amount", Strings.formatPennies(pennies, ""));
	}

	public String switchDRCR()
	{
		if (getField("DRCR").equals("C"))
			setField("DRCR", "D");
		else
			setField("DRCR", "C");
		return getField("DRCR");
	}

	public Julian getJulianDate()
	{
		String sdate = getField("DATE");
		return new Julian(sdate);
		// String yy = fields[9].substring( 0, 2 );
		// String mm = fields[9].substring( 2, 4 );
		// String dd = fields[9].substring( 4, 6 );
		// return new Julian( mm + "-" + dd + "-" + yy );
		// return VLUtil.fixDate(getField("DATE"));
	}

	// public Julian getFixedJulianDate()
	// {
	// return VLUtil.fixDate(getField("DATE"));
	// }
	//
	public void setDate(Julian jDate)
	{
		setField("DATE", jDate.toString("yymmdd"));
	}

	public void setDate(String yymmdd)
	{
		setField("DATE", yymmdd);
	}

	public String getDescr()
	{
		return getField("DESCR");
	}

	public int getDescrLength()
	{
		return getDescr().length();
	}

	public String buildRecord()
	{
		String record = "";
		for (int i = 0; i < nFields; ++i)
			record += fields[i] + "|";
		return record;
	}

	public boolean validate() throws VLException
	{
		// Try regex
		// String javaPattern =
		// "[A-Z]{4}\\|0\\|E\\|[CD]\\|100\\|[\\d]{4}(/.*){0,1}\\|\\d*\\.\\d\\d\\|.*\\|\\d{6}";
		// TODO String regexPattern = "[(CR)|(CD)]\d\d|\d+|E|[CD]\|100\|
		// String test = buildRecord();
		// boolean match = Pattern.matches( javaPattern, test );
		// if (!match)
		// throw new VLException( VLException.GL_ERRORS );
		if (!getField("DRCR").equals("D") && !getField("DRCR").equals("C"))
			throw new VLException(VLException.INVALID_DRCR, getField("DRCR"));
		if (!Strings.isValidFloat(getField("Amount")))
			throw new VLException(VLException.INVALID_AMOUNT, getField("Amount"));
		if (getField("Amount").contains("-"))
			throw new VLException(VLException.INVALID_AMOUNT, getField("Amount"));
		if (getField("ACCOUNT").trim().contains(" "))
			throw new VLException(VLException.SPACE_IN_ACCOUNT, getField("ACCOUNT"));
		return true;
	}

	public static int getFieldIndex(String fieldName)
	{
		for (int i = 0; i < nFields; ++i)
			if (fieldNames[i].equalsIgnoreCase(fieldName))
				return i;
		return -1;
	}

	@Override
	public String toString()
	{
		return buildRecord();
	}

	/*****
	 * FOR TESTING **** public static void main( String[] args ) { try { String
	 * fieldName; //String image = "TEMP 0ED100100 ENCIRQ 29512.39TEMP BAL
	 * FWD.................020101"; String image =
	 * "TEMP|0|E|D|100|100|ENCIRQ|29512.39|TEMP BAL FWD|020101"; GLEntry glEntry =
	 * new GLEntry( image ); for (int i=0; i<fieldNames.length; ++i) { fieldName =
	 * GLEntry.fieldNames[i]; Console.println( fieldName + "=" + glEntry.getField(
	 * fieldName ) ); } Console.println( "pennies=" + glEntry.getLongAmount() );
	 * Console.println( "JDate=" + glEntry.getJulianDate().toString( "mm-dd-yyyy" )
	 * ); Console.println( "'" + glEntry.buildRecord() + "'" ); Console.println(
	 * "valid=" + glEntry.validate() ); } catch (VLException vlx) { Console.println(
	 * vlx.getMessage() ); } }
	 * 
	 * /
	 *****/

	/*
	 * Format of (old) fixed-length general ledger file records. The fields are in
	 * the same and in the same order EXCEPT (a) subAccount has been removed
	 * (combined with account), and (b) GSN has been added to the format. The fields
	 * are separated in the new format by the '|' delimiter. These column numbers
	 * are obviously not relevant to the variable-length format. 0 1 2 3 4 5 6 7
	 * 012345678901234567890123456789012345678901234567890123456789012345678901234567
	 * TEMP 0ED100100.......ENCIRQ....12345678.99TEMP BAL FWD.................020100
	 * 
	 * Index Col Field 0 0 - 3 Journal Reference 1 4 - 6 GSN 2 7 - 7 Status 3 8 - 8
	 * Debit/Credit 4 9 - 11 Company Number 5 12 - 21 Account 6 22 - 31 SubAccount 7
	 * 32 - 42 Amount 8 43 - 71 Description 9 72 - 77 Date (yymmdd)
	 */

	// public final static String JREF = "jRef";
	// public final static String SEQUENCE = "sequence";
	// public final static String STATUS = "status";
	// public final static String DRCR = "drcr";
	// public final static String CONUM = "conum";
	// public final static String ACCOUNT = "account";
	// public final static String AMOUNT = "amount";
	// public final static String DESCR = "descr";
	// public final static String DATE = "date";

	public final static String[] fieldNames = new String[] { "JREF", "GSN", "STATUS", "DRCR", "CONUM", "ACCOUNT",
			"AMOUNT", "DESCR", "DATE" };
	public final static int nFields = fieldNames.length;
	// public final static int[] pStart = new int[] {0, 4, 7, 8, 9, 12, 22, 32, 43,
	// 72};
	// public final static int[] pEnd = new int[] {3, 6, 7, 8, 11, 21, 31, 42, 71,
	// 77};
	String[] fields = new String[nFields];
	LogFile logger;
}
