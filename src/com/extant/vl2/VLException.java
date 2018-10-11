/*
 * VLException.java
 *
 * Created on November 7, 2002, 3:56 PM
 */

package com.extant.vl2;

/**
 *
 * @author jms
 */
@SuppressWarnings("serial")
public class VLException extends java.lang.Exception
{
	int code;
	public static String Messages[] = { "Unknown VL Error: ", "Invalid Field Length: ", "DRCR Field is not valid: ",
			"Amount Field is not valid: ", "Account Number contains imbedded space: ",
			"Transaction Date is not valid: ", "GL entries contain errors", "Internal Error: ",
			"Error(s) in Chart File ", "File Not Found: ", "Retained Earnings Account not found",
			"Account not in Chart: ", "Chart contains duplicate Accounts", "Incorrect Version of Chart: ",
			"Unrecognized source for EFT file: ", "Syntax error in CSV file: ", "IO Error while processing file ",
			"Basic/Persistent Data Services Unavailable", "Invalid Record Length ", "Inconsistent Dates: ",
			"Table Contains Data: ", ".ini file not found in ", "More than one .ini file in ",
			"Chart does not contain CashAcct: ", "VLInit Abnormal Exit ", "invalid set: ", "Trace ",
			"BadElementException ", "DocumentException ", "Invalid Argument: ", "WriteException ",
			"Initialization Canceled by User", "Unable to initialize XML Chart", "Operation incompatible with element ",
			"Unable to create directory ", "Directory is not empty ", "Database contains tables for year ",
			"Incorrect number of fields: ", "Transaction not balanced ", "No Account selected ", "logger is null" };
	public static final int UNKNOWN = 0;
	public static final int INVALID_NO_FIELDS = 1;
	public static final int INVALID_DRCR = 2;
	public static final int INVALID_AMOUNT = 3;
	public static final int SPACE_IN_ACCOUNT = 4;
	public static final int INVALID_DATE = 5;
	public static final int GL_ERRORS = 6;
	public static final int INTERNAL_ERROR = 7;
	public static final int CHART_FORMAT_ERROR = 8;
	public static final int NO_FILE = 9;
	public static final int NO_RE_ACCOUNT = 10;
	public static final int ACCT_NOT_IN_CHART = 11;
	public static final int DUP_ACCOUNTS = 12;
	public static final int CHART_VERSION = 13;
	public static final int EFT_SOURCE = 14;
	public static final int CSV_SYNTAX = 15;
	public static final int IOX = 16;
	public static final int UNABLE_SERVICES = 17;
	public static final int INVALID_RECORD_LENGTH = 18;
	public static final int INCONSISTENT_DATES = 19;
	public static final int TABLE_CONTAINS_DATA = 20;
	public static final int NO_INI_FILE = 21;
	public static final int TOO_MANY_INI = 22;
	public static final int CASH_NOT_IN_CHART = 23;
	public static final int INIT_FAILED = 24;
	public static final int INVOICE_DETAIL_ERR = 25;
	public static final int TRACE = 26;
	public static final int BAD_ELEMENT = 27;
	public static final int DOCUMENT = 28;
	public static final int INVALID_ARGUMENT = 29;
	public static final int WRITEX = 30;
	public static final int INIT_CANCEL = 31;
	public static final int CHART_INIT = 32;
	public static final int INCOMPATIBLE_ACCOUNT = 33;
	public static final int UNABLE_DIR = 34;
	public static final int DIR_NOT_EMPTY = 35;
	public static final int DB_YEAR_NOT_EMPTY = 36;
	public static final int INVALID_FIELD_COUNT = 37;
	public static final int OUT_OF_BALANCE = 38;
	public static final int NO_ACCOUNT_SELECTED = 39;
	public static final int NULL_LOGGER = 40;

	public VLException(int i)
	{
		this(i, "");
	}

	public VLException(int i, String message)
	{
		super(((i > Messages.length || i < 0) ? Messages[i = UNKNOWN] : Messages[i]) + message);
		code = i;
	}

	public int getErrorCode()
	{
		return code;
	}

}
