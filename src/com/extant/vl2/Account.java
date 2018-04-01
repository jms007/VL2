/*
 * Account.java
 *
 * Created on November 9, 2002, 12:40 PM
 */

package com.extant.vl2;

import com.extant.utilities.*;
import java.util.Vector;

/**
 *
 * @author jms
 */
public class Account
{
	String accountNo;
	int accountLevel;
	// String accountType; // Type of Account: (All obsolete and not used, I think)
	// // * - Comment
	// // A - G/L Account
	// // S - Subtotal
	// // T - Total (formerly: Transfer total up 1 level)
	// // C - Caption
	// // F - Formatting Instruction
	// // V - Version of chart
	String accountType; // Type of Account:
						// A - Asset
						// L - Liability
						// I - Income
						// E - Expense
						// R - Retained Earnings/Net Worth
	String title;
	private long beginBal = 0L;
	private long deltaBal = 0L;
	Vector<GLEntry> glEntries = new Vector<GLEntry>(100, 100);
	LogFile logger = VL2.logger;

	public Account(String accountNo, String accountLevel, String accountType, String title)
	{
		this.accountNo = accountNo;
		if (this.accountNo == null)
			logger.logFatal("Account constructor accountNo is null");
		this.accountLevel = com.extant.utilities.Strings.parseInt(accountLevel);
		this.accountType = accountType;
		this.title = title;
	}

	// public Account
	// ( String accountNo
	// , String subAccount
	// , String accountLevel
	// , String accountType
	// , String accountClass
	// , String descr
	// )
	// {
	// this.accountNo = Strings.trim( accountNo, " " );
	// this.subAccount = Strings.trim( subAccount, " " );
	// this.accountLevel = Strings.parseInt( accountLevel );
	// this.accountType = accountType;
	// this.accountClass = accountClass;
	// this.descr = descr;
	// this.normalizedAcctNo = buildNormalizedAcctNo();
	// }

	/*
	 * a normalized account number is of the form <account> -or-
	 * <account>/<subAccount> (no brackets, no whitespace)
	 */
	// private String buildNormalizedAcctNo()
	// {
	// String a = Strings.trim( accountNo, " " );
	// if ( subAccount.length() > 0 ) a += "/" + Strings.trim( subAccount, " " );
	// return a;
	// }
	//
	// public String getNormalizedAcctNo()
	// {
	// return buildNormalizedAcctNo();
	// }
	//
	public String getAccountNo()
	{
		return accountNo;
	}

	// public String getSubAccount()
	// {
	// return subAccount;
	// }
	//
	public String getFullAccountNo()
	{
		return accountNo;
	}

	public int getLevel()
	{
		return accountLevel;
	}

	public void setLevel(int level)
	{
		accountLevel = level;
	}

	public String getType()
	{
		return accountType;
	}

	// public String getAcctClass()
	// {
	// return accountClass;
	// }
	//
	public String getTitle()
	{
		return title;
	}

	public long getBeginBal()
	{
		return beginBal;
	}

	public long getDeltaBal()
	{
		return deltaBal;
	}

	public long getEndBal()
	{
		return beginBal + deltaBal;
	}

	public void zeroBalances()
	{
		beginBal = deltaBal = 0L;
		// glEntries.removeAllElements();
	}

	public void removeGLEntries()
	{
		glEntries.removeAllElements();
	}

	// The next 2 methods (addToBal and updateBalances) are not currently (5-10-03)
	// used
	// and have not been tested.
	public void addToBal(Julian begin, Julian end, long value, Julian trDate)
	{
		if (trDate.isLaterThan(end))
			return;
		if (trDate.isEarlierThan(begin))
			addToBeginBal(value);
		else
			addToDeltaBal(value);
	}

	public void updateBalances(Julian begin, Julian end)
	{
		zeroBalances();
		for (int i = 0; i < glEntries.size(); ++i)
		{
			GLEntry glEntry = (GLEntry) glEntries.elementAt(i);
			addToBal(begin, end, glEntry.getSignedAmount(), glEntry.getFixedJulianDate());
		}
	}

	public void addToBeginBal(long value)
	{
		beginBal += value;
	}

	public void addToDeltaBal(long value)
	{
		deltaBal += value;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	// public String setProperty( String key, String value )
	// {
	// return (String)props.setProperty( key, value );
	// }
	//
	// public String getProperty( String key )
	// {
	// return props.getProperty( key, "" );
	// }
	//
	public void addGLEntry(GLEntry glEntry)
	{
		glEntries.addElement(glEntry);
	}

	public String toString()
	{
		return accountNo + " " + title + " begin=" + com.extant.utilities.Strings.formatPennies(beginBal) + " delta="
				+ com.extant.utilities.Strings.formatPennies(deltaBal);
	}
}
