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
	int elementIndex;

	Vector<GLEntry> glEntries = new Vector<GLEntry>(100, 100);
	LogFile logger = VL2.logger;

	public Account(String accountNo, String accountLevel, String accountType, String title, int elementIndex)
	{
		this.accountNo = accountNo;
		if (this.accountNo == null)
			logger.logFatal("Account constructor accountNo is null");
		this.accountLevel = com.extant.utilities.Strings.parseInt(accountLevel);
		this.accountType = accountType;
		this.title = title;
		this.elementIndex = elementIndex;
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

	public int getAccountElementIndex()
	{
		return elementIndex;
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

	public void setTitle(String title)
	{
		this.title = title;
	}

	// public void zeroBalances()
	// {
	// beginBal = deltaBal = 0L;
	// // glEntries.removeAllElements();
	// }
	//
	public void removeGLEntries()
	{
		glEntries.removeAllElements();
	}

	// public void addToBal(GLEntry glEntry)
	// {
	// long amount = glEntry.getSignedAmount();
	// if (glEntry.getField("JREF").equalsIgnoreCase("BALF"))
	// addToBeginBal(amount);
	// else
	// addToDeltaBal(amount);
	// }
	//
	// public void addToBeginBal(long value)
	// {
	// beginBal += value;
	// }
	//
	// public void addToDeltaBal(long value)
	// {
	// deltaBal += value;
	// }
	//
	// public long getBeginBal()
	// {
	// return beginBal;
	// }
	//
	// public long getDeltaBal()
	// {
	// return deltaBal;
	// }
	//
	// public long getEndBal()
	// {
	// return beginBal + deltaBal;
	// }
	//
	public void addGLEntry(GLEntry glEntry)
	{
		glEntries.addElement(glEntry);
	}

	public String toString()
	{
		return accountNo + " " + title;
	}
}
